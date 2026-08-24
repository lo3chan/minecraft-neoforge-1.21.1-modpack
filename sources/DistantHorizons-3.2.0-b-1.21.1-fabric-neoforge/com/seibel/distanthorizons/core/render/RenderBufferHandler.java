package com.seibel.distanthorizons.core.render;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiCullingFrustum;
import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiShadowCullingFrustum;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.LodBufferContainer;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.render.QuadTree.LodQuadTree;
import com.seibel.distanthorizons.core.render.QuadTree.LodRenderSection;
import com.seibel.distanthorizons.core.render.renderer.cullingFrustum.DhFrustumBounds;
import com.seibel.distanthorizons.core.render.renderer.cullingFrustum.NeverCullFrustum;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.math.DhMat4f;
import com.seibel.distanthorizons.core.util.objects.SortedArraySet;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IIrisAccessor;
import com.seibel.distanthorizons.coreapi.ModInfo;
import java.util.ArrayList;
import java.util.Iterator;
import org.joml.Matrix4f;

public class RenderBufferHandler implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final IIrisAccessor IRIS_ACCESSOR = ModAccessorInjector.INSTANCE.get(IIrisAccessor.class);
   private static final float[] JOML_TRANSPOSE_ARRAY = new float[16];
   private static final Matrix4f WORLD_VIEW_JOML_MATRIX = new Matrix4f();
   private static final Matrix4f WORLD_VIEW_PROJ_JOML_MATRIX = new Matrix4f();
   private static final DhMat4f FRUSTOM_DH_MATRIX = new DhMat4f();
   public final LodQuadTree lodQuadTree;
   private final SortedArraySet<LodBufferContainer> loadedNearToFarBuffers;
   private final ArrayList<LodRenderSection> tempProcessNodeList = new ArrayList<>();
   private int visibleBufferCount;
   private int culledBufferCount;
   private int shadowVisibleBufferCount;
   private int shadowCulledBufferCount;

   public RenderBufferHandler(LodQuadTree lodQuadTree) {
      this.lodQuadTree = lodQuadTree;
      IDhApiCullingFrustum coreCameraFrustum = DhApi.overrides.get(IDhApiCullingFrustum.class, -1);
      if (coreCameraFrustum == null) {
         DhApi.overrides.bind(IDhApiCullingFrustum.class, new DhFrustumBounds());
      }

      IDhApiShadowCullingFrustum coreShadowFrustum = DhApi.overrides.get(IDhApiShadowCullingFrustum.class, -1);
      if (coreShadowFrustum == null) {
         DhApi.overrides.bind(IDhApiShadowCullingFrustum.class, new NeverCullFrustum());
      }

      this.loadedNearToFarBuffers = new SortedArraySet<>(this::sortBufferContainersNearToFar);
   }

   private int sortBufferContainersNearToFar(LodBufferContainer loadedBufferA, LodBufferContainer loadedBufferB) {
      DhBlockPos2D aPos = DhSectionPos.getCenterBlockPos(loadedBufferA.pos);
      DhBlockPos2D bPos = DhSectionPos.getCenterBlockPos(loadedBufferB.pos);
      DhBlockPos2D centerPos = this.lodQuadTree.getCenterBlockPos();
      int aManhattanDistance = aPos.manhattanDist(centerPos);
      int bManhattanDistance = bPos.manhattanDist(centerPos);
      return aManhattanDistance - bManhattanDistance;
   }

   public void buildRenderList(RenderParams renderParams) {
      if (ModInfo.IS_DEV_BUILD && !RenderThreadTaskHandler.INSTANCE.isCurrentThread()) {
         LodUtil.assertNotReach("Should only be run on the render thread");
      }

      this.loadedNearToFarBuffers.clear();
      boolean isShadowPass = IRIS_ACCESSOR != null && IRIS_ACCESSOR.isRenderingShadowPass();
      boolean enableFrustumCulling;
      IDhApiCullingFrustum frustum;
      if (isShadowPass) {
         enableFrustumCulling = !Config.Client.Advanced.Graphics.Culling.disableShadowPassFrustumCulling.get();
         frustum = DhApi.overrides.get(IDhApiShadowCullingFrustum.class);
      } else {
         enableFrustumCulling = !Config.Client.Advanced.Graphics.Culling.disableFrustumCulling.get();
         frustum = DhApi.overrides.get(IDhApiCullingFrustum.class);
      }

      if (enableFrustumCulling) {
         int worldMinY = renderParams.clientLevelWrapper.getMinHeight();
         int worldHeight = renderParams.clientLevelWrapper.getMaxHeight();
         renderParams.mcModelViewMatrix.putValuesInArray(JOML_TRANSPOSE_ARRAY);
         WORLD_VIEW_JOML_MATRIX.setTransposed(JOML_TRANSPOSE_ARRAY)
            .translate(-((float)renderParams.exactCameraPosition.x), -((float)renderParams.exactCameraPosition.y), -((float)renderParams.exactCameraPosition.z));
         renderParams.dhProjectionMatrix.putValuesInArray(JOML_TRANSPOSE_ARRAY);
         WORLD_VIEW_PROJ_JOML_MATRIX.setTransposed(JOML_TRANSPOSE_ARRAY).mul(WORLD_VIEW_JOML_MATRIX);
         FRUSTOM_DH_MATRIX.set(WORLD_VIEW_PROJ_JOML_MATRIX);
         frustum.update(worldMinY, worldMinY + worldHeight, FRUSTOM_DH_MATRIX);
      }

      if (isShadowPass) {
         this.shadowCulledBufferCount = 0;
      } else {
         this.culledBufferCount = 0;
      }

      this.lodQuadTree.populateListWithEnabledRenderSections(this.tempProcessNodeList);
      Iterator var13 = this.tempProcessNodeList.iterator();

      while (true) {
         LodRenderSection renderSection;
         while (true) {
            if (!var13.hasNext()) {
               if (isShadowPass) {
                  this.shadowVisibleBufferCount = this.loadedNearToFarBuffers.size();
               } else {
                  this.visibleBufferCount = this.loadedNearToFarBuffers.size();
               }

               return;
            }

            renderSection = (LodRenderSection)var13.next();
            if (renderSection != null) {
               try {
                  if (!enableFrustumCulling) {
                     break;
                  }

                  int blockMinX = DhSectionPos.getMinCornerBlockX(renderSection.pos);
                  int blockMinZ = DhSectionPos.getMinCornerBlockZ(renderSection.pos);
                  int blockWidth = DhSectionPos.getBlockWidth(renderSection.pos);
                  byte detailLevel = DhSectionPos.getDetailLevel(renderSection.pos);
                  if (frustum.intersects(blockMinX, blockMinZ, blockWidth, detailLevel)) {
                     break;
                  }

                  if (isShadowPass) {
                     this.shadowCulledBufferCount++;
                  } else {
                     this.culledBufferCount++;
                  }
               } catch (Exception var12) {
                  LOGGER.error(
                     "Unexpected issue during culling for node pos: [" + DhSectionPos.toString(renderSection.pos) + "], error: [" + var12.getMessage() + "].",
                     var12
                  );
                  break;
               }
            }
         }

         try {
            LodBufferContainer bufferContainer = renderSection.renderBufferContainer;
            if (bufferContainer != null && renderSection.getRenderingEnabled()) {
               this.loadedNearToFarBuffers.add(bufferContainer);
            }
         } catch (Exception var11) {
            LOGGER.error(
               "Error updating QuadTree render source at [" + DhSectionPos.toString(renderSection.pos) + "], error: [" + var11.getMessage() + "].", var11
            );
         }
      }
   }

   public SortedArraySet<LodBufferContainer> getColumnRenderBuffers() {
      return this.loadedNearToFarBuffers;
   }

   public String getVboRenderDebugMenuString() {
      String countText = F3Screen.NUMBER_FORMAT.format((long)this.visibleBufferCount);
      if (!Config.Client.Advanced.Graphics.Culling.disableFrustumCulling.get()) {
         countText = countText + "/" + F3Screen.NUMBER_FORMAT.format((long)(this.visibleBufferCount + this.culledBufferCount));
      }

      return "VBO Render Count: [" + countText + "]";
   }

   public String getShadowPassRenderDebugMenuString() {
      boolean hasIrisShaders = IRIS_ACCESSOR != null && IRIS_ACCESSOR.isShaderPackInUse();
      if (!hasIrisShaders) {
         return null;
      } else {
         String countText = F3Screen.NUMBER_FORMAT.format((long)this.shadowVisibleBufferCount);
         if (!Config.Client.Advanced.Graphics.Culling.disableFrustumCulling.get()) {
            countText = countText + "/" + F3Screen.NUMBER_FORMAT.format((long)(this.shadowVisibleBufferCount + this.shadowCulledBufferCount));
         }

         return "Shadow VBO Render Count: [" + countText + "]";
      }
   }

   @Override
   public void close() {
      this.lodQuadTree.close();
   }
}
