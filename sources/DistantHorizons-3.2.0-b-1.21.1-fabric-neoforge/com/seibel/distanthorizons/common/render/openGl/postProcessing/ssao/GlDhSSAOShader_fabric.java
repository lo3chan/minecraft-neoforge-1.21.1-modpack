package com.seibel.distanthorizons.common.render.openGl.postProcessing.ssao;

import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;
import com.seibel.distanthorizons.common.render.openGl.GlDhMetaRenderer_fabric;
import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.GlScreenQuad;
import com.seibel.distanthorizons.common.render.openGl.util.GlAbstractShaderRenderer;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.util.math.DhMat4f;
import org.lwjgl.opengl.GL33;

public class GlDhSSAOShader_fabric extends GlAbstractShaderRenderer {
   public static GlDhSSAOShader_fabric INSTANCE = new GlDhSSAOShader_fabric();
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   public int frameBuffer;
   private DhMat4f projection;
   private DhMat4f invertedProjection;
   public int uProj;
   public int uInvProj;
   public int uSampleCount;
   public int uRadius;
   public int uStrength;
   public int uMinLight;
   public int uBias;
   public int uDepthMap;
   public int uFadeDistanceInBlocks;

   @Override
   public void onInit() {
      this.shader = new GlShaderProgram(
         "assets/distanthorizons/shaders/shared/gl/quad_apply.vert", "assets/distanthorizons/shaders/ssao/gl/ao.frag", "vPosition"
      );
      this.uProj = this.shader.getUniformLocation("uProj");
      this.uInvProj = this.shader.getUniformLocation("uInvProj");
      this.uSampleCount = this.shader.getUniformLocation("uSampleCount");
      this.uRadius = this.shader.getUniformLocation("uRadius");
      this.uStrength = this.shader.getUniformLocation("uStrength");
      this.uMinLight = this.shader.getUniformLocation("uMinLight");
      this.uBias = this.shader.getUniformLocation("uBias");
      this.uDepthMap = this.shader.getUniformLocation("uDepthMap");
      this.uFadeDistanceInBlocks = this.shader.getUniformLocation("uFadeDistanceInBlocks");
   }

   public void setProjectionMatrix(DhApiMat4f projectionMatrix) {
      this.projection = new DhMat4f(projectionMatrix);
      this.invertedProjection = new DhMat4f(projectionMatrix);
      this.invertedProjection.invert();
   }

   @Override
   protected void onApplyUniforms(RenderParams renderParams) {
      this.shader.setUniform(this.uProj, this.projection);
      this.shader.setUniform(this.uInvProj, this.invertedProjection);
      this.shader.setUniform(this.uSampleCount, 6);
      this.shader.setUniform(this.uRadius, 4.0F);
      this.shader.setUniform(this.uStrength, 0.2F);
      this.shader.setUniform(this.uMinLight, 0.25F);
      this.shader.setUniform(this.uBias, 0.02F);
      this.shader.setUniform(this.uFadeDistanceInBlocks, 1600.0F);
      GL33.glUniform1i(this.uDepthMap, 0);
   }

   @Override
   protected void onRender() {
      GLMC.glBindFramebuffer(36160, this.frameBuffer);
      GLMC.disableScissorTest();
      GLMC.disableDepthTest();
      GLMC.disableBlend();
      GLMC.glActiveTexture(33984);
      GLMC.glBindTexture(GlDhMetaRenderer_fabric.INSTANCE.getActiveDepthTextureId());
      GlScreenQuad.INSTANCE.render();
   }
}
