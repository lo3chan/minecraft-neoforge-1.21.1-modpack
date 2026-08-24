package com.seibel.distanthorizons.common.render.openGl.postProcessing.apply;

import com.seibel.distanthorizons.common.render.openGl.GlDhMetaRenderer_neoforge;
import com.seibel.distanthorizons.common.render.openGl.glObject.GLState;
import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.GlScreenQuad;
import com.seibel.distanthorizons.common.render.openGl.util.GlAbstractShaderRenderer;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import org.lwjgl.opengl.GL33;

public class GlDhApplyShader_neoforge extends GlAbstractShaderRenderer {
   public static GlDhApplyShader_neoforge INSTANCE = new GlDhApplyShader_neoforge();
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   public int gDhColorTextureUniform;
   public int gDepthMapUniform;

   private GlDhApplyShader_neoforge() {
   }

   @Override
   public void onInit() {
      this.shader = new GlShaderProgram(
         "assets/distanthorizons/shaders/shared/gl/quad_apply.vert", "assets/distanthorizons/shaders/shared/gl/apply.frag", "vPosition"
      );
      this.gDhColorTextureUniform = this.shader.getUniformLocation("gDhColorTexture");
      this.gDepthMapUniform = this.shader.getUniformLocation("gDhDepthTexture");
   }

   @Override
   protected void onRender() {
      if (MC_RENDER.mcRendersToFrameBuffer()) {
         this.renderToFrameBuffer();
      } else {
         this.renderToMcTexture();
      }
   }

   private void renderToFrameBuffer() {
      int targetFrameBuffer = MC_RENDER.getTargetFramebuffer();
      if (targetFrameBuffer != -1) {
         try (GLState state = new GLState()) {
            GLMC.disableDepthTest();
            GLMC.disableBlend();
            GLMC.glActiveTexture(33984);
            GLMC.glBindTexture(GlDhMetaRenderer_neoforge.INSTANCE.getActiveColorTextureId());
            GL33.glUniform1i(this.gDhColorTextureUniform, 0);
            GLMC.glActiveTexture(33985);
            GLMC.glBindTexture(GlDhMetaRenderer_neoforge.INSTANCE.getActiveDepthTextureId());
            GL33.glUniform1i(this.gDepthMapUniform, 1);
            GLMC.glBindFramebuffer(36160, targetFrameBuffer);
            GlScreenQuad.INSTANCE.render();
         }

         GLMC.glBindFramebuffer(36160, targetFrameBuffer);
      }
   }

   private void renderToMcTexture() {
      int targetColorTextureId = MC_RENDER.getGlColorTextureId();
      if (targetColorTextureId != -1) {
         int dhFrameBufferId = GlDhMetaRenderer_neoforge.INSTANCE.getActiveFramebufferId();
         if (dhFrameBufferId != -1) {
            int mcFrameBufferId = MC_RENDER.getTargetFramebuffer();
            if (mcFrameBufferId != -1) {
               try (GLState state = new GLState()) {
                  GLMC.disableDepthTest();
                  GLMC.disableBlend();
                  GLMC.glActiveTexture(33984);
                  GLMC.glBindTexture(GlDhMetaRenderer_neoforge.INSTANCE.getActiveColorTextureId());
                  GL33.glUniform1i(this.gDhColorTextureUniform, 0);
                  GLMC.glActiveTexture(33985);
                  GLMC.glBindTexture(GlDhMetaRenderer_neoforge.INSTANCE.getActiveDepthTextureId());
                  GL33.glUniform1i(this.gDepthMapUniform, 1);
                  GL33.glFramebufferTexture(36009, 36064, targetColorTextureId, 0);
                  GLMC.glBindFramebuffer(36160, dhFrameBufferId);
                  GlScreenQuad.INSTANCE.render();
               }

               GLMC.glBindFramebuffer(36160, mcFrameBufferId);
            }
         }
      }
   }
}
