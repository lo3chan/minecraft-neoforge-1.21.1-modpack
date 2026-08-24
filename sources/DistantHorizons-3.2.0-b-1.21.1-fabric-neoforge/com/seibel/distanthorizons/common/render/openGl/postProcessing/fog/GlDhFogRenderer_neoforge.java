package com.seibel.distanthorizons.common.render.openGl.postProcessing.fog;

import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiFogRenderParam;
import com.seibel.distanthorizons.common.render.openGl.glObject.GLState;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhFogRenderer;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL43C;

public class GlDhFogRenderer_neoforge implements IDhFogRenderer {
   public static GlDhFogRenderer_neoforge INSTANCE = new GlDhFogRenderer_neoforge();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private boolean init = false;
   private int width = -1;
   private int height = -1;
   private int fogFramebuffer = -1;
   private int fogTexture = -1;

   private GlDhFogRenderer_neoforge() {
   }

   public void init() {
      if (!this.init) {
         this.init = true;
         GlDhFogShader_neoforge.INSTANCE.init();
         GlDhFogApplyShader_neoforge.INSTANCE.init();
      }
   }

   private void createFramebuffer(int width, int height) {
      if (this.fogFramebuffer != -1) {
         GL33.glDeleteFramebuffers(this.fogFramebuffer);
         this.fogFramebuffer = -1;
      }

      if (this.fogTexture != -1) {
         GLMC.glDeleteTextures(this.fogTexture);
         this.fogTexture = -1;
      }

      this.fogFramebuffer = GL33.glGenFramebuffers();
      GLMC.glBindFramebuffer(36160, this.fogFramebuffer);
      this.fogTexture = GLMC.glGenTextures();
      GLMC.glBindTexture(this.fogTexture);
      GL33.glTexImage2D(3553, 0, 32859, width, height, 0, 6408, 32819, (ByteBuffer)null);
      GL33.glTexParameteri(3553, 10241, 9729);
      GL33.glTexParameteri(3553, 10240, 9729);
      GL33.glFramebufferTexture2D(36160, 36064, 3553, this.fogTexture, 0);
      GL43C.glTexParameteri(3553, 33084, 0);
      GL43C.glTexParameteri(3553, 33085, 0);
   }

   @Override
   public void render(RenderParams renderParams, DhApiFogRenderParam fogRenderParams) {
      try (GLState state = new GLState()) {
         this.init();
         int width = MC_RENDER.getTargetFramebufferViewportWidth();
         int height = MC_RENDER.getTargetFramebufferViewportHeight();
         if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            this.createFramebuffer(width, height);
         }

         GlDhFogShader_neoforge.INSTANCE.frameBuffer = this.fogFramebuffer;
         GlDhFogShader_neoforge.INSTANCE.prepUniformObjects(renderParams.dhMvmProjMatrix, fogRenderParams);
         GlDhFogShader_neoforge.INSTANCE.render(renderParams);
         GlDhFogApplyShader_neoforge.INSTANCE.fogTexture = this.fogTexture;
         GlDhFogApplyShader_neoforge.INSTANCE.render(renderParams);
      }
   }

   public void free() {
      GlDhFogShader_neoforge.INSTANCE.free();
      GlDhFogApplyShader_neoforge.INSTANCE.free();
   }
}
