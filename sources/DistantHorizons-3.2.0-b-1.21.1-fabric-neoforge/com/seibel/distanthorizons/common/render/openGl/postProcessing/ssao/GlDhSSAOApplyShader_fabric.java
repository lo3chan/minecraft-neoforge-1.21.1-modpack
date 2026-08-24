package com.seibel.distanthorizons.common.render.openGl.postProcessing.ssao;

import com.seibel.distanthorizons.common.render.openGl.GlDhMetaRenderer_fabric;
import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.GlScreenQuad;
import com.seibel.distanthorizons.common.render.openGl.util.GlAbstractShaderRenderer;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.util.RenderUtil;
import org.lwjgl.opengl.GL33;

public class GlDhSSAOApplyShader_fabric extends GlAbstractShaderRenderer {
   public static GlDhSSAOApplyShader_fabric INSTANCE = new GlDhSSAOApplyShader_fabric();
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   public int ssaoTexture;
   public int gSSAOMapUniform;
   public int gDepthMapUniform;
   public int gViewSizeUniform;
   public int gBlurRadiusUniform;
   public int gNearUniform;
   public int gFarUniform;

   @Override
   public void onInit() {
      this.shader = new GlShaderProgram(
         "assets/distanthorizons/shaders/shared/gl/quad_apply.vert", "assets/distanthorizons/shaders/ssao/gl/apply.frag", "vPosition"
      );
      this.gSSAOMapUniform = this.shader.getUniformLocation("gSSAOMap");
      this.gDepthMapUniform = this.shader.getUniformLocation("gDepthMap");
      this.gViewSizeUniform = this.shader.tryGetUniformLocation("gViewSize");
      this.gBlurRadiusUniform = this.shader.tryGetUniformLocation("gBlurRadius");
      this.gNearUniform = this.shader.tryGetUniformLocation("gNear");
      this.gFarUniform = this.shader.tryGetUniformLocation("gFar");
   }

   @Override
   protected void onApplyUniforms(RenderParams renderParams) {
      GLMC.glActiveTexture(33984);
      GLMC.glBindTexture(GlDhMetaRenderer_fabric.INSTANCE.getActiveDepthTextureId());
      GL33.glUniform1i(this.gDepthMapUniform, 0);
      GLMC.glActiveTexture(33985);
      GLMC.glBindTexture(this.ssaoTexture);
      GL33.glUniform1i(this.gSSAOMapUniform, 1);
      GL33.glUniform1i(this.gBlurRadiusUniform, 2);
      if (this.gViewSizeUniform >= 0) {
         GL33.glUniform2f(this.gViewSizeUniform, MC_RENDER.getTargetFramebufferViewportWidth(), MC_RENDER.getTargetFramebufferViewportHeight());
      }

      if (this.gNearUniform >= 0) {
         GL33.glUniform1f(this.gNearUniform, RenderUtil.getNearClipPlaneInBlocks());
      }

      if (this.gFarUniform >= 0) {
         float farClipPlane = RenderUtil.getFarClipPlaneDistanceInBlocks();
         GL33.glUniform1f(this.gFarUniform, farClipPlane);
      }
   }

   @Override
   protected void onRender() {
      GLMC.enableBlend();
      GL33.glBlendEquation(32774);
      GLMC.glBlendFuncSeparate(0, 770, 0, 1);
      GLMC.disableDepthTest();
      GLMC.glBindFramebuffer(36008, GlDhSSAOShader_fabric.INSTANCE.frameBuffer);
      GLMC.glBindFramebuffer(36009, GlDhMetaRenderer_fabric.INSTANCE.getActiveFramebufferId());
      GlScreenQuad.INSTANCE.render();
   }
}
