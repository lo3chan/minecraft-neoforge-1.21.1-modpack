package com.seibel.distanthorizons.common.render.openGl.postProcessing.fog;

import com.seibel.distanthorizons.common.render.openGl.GlDhMetaRenderer_neoforge;
import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.GlScreenQuad;
import com.seibel.distanthorizons.common.render.openGl.util.GlAbstractShaderRenderer;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.render.RenderParams;
import org.lwjgl.opengl.GL33;

public class GlDhFogApplyShader_neoforge extends GlAbstractShaderRenderer {
   public static GlDhFogApplyShader_neoforge INSTANCE = new GlDhFogApplyShader_neoforge();
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   public int fogTexture;
   public int colorTextureUniform;
   public int depthTextureUniform;

   @Override
   public void onInit() {
      this.shader = new GlShaderProgram(
         "assets/distanthorizons/shaders/shared/gl/quad_apply.vert", "assets/distanthorizons/shaders/fog/gl/apply.frag", "vPosition"
      );
      this.colorTextureUniform = this.shader.getUniformLocation("uColorTexture");
      this.depthTextureUniform = this.shader.getUniformLocation("uDepthTexture");
   }

   @Override
   protected void onApplyUniforms(RenderParams renderParams) {
      GLMC.glActiveTexture(33984);
      GLMC.glBindTexture(this.fogTexture);
      GL33.glUniform1i(this.colorTextureUniform, 0);
      GLMC.glActiveTexture(33985);
      GLMC.glBindTexture(GlDhMetaRenderer_neoforge.INSTANCE.getActiveDepthTextureId());
      GL33.glUniform1i(this.depthTextureUniform, 1);
   }

   @Override
   protected void onRender() {
      GLMC.enableBlend();
      GL33.glBlendEquation(32774);
      GLMC.glBlendFuncSeparate(770, 771, 1, 771);
      GLMC.disableDepthTest();
      GLMC.glBindFramebuffer(36008, GlDhFogShader_neoforge.INSTANCE.frameBuffer);
      GLMC.glBindFramebuffer(36009, GlDhMetaRenderer_neoforge.INSTANCE.getActiveFramebufferId());
      GlScreenQuad.INSTANCE.render();
      GLMC.glBindFramebuffer(36008, 0);
   }
}
