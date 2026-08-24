package com.seibel.distanthorizons.common.render.openGl.postProcessing.ssao;

import com.seibel.distanthorizons.common.render.openGl.glObject.GLState;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhSsaoRenderer;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL43C;

public class GlDhSSAORenderer_neoforge implements IDhSsaoRenderer {
   public static GlDhSSAORenderer_neoforge INSTANCE = new GlDhSSAORenderer_neoforge();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private boolean init = false;
   private int width = -1;
   private int height = -1;
   private int ssaoFramebuffer = -1;
   private int ssaoTexture = -1;

   private GlDhSSAORenderer_neoforge() {
   }

   public void init() {
      if (!this.init) {
         this.init = true;
         GlDhSSAOShader_neoforge.INSTANCE.init();
         GlDhSSAOApplyShader_neoforge.INSTANCE.init();
      }
   }

   private void createFramebuffer(int width, int height) {
      if (this.ssaoFramebuffer != -1) {
         GL33.glDeleteFramebuffers(this.ssaoFramebuffer);
         this.ssaoFramebuffer = -1;
      }

      if (this.ssaoTexture != -1) {
         GLMC.glDeleteTextures(this.ssaoTexture);
         this.ssaoTexture = -1;
      }

      this.ssaoFramebuffer = GL33.glGenFramebuffers();
      GLMC.glBindFramebuffer(36160, this.ssaoFramebuffer);
      this.ssaoTexture = GLMC.glGenTextures();
      GLMC.glBindTexture(this.ssaoTexture);
      GL33.glTexImage2D(3553, 0, 33325, width, height, 0, 6403, 5131, (ByteBuffer)null);
      GL33.glTexParameteri(3553, 10241, 9729);
      GL33.glTexParameteri(3553, 10240, 9729);
      GL43C.glTexParameteri(3553, 33084, 0);
      GL43C.glTexParameteri(3553, 33085, 0);
      GL33.glFramebufferTexture2D(36160, 36064, 3553, this.ssaoTexture, 0);
   }

   @Override
   public void render(RenderParams renderParams) {
      try (GLState state = new GLState()) {
         this.init();
         int width = MC_RENDER.getTargetFramebufferViewportWidth();
         int height = MC_RENDER.getTargetFramebufferViewportHeight();
         if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            this.createFramebuffer(width, height);
         }

         GlDhSSAOShader_neoforge.INSTANCE.frameBuffer = this.ssaoFramebuffer;
         GlDhSSAOShader_neoforge.INSTANCE.setProjectionMatrix(renderParams.dhProjectionMatrix);
         GlDhSSAOShader_neoforge.INSTANCE.render(renderParams);
         GlDhSSAOApplyShader_neoforge.INSTANCE.ssaoTexture = this.ssaoTexture;
         GlDhSSAOApplyShader_neoforge.INSTANCE.render(renderParams);
      }
   }

   public void free() {
      GlDhSSAOShader_neoforge.INSTANCE.free();
      GlDhSSAOApplyShader_neoforge.INSTANCE.free();
   }
}
