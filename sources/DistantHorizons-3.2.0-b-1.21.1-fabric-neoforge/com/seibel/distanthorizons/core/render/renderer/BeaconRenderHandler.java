package com.seibel.distanthorizons.core.render.renderer;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiBlockMaterial;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiCustomRenderObjectFactory;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiRenderableBoxGroup;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3d;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBox;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBoxGroupShading;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.sql.dto.BeaconBeamDTO;
import com.seibel.distanthorizons.core.util.RenderUtil;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;

public class BeaconRenderHandler {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final IDhApiCustomRenderObjectFactory GENERIC_OBJECT_FACTORY = SingletonInjector.INSTANCE.get(IDhApiCustomRenderObjectFactory.class);
   private static final int MAX_CULLING_FREQUENCY_IN_MS = 1000;
   private final ReentrantLock updateLock = new ReentrantLock();
   private final IDhApiRenderableBoxGroup activeBeaconBoxRenderGroup;
   private final ArrayList<DhApiRenderableBox> fullBeaconBoxList = new ArrayList<>();
   private boolean cullingThreadRunning = false;
   private boolean updateRenderDataNextFrame = false;

   public BeaconRenderHandler(@NotNull IDhGenericRenderer renderer) {
      this.activeBeaconBoxRenderGroup = GENERIC_OBJECT_FACTORY.createAbsolutePositionedGroup("DistantHorizons:Beacons", new ArrayList<>(0));
      this.activeBeaconBoxRenderGroup.setBlockLight(15);
      this.activeBeaconBoxRenderGroup.setSkyLight(15);
      this.activeBeaconBoxRenderGroup.setSsaoEnabled(false);
      this.activeBeaconBoxRenderGroup.setShading(DhApiRenderableBoxGroupShading.getUnshaded());
      this.activeBeaconBoxRenderGroup.setPreRenderFunc(this::beforeRender);
      renderer.add(this.activeBeaconBoxRenderGroup);
   }

   private void beforeRender(DhApiRenderParam renderEventParam) {
      if (Config.Client.Advanced.Graphics.Culling.disableBeaconDistanceCulling.get()) {
         this.tryUpdateBeaconCullingAsync();
      }

      if (this.updateRenderDataNextFrame) {
         this.activeBeaconBoxRenderGroup.triggerBoxChange();
         this.updateRenderDataNextFrame = false;
      }

      this.activeBeaconBoxRenderGroup.setActive(Config.Client.Advanced.Graphics.GenericRendering.enableBeaconRendering.get());
   }

   private void tryUpdateBeaconCullingAsync() {
      ThreadPoolExecutor executor = ThreadPoolUtil.getBeaconCullingExecutor();
      if (executor != null && !this.cullingThreadRunning) {
         this.cullingThreadRunning = true;

         try {
            executor.execute(() -> {
               try {
                  Thread.sleep(1000L);
               } catch (InterruptedException var11) {
               }

               try {
                  this.updateLock.lock();
                  DhVec3d cameraPos = MC_RENDER.getCameraExactPosition();
                  float dhFadeDistance = RenderUtil.getNearClipPlaneInBlocks();
                  this.activeBeaconBoxRenderGroup.clear();

                  for (DhApiRenderableBox box : this.fullBeaconBoxList) {
                     double distance = DhVec3d.getHorizontalDistance(cameraPos, box.minPos);
                     if (distance > dhFadeDistance) {
                        this.activeBeaconBoxRenderGroup.add(box);
                     }
                  }

                  this.updateRenderDataNextFrame = true;
               } catch (Exception var12) {
                  LOGGER.error("Unexpected issue while updating beacon culling. Error: " + var12.getMessage(), var12);
               } finally {
                  this.updateLock.unlock();
                  this.cullingThreadRunning = false;
               }
            });
         } catch (RejectedExecutionException var3) {
         }
      }
   }

   public void replaceRenderingBeacons(ArrayList<BeaconRenderHandler.BeaconBeamWithWidth> beaconList) {
      try {
         this.updateLock.lock();
         ArrayList<BeaconRenderHandler.BeaconBeamWithWidth> sortedBeaconList = new ArrayList<>(beaconList);
         if (Config.Client.Advanced.Graphics.GenericRendering.expandDistantBeacons.get()) {
            sortedBeaconList.sort(BeaconRenderHandler.NegativeInfiniteBlockPosComparator.INSTANCE);

            for (int outerIndex = 0; outerIndex < sortedBeaconList.size(); outerIndex++) {
               BeaconRenderHandler.BeaconBeamWithWidth outerBeacon = sortedBeaconList.get(outerIndex);
               DhBlockPos outerBlockPos = outerBeacon.blockPos;

               for (int mergeIndex = outerIndex + 1; mergeIndex < sortedBeaconList.size(); mergeIndex++) {
                  BeaconRenderHandler.BeaconBeamWithWidth beaconToMerge = sortedBeaconList.get(mergeIndex);
                  DhBlockPos mergeBlockPos = beaconToMerge.blockPos;
                  int xDiff = mergeBlockPos.getX() - outerBlockPos.getX();
                  int zDiff = mergeBlockPos.getZ() - outerBlockPos.getZ();
                  if (xDiff < beaconToMerge.beaconBlockWidth && zDiff < beaconToMerge.beaconBlockWidth) {
                     sortedBeaconList.remove(mergeIndex);
                     mergeIndex--;
                  }
               }
            }
         }

         this.activeBeaconBoxRenderGroup.clear();
         this.fullBeaconBoxList.clear();

         for (int i = 0; i < sortedBeaconList.size(); i++) {
            BeaconRenderHandler.BeaconBeamWithWidth beacon = sortedBeaconList.get(i);
            int maxBeaconBeamHeight = Config.Client.Advanced.Graphics.GenericRendering.beaconRenderHeight.get();
            DhApiRenderableBox beaconBox = new DhApiRenderableBox(
               new DhApiVec3d(beacon.blockPos.getX(), beacon.blockPos.getY() + 1, beacon.blockPos.getZ()),
               new DhApiVec3d(beacon.blockPos.getX() + beacon.beaconBlockWidth, maxBeaconBeamHeight, beacon.blockPos.getZ() + beacon.beaconBlockWidth),
               beacon.color,
               EDhApiBlockMaterial.ILLUMINATED
            );
            this.activeBeaconBoxRenderGroup.add(beaconBox);
            this.fullBeaconBoxList.add(beaconBox);
         }

         this.activeBeaconBoxRenderGroup.triggerBoxChange();
      } finally {
         this.updateLock.unlock();
      }
   }

   public static class BeaconBeamWithWidth extends BeaconBeamDTO {
      public final int beaconBlockWidth;

      public BeaconBeamWithWidth(BeaconBeamDTO beaconBeamDTO, byte lodDetailLevel) {
         super(beaconBeamDTO.blockPos, beaconBeamDTO.color);
         if (Config.Client.Advanced.Graphics.GenericRendering.expandDistantBeacons.get()) {
            this.beaconBlockWidth = DhSectionPos.getBlockWidth(lodDetailLevel);
         } else {
            this.beaconBlockWidth = 1;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj != null && obj.getClass() == this.getClass()) {
            BeaconRenderHandler.BeaconBeamWithWidth that = (BeaconRenderHandler.BeaconBeamWithWidth)obj;
            return that.beaconBlockWidth != this.beaconBlockWidth ? false : super.equals(that);
         } else {
            return false;
         }
      }
   }

   public static class NegativeInfiniteBlockPosComparator implements Comparator<BeaconRenderHandler.BeaconBeamWithWidth> {
      public static final BeaconRenderHandler.NegativeInfiniteBlockPosComparator INSTANCE = new BeaconRenderHandler.NegativeInfiniteBlockPosComparator();

      public int compare(BeaconRenderHandler.BeaconBeamWithWidth beacon1, BeaconRenderHandler.BeaconBeamWithWidth beacon2) {
         DhBlockPos blockPos1 = beacon1.blockPos;
         DhBlockPos blockPos2 = beacon2.blockPos;
         return blockPos1.getX() != blockPos2.getX()
            ? Integer.compare(blockPos1.getX(), blockPos2.getX())
            : Integer.compare(blockPos1.getZ(), blockPos2.getZ());
      }
   }
}
