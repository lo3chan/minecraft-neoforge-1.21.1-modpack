package dev.tr7zw.entityculling;

import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import com.logisticscraft.occlusionculling.util.Vec3d;
import dev.tr7zw.entityculling.versionless.EntityCullingVersionlessBase;
import dev.tr7zw.entityculling.versionless.access.Cullable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CullTask implements Runnable {
   public boolean requestCull = false;
   public boolean disableEntityCulling = false;
   public boolean disableBlockEntityCulling = false;
   private final OcclusionCullingInstance culling;
   private final Minecraft client = Minecraft.getInstance();
   private final int sleepDelay;
   private final int hitboxLimit;
   private final Set<BlockEntityType<?>> blockEntityWhitelist;
   private final Set<EntityType<?>> entityWhistelist;
   public double lastTime;
   private Vec3d lastPos;
   private Vec3d aabbMin;
   private Vec3d aabbMax;
   private boolean ingame;
   private List<Entity> entitiesForRendering;
   private Map<BlockPos, BlockEntity> blockEntities;
   private Vec3 cameraMC;

   public CullTask(OcclusionCullingInstance culling, Set<BlockEntityType<?>> blockEntityWhitelist, Set<EntityType<?>> entityWhistelist) {
      this.sleepDelay = EntityCullingModBase.instance.config.sleepDelay;
      this.hitboxLimit = EntityCullingModBase.instance.config.hitboxLimit;
      this.lastTime = 0.0;
      this.lastPos = new Vec3d(0.0, 0.0, 0.0);
      this.aabbMin = new Vec3d(0.0, 0.0, 0.0);
      this.aabbMax = new Vec3d(0.0, 0.0, 0.0);
      this.ingame = false;
      this.entitiesForRendering = new ArrayList<>();
      this.blockEntities = new HashMap<>();
      this.cameraMC = new Vec3(0.0, 0.0, 0.0);
      this.culling = culling;
      this.blockEntityWhitelist = blockEntityWhitelist;
      this.entityWhistelist = entityWhistelist;
   }

   @Override
   public void run() {
      while (Minecraft.getInstance().isRunning()) {
         try {
            Thread.sleep(this.sleepDelay);
            if (!EntityCullingVersionlessBase.enabled || !this.ingame) {
               this.lastTime = 0.0;
            } else if (this.requestCull || this.cameraMC.x != this.lastPos.x || this.cameraMC.y != this.lastPos.y || this.cameraMC.z != this.lastPos.z) {
               long start = System.nanoTime();
               this.requestCull = false;
               this.lastPos.set(this.cameraMC.x, this.cameraMC.y, this.cameraMC.z);
               Vec3d camera = this.lastPos;
               this.culling.resetCache();
               this.cullBlockEntities(this.cameraMC, camera);
               this.cullEntities(this.cameraMC, camera);
               this.lastTime = (System.nanoTime() - start) / 1000000.0;
            }
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

      System.out.println("Shutting down culling task!");
   }

   private void cullEntities(Vec3 cameraMC, Vec3d camera) {
      if (!this.disableEntityCulling) {
         Entity entity = null;

         for (Entity var8 : this.entitiesForRendering) {
            if (var8 == null) {
               break;
            }

            if (var8 instanceof Cullable && !this.entityWhistelist.contains(var8.getType()) && !EntityCullingModBase.instance.isDynamicWhitelisted(var8)) {
               Cullable cullable = (Cullable)var8;
               if (!cullable.isForcedVisible()) {
                  if (Minecraft.getInstance().shouldEntityAppearGlowing(var8)) {
                     cullable.setCulled(false);
                  } else if (!var8.position().closerThan(cameraMC, EntityCullingModBase.instance.config.tracingDistance)) {
                     cullable.setCulled(false);
                  } else {
                     AABB boundingBox = NMSCullingHelper.getCullingBox(var8);
                     if (boundingBox != null
                        && !(boundingBox.getXsize() > this.hitboxLimit)
                        && !(boundingBox.getYsize() > this.hitboxLimit)
                        && !(boundingBox.getZsize() > this.hitboxLimit)) {
                        this.aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
                        this.aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                        boolean visible = this.culling.isAABBVisible(this.aabbMin, this.aabbMax, camera);
                        cullable.setCulled(!visible);
                     } else {
                        cullable.setCulled(false);
                     }
                  }
               }
            }
         }
      }
   }

   private void cullBlockEntities(Vec3 cameraMC, Vec3d camera) {
      if (!this.disableBlockEntityCulling) {
         for (Entry<BlockPos, BlockEntity> entry : this.blockEntities.entrySet()) {
            try {
               ;
            } catch (ConcurrentModificationException | NullPointerException var9) {
               break;
            }

            if (entry == null) {
               break;
            }

            if (!this.blockEntityWhitelist.contains(entry.getValue().getType())
               && this.client.getBlockEntityRenderDispatcher().getRenderer(entry.getValue()) != null
               && !EntityCullingModBase.instance.isDynamicWhitelisted(entry.getValue())) {
               Cullable cullable = (Cullable)entry.getValue();
               if (!cullable.isForcedVisible()) {
                  BlockPos pos = entry.getKey();
                  if (closerThan(pos, cameraMC, 64.0)) {
                     AABB boundingBox = EntityCullingModBase.instance.setupAABB(entry.getValue(), pos);
                     if (!(boundingBox.getXsize() > this.hitboxLimit)
                        && !(boundingBox.getYsize() > this.hitboxLimit)
                        && !(boundingBox.getZsize() > this.hitboxLimit)) {
                        this.aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
                        this.aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                        boolean visible = this.culling.isAABBVisible(this.aabbMin, this.aabbMax, camera);
                        cullable.setCulled(!visible);
                     } else {
                        cullable.setCulled(false);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean closerThan(BlockPos blockPos, Position position, double d) {
      return distSqr(blockPos, position.x(), position.y(), position.z(), true) < d * d;
   }

   private static double distSqr(BlockPos blockPos, double d, double e, double f, boolean bl) {
      double g = bl ? 0.5 : 0.0;
      double h = blockPos.getX() + g - d;
      double i = blockPos.getY() + g - e;
      double j = blockPos.getZ() + g - f;
      return h * h + i * i + j * j;
   }

   @Generated
   public void setIngame(boolean ingame) {
      this.ingame = ingame;
   }

   @Generated
   public void setEntitiesForRendering(List<Entity> entitiesForRendering) {
      this.entitiesForRendering = entitiesForRendering;
   }

   @Generated
   public void setBlockEntities(Map<BlockPos, BlockEntity> blockEntities) {
      this.blockEntities = blockEntities;
   }

   @Generated
   public void setCameraMC(Vec3 cameraMC) {
      this.cameraMC = cameraMC;
   }
}
