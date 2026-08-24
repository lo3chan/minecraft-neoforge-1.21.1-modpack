package com.seibel.distanthorizons.common.render.openGl.postProcessing.fade;

import com.seibel.distanthorizons.common.render.openGl.GlDhMetaRenderer_fabric;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhFarFadeRenderer;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL43C;

public class GlDhFarFadeRenderer_fabric implements IDhFarFadeRenderer {
   public static GlDhFarFadeRenderer_fabric INSTANCE = new GlDhFarFadeRenderer_fabric();
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private boolean init = false;
   private int width = -1;
   private int height = -1;
   private int fadeFramebuffer = -1;
   private int fadeTexture = -1;

   private GlDhFarFadeRenderer_fabric() {
   }

   public void init() {
      if (!this.init) {
         this.init = true;
         GlDhFarFadeShader_fabric.INSTANCE.init();
         GlDhFarFadeApplyShader.INSTANCE.init();
      }
   }

   private void createFramebuffer(int width, int height) {
      if (this.fadeFramebuffer != -1) {
         GL33.glDeleteFramebuffers(this.fadeFramebuffer);
         this.fadeFramebuffer = -1;
      }

      this.fadeFramebuffer = GL33.glGenFramebuffers();
      GLMC.glBindFramebuffer(36160, this.fadeFramebuffer);
      if (this.fadeTexture != -1) {
         GLMC.glDeleteTextures(this.fadeTexture);
         this.fadeTexture = -1;
      }

      this.fadeTexture = GL33.glGenTextures();
      GLMC.glBindTexture(this.fadeTexture);
      GL33.glTexImage2D(3553, 0, 32859, width, height, 0, 6408, 32819, (ByteBuffer)null);
      GL33.glTexParameteri(3553, 10241, 9729);
      GL33.glTexParameteri(3553, 10240, 9729);
      GL43C.glTexParameteri(3553, 33084, 0);
      GL43C.glTexParameteri(3553, 33085, 0);
      GL33.glFramebufferTexture2D(36160, 36064, 3553, this.fadeTexture, 0);
   }

   @Override
   public void render(RenderParams renderParams) {
      try {
         this.init();
         int width = MC_RENDER.getTargetFramebufferViewportWidth();
         int height = MC_RENDER.getTargetFramebufferViewportHeight();
         if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            this.createFramebuffer(width, height);
         }

         GlDhFarFadeShader_fabric.INSTANCE.frameBuffer = this.fadeFramebuffer;
         GlDhFarFadeShader_fabric.INSTANCE.setProjectionMatrix(renderParams);
         GlDhFarFadeShader_fabric.INSTANCE.render(renderParams);
         GlDhFarFadeApplyShader.INSTANCE.fadeTexture = this.fadeTexture;
         GlDhFarFadeApplyShader.INSTANCE.readFramebuffer = GlDhFarFadeShader_fabric.INSTANCE.frameBuffer;
         GlDhFarFadeApplyShader.INSTANCE.drawFramebuffer = GlDhMetaRenderer_fabric.INSTANCE.getActiveFramebufferId();
         GlDhFarFadeApplyShader.INSTANCE.render(renderParams);
      } catch (Exception var4) {
         LOGGER.error("Unexpected error during fade render, error: [" + var4.getMessage() + "].", var4);
      }
   }
}
