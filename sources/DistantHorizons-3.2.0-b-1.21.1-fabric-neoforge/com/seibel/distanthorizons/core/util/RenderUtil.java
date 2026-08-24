package com.seibel.distanthorizons.core.util;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.CameraZoom;
import com.seibel.distanthorizons.core.render.EDhRenderDepth;
import com.seibel.distanthorizons.core.util.math.DhMat4f;
import com.seibel.distanthorizons.core.util.math.DhVec3f;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IIrisAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.AbstractDhRenderApiDefinition;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.coreapi.util.MathUtil;

public class RenderUtil {
   private static final DhLogger LOGGER = new DhLoggerBuilder().maxCountPerSecond(1).build();
   private static final IMinecraftClientWrapper MC = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final IIrisAccessor IRIS_ACCESSOR = ModAccessorInjector.INSTANCE.get(IIrisAccessor.class);
   private static final AbstractDhRenderApiDefinition RENDER_API_DEF = SingletonInjector.INSTANCE.get(AbstractDhRenderApiDefinition.class);
   public static final double NOT_ZOOMED_MAGNIFICATION = 1.0;
   private static final double MIN_ZOOM_MAGNIFICATION = 1.5;
   private static final double ZOOM_CONE_PADDING_MULTIPLIER = 1.5;

   public static void setDhProjectionMatrix(DhApiMat4f updateMatrix, DhApiMat4f mcProjMat) {
      float nearClipDist = getNearClipPlaneInBlocks();
      if (getHeightBasedNearClipOverrideBlockDistance() == -1.0F) {
         nearClipDist = Math.min(nearClipDist, 7.5F);
      }

      float farClipDist = getFarClipPlaneDistanceInBlocks();
      updateMatrix.set(mcProjMat);
      if (RENDER_API_DEF.getRenderDepth() == EDhRenderDepth.FORWARD_Z) {
         setClipPlanes(updateMatrix, nearClipDist, farClipDist, false);
      } else {
         setClipPlanes(updateMatrix, farClipDist, nearClipDist, true);
      }
   }

   public static void setClipPlanes(DhApiMat4f matrix, float nearClip, float farClip, boolean zZeroToOne) {
      matrix.m22 = (zZeroToOne ? farClip : farClip + nearClip) / (nearClip - farClip);
      matrix.m23 = (zZeroToOne ? farClip : farClip + farClip) * nearClip / (nearClip - farClip);
   }

   public static float getNearClipPlaneInBlocks() {
      float overdraw = Config.Client.Advanced.Graphics.Culling.overdrawPrevention.get();
      if (overdraw < 0.0F) {
         int chunkRenderDistance = MC_RENDER.getRenderDistance();
         if (IRIS_ACCESSOR != null && IRIS_ACCESSOR.isShaderPackInUse()) {
            overdraw = 0.2F;
         } else if (chunkRenderDistance <= 2) {
            overdraw = 0.2F;
         } else if (chunkRenderDistance <= 4) {
            overdraw = 0.3F;
         } else if (chunkRenderDistance <= 6) {
            overdraw = 0.6F;
         } else if (chunkRenderDistance <= 10) {
            overdraw = 0.8F;
         } else {
            overdraw = 0.9F;
         }
      } else {
         overdraw = MathUtil.clamp(0.05F, overdraw, 1.0F);
      }

      if (Config.Client.Advanced.Graphics.Culling.reduceOverdrawWithFastMovement.get()) {
         double avgSpeed = ClientApi.INSTANCE.getAvgCameraSpeed();
         if (avgSpeed >= 10.0) {
            float speedRange = (float)((100.0 - avgSpeed) / 100.0);
            speedRange = Math.max(speedRange, 0.2F);
            overdraw *= speedRange;
         }
      }

      return getNearClipPlaneDistanceInBlocks(overdraw);
   }

   private static float getNearClipPlaneDistanceInBlocks(float overdrawPreventionPercent) {
      int chunkRenderDistance = MC_RENDER.getRenderDistance();
      int vanillaBlockRenderedDistance = chunkRenderDistance * 16;
      float nearClipPlane;
      if (Config.Client.Advanced.Debugging.lodOnlyMode.get()) {
         nearClipPlane = 0.5F;
      } else {
         nearClipPlane = vanillaBlockRenderedDistance;
         nearClipPlane *= overdrawPreventionPercent;
         if (nearClipPlane < 1.0F) {
            nearClipPlane = 1.0F;
         }
      }

      float heightOverride = getHeightBasedNearClipOverrideBlockDistance();
      if (heightOverride != -1.0F) {
         nearClipPlane = heightOverride;
      }

      double fov = 70.0;
      double aspectRatio = (double)MC_RENDER.getTargetFramebufferViewportWidth() / MC_RENDER.getTargetFramebufferViewportHeight();
      return (float)(nearClipPlane / Math.sqrt(1.0 + MathUtil.pow2(Math.tan(fov / 180.0 * 3.141592653589793 / 2.0)) * (MathUtil.pow2(aspectRatio) + 1.0)));
   }

   public static float getHeightBasedNearClipOverrideBlockDistance() {
      IClientLevelWrapper level = MC.getWrappedClientLevel();
      if (level != null) {
         int playerHeight = MC.getPlayerBlockPos().getY();
         int levelMaxHeight = level.getMaxHeight();
         if (playerHeight > levelMaxHeight + 1000) {
            return playerHeight - (levelMaxHeight + 1000);
         }
      }

      return -1.0F;
   }

   public static float getFarClipPlaneDistanceInBlocks() {
      if (IRIS_ACCESSOR != null) {
         int lodChunkDist = DhApi.Delayed.configs.graphics().chunkRenderDistance().getValue();
         int lodBlockDist = lodChunkDist * 16;
         return (float)((lodBlockDist + 512) * Math.sqrt(2.0));
      } else {
         int lodChunkDist = Config.Client.Advanced.Graphics.Quality.lodChunkRenderDistanceRadius.get();
         int lodBlockDist = lodChunkDist * 16;
         return (lodBlockDist + 512) * 2;
      }
   }

   public static void updateCameraZoom(CameraZoom cameraZoom) {
      if (!Config.Client.Advanced.Graphics.Quality.increaseQualityWhenZoomedIn.get()) {
         cameraZoom.set(CameraZoom.NOT_ZOOMED);
      } else {
         DhApiMat4f projectionMatrix = ClientApi.RENDER_STATE.mcProjectionMatrix;
         if (projectionMatrix == null) {
            cameraZoom.set(CameraZoom.NOT_ZOOMED);
         } else {
            if (projectionMatrix.equals(DhMat4f.IDENTITY)) {
               projectionMatrix = ClientApi.RENDER_STATE.mcModelViewMatrix;
               if (projectionMatrix == null) {
                  cameraZoom.set(CameraZoom.NOT_ZOOMED);
                  return;
               }
            }

            double projectionYScale = Math.sqrt(MathUtil.pow2(projectionMatrix.m10) + MathUtil.pow2(projectionMatrix.m11) + MathUtil.pow2(projectionMatrix.m12));
            double fovSettingYScale = 1.0 / Math.tan(Math.toRadians(MC_RENDER.getFovSetting()) / 2.0);
            double magnification = projectionYScale / fovSettingYScale;
            if (magnification < 1.5) {
               cameraZoom.set(CameraZoom.NOT_ZOOMED);
            } else {
               double quadraticBase = Config.Client.Advanced.Graphics.Quality.horizontalQuality.get().quadraticBase;
               double maxMagnification = Math.pow(quadraticBase, Config.Client.Advanced.Graphics.Quality.maxZoomQualityIncrease.get().intValue());
               magnification = Math.min(magnification, maxMagnification);
               DhVec3f lookAtVector = MC_RENDER.getLookAtVector();
               double lookLengthXZ = Math.sqrt(MathUtil.pow2(lookAtVector.x) + MathUtil.pow2(lookAtVector.z));
               if (lookLengthXZ < 0.1) {
                  cameraZoom.set(CameraZoom.NOT_ZOOMED);
               } else {
                  double projectionXScale = Math.sqrt(
                     MathUtil.pow2(projectionMatrix.m00) + MathUtil.pow2(projectionMatrix.m01) + MathUtil.pow2(projectionMatrix.m02)
                  );
                  double coneTanHalfAngle = 1.0 / projectionXScale * 1.5;
                  cameraZoom.set(magnification, coneTanHalfAngle, lookAtVector.x / lookLengthXZ, lookAtVector.z / lookLengthXZ);
               }
            }
         }
      }
   }

   private static class DynamicOverdraw {
      public static final float MIN_SPEED = 10.0F;
      public static final float MAX_SPEED = 100.0F;
      public static final float MIN_OVERDRAW_RATIO = 0.2F;
   }
}
