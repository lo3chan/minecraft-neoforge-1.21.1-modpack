package com.seibel.distanthorizons.common.render.openGl.postProcessing.fade;

import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;
import com.seibel.distanthorizons.common.render.openGl.GlDhMetaRenderer_fabric;
import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.GlScreenQuad;
import com.seibel.distanthorizons.common.render.openGl.util.GlAbstractShaderRenderer;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.util.RenderUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import org.lwjgl.opengl.GL33;

public class GlDhFarFadeShader_fabric extends GlAbstractShaderRenderer {
   public static GlDhFarFadeShader_fabric INSTANCE = new GlDhFarFadeShader_fabric();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   public int frameBuffer = -1;
   private DhApiMat4f inverseDhMvmProjMatrix;
   public int uDhInvMvmProj = -1;
   public int uDhDepthTexture = -1;
   public int uMcColorTexture = -1;
   public int uDhColorTexture = -1;
   public int uStartFadeBlockDistance = -1;
   public int uEndFadeBlockDistance = -1;

   @Override
   public void onInit() {
      this.shader = new GlShaderProgram(
         "assets/distanthorizons/shaders/shared/gl/quad_apply.vert", "assets/distanthorizons/shaders/fade/gl/dh_fade.frag", "vPosition"
      );
      this.uDhInvMvmProj = this.shader.tryGetUniformLocation("uDhInvMvmProj");
      this.uDhDepthTexture = this.shader.tryGetUniformLocation("uDhDepthTexture");
      this.uMcColorTexture = this.shader.tryGetUniformLocation("uMcColorTexture");
      this.uDhColorTexture = this.shader.tryGetUniformLocation("uDhColorTexture");
      this.uStartFadeBlockDistance = this.shader.tryGetUniformLocation("uStartFadeBlockDistance");
      this.uEndFadeBlockDistance = this.shader.tryGetUniformLocation("uEndFadeBlockDistance");
   }

   @Override
   protected void onApplyUniforms(RenderParams renderParams) {
      this.shader.setUniform(this.uDhInvMvmProj, this.inverseDhMvmProjMatrix);
      float dhFarClipDistance = RenderUtil.getFarClipPlaneDistanceInBlocks();
      float fadeStartDistance = dhFarClipDistance * 0.5F;
      float fadeEndDistance = dhFarClipDistance * 0.9F;
      this.shader.setUniform(this.uStartFadeBlockDistance, fadeStartDistance);
      this.shader.setUniform(this.uEndFadeBlockDistance, fadeEndDistance);
   }

   public void setProjectionMatrix(RenderParams renderParams) {
      this.inverseDhMvmProjMatrix = renderParams.dhInverseMvmProjectionMatrix;
   }

   @Override
   protected void onRender() {
      int depthTextureId = GlDhMetaRenderer_fabric.INSTANCE.getActiveDepthTextureId();
      int colorTextureId = GlDhMetaRenderer_fabric.INSTANCE.getActiveColorTextureId();
      if (depthTextureId != -1 && colorTextureId != -1) {
         GLMC.glBindFramebuffer(36160, this.frameBuffer);
         GLMC.disableScissorTest();
         GLMC.disableDepthTest();
         GLMC.disableBlend();
         GLMC.glActiveTexture(33984);
         GLMC.glBindTexture(depthTextureId);
         GL33.glUniform1i(this.uDhDepthTexture, 0);
         GLMC.glActiveTexture(33985);
         GLMC.glBindTexture(MC_RENDER.getGlColorTextureId());
         GL33.glUniform1i(this.uMcColorTexture, 1);
         GLMC.glActiveTexture(33986);
         GLMC.glBindTexture(colorTextureId);
         GL33.glUniform1i(this.uDhColorTexture, 2);
         GlScreenQuad.INSTANCE.render();
      }
   }
}
