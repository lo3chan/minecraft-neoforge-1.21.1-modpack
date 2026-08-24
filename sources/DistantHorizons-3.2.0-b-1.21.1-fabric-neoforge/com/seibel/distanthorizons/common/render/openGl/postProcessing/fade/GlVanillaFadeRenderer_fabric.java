package com.seibel.distanthorizons.common.render.openGl.postProcessing.fade;

import com.seibel.distanthorizons.common.render.openGl.GlDhMetaRenderer_fabric;
import com.seibel.distanthorizons.common.render.openGl.glObject.GLState;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhVanillaFadeRenderer;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL33;

public class GlVanillaFadeRenderer_fabric implements IDhVanillaFadeRenderer {
   public static GlVanillaFadeRenderer_fabric INSTANCE = new GlVanillaFadeRenderer_fabric();
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private boolean init = false;
   private int width = -1;
   private int height = -1;
   private int fadeFramebuffer = -1;
   private int fadeTexture = -1;

   private GlVanillaFadeRenderer_fabric() {
   }

   public void init() {
      if (!this.init) {
         this.init = true;
         GlDhVanillaFadeShader_fabric.INSTANCE.init();
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
      if (MC_RENDER.mcRendersToFrameBuffer()) {
         if (this.fadeTexture != -1) {
            GLMC.glDeleteTextures(this.fadeTexture);
            this.fadeTexture = -1;
         }

         this.fadeTexture = GL33.glGenTextures();
         GLMC.glBindTexture(this.fadeTexture);
         GL33.glTexImage2D(3553, 0, 32859, width, height, 0, 6408, 32819, (ByteBuffer)null);
         GL33.glTexParameteri(3553, 10241, 9729);
         GL33.glTexParameteri(3553, 10240, 9729);
         GL33.glFramebufferTexture2D(36160, 36064, 3553, this.fadeTexture, 0);
      } else {
         GL33.glFramebufferTexture2D(36160, 36064, 3553, MC_RENDER.getGlColorTextureId(), 0);
      }
   }

   @Override
   public void render(RenderParams renderParams) {
      int depthTextureId = GlDhMetaRenderer_fabric.INSTANCE.getActiveDepthTextureId();
      if (depthTextureId != -1) {
         IProfilerWrapper profiler = MC_CLIENT.getProfiler();

         try (
            IProfilerWrapper.IProfileBlock fade_profile = profiler.push("DH-Vanilla Fade");
            GLState mcState = new GLState();
         ) {
            this.init();
            int width = MC_RENDER.getTargetFramebufferViewportWidth();
            int height = MC_RENDER.getTargetFramebufferViewportHeight();
            if (this.width != width || this.height != height) {
               this.width = width;
               this.height = height;
               this.createFramebuffer(width, height);
            }

            GlDhVanillaFadeShader_fabric.INSTANCE.frameBuffer = this.fadeFramebuffer;
            GlDhVanillaFadeShader_fabric.INSTANCE.setProjectionMatrix(renderParams);
            GlDhVanillaFadeShader_fabric.INSTANCE.setLevelMaxHeight(renderParams.clientLevelWrapper.getMaxHeight());
            GlDhVanillaFadeShader_fabric.INSTANCE.render(renderParams);
            if (MC_RENDER.mcRendersToFrameBuffer()) {
               GlDhFarFadeApplyShader.INSTANCE.fadeTexture = this.fadeTexture;
               GlDhFarFadeApplyShader.INSTANCE.readFramebuffer = GlDhVanillaFadeShader_fabric.INSTANCE.frameBuffer;
               GlDhFarFadeApplyShader.INSTANCE.drawFramebuffer = MC_RENDER.getTargetFramebuffer();
               GlDhFarFadeApplyShader.INSTANCE.render(renderParams);
            }
         } catch (Exception var12) {
            LOGGER.error("Unexpected error during fade render, error: [" + var12.getMessage() + "].", var12);
         }
      }
   }

   public void free() {
      GlDhVanillaFadeShader_fabric.INSTANCE.free();
      GlDhFarFadeApplyShader.INSTANCE.free();
   }
}
