package com.seibel.distanthorizons.common.render.openGl.postProcessing.fade;

import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.GlScreenQuad;
import com.seibel.distanthorizons.common.render.openGl.util.GlAbstractShaderRenderer;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.render.RenderParams;
import org.lwjgl.opengl.GL33;

public class GlDhFarFadeApplyShader extends GlAbstractShaderRenderer {
   public static GlDhFarFadeApplyShader INSTANCE = new GlDhFarFadeApplyShader();
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   public int fadeTexture;
   public int readFramebuffer;
   public int drawFramebuffer;
   public int uFadeColorTextureUniform = -1;

   @Override
   public void onInit() {
      this.shader = new GlShaderProgram(
         "assets/distanthorizons/shaders/shared/gl/quad_apply.vert", "assets/distanthorizons/shaders/fade/gl/apply.frag", "vPosition"
      );
      this.uFadeColorTextureUniform = this.shader.getUniformLocation("uFadeColorTextureUniform");
   }

   @Override
   protected void onApplyUniforms(RenderParams renderParams) {
      GLMC.glActiveTexture(33984);
      GLMC.glBindTexture(this.fadeTexture);
      GL33.glUniform1i(this.uFadeColorTextureUniform, 0);
   }

   @Override
   protected void onRender() {
      GLMC.disableBlend();
      GLMC.disableDepthTest();
      GLMC.glBindFramebuffer(36008, this.readFramebuffer);
      GLMC.glBindFramebuffer(36009, this.drawFramebuffer);
      GlScreenQuad.INSTANCE.render();
      GLMC.enableDepthTest();
   }
}
