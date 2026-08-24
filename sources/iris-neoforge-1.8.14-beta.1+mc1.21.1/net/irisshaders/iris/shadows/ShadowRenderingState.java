package net.irisshaders.iris.shadows;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.RenderBuffers;

public class ShadowRenderingState {
   private static ShadowRenderingState.BlockEntityRenderFunction function = ShadowRenderer::renderBlockEntities;

   public static boolean areShadowsCurrentlyBeingRendered() {
      return ShadowRenderer.ACTIVE;
   }

   public static void setBlockEntityRenderFunction(ShadowRenderingState.BlockEntityRenderFunction function) {
      ShadowRenderingState.function = function;
   }

   public static int renderBlockEntities(
      ShadowRenderer shadowRenderer,
      RenderBuffers bufferSource,
      PoseStack modelView,
      Camera camera,
      double cameraX,
      double cameraY,
      double cameraZ,
      float tickDelta,
      boolean hasEntityFrustum,
      boolean lightsOnly
   ) {
      return function.renderBlockEntities(shadowRenderer, bufferSource, modelView, camera, cameraX, cameraY, cameraZ, tickDelta, hasEntityFrustum, lightsOnly);
   }

   public static int getRenderDistance() {
      return ShadowRenderer.renderDistance;
   }

   public interface BlockEntityRenderFunction {
      int renderBlockEntities(
         ShadowRenderer var1, RenderBuffers var2, PoseStack var3, Camera var4, double var5, double var7, double var9, float var11, boolean var12, boolean var13
      );
   }
}
