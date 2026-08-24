package com.seibel.distanthorizons.common.render.openGl.glObject;

import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiFramebuffer;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import org.lwjgl.opengl.GL33;

public class GlDhFramebuffer implements IDhApiFramebuffer {
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private int id;

   public GlDhFramebuffer() {
      this.id = GL33.glGenFramebuffers();
   }

   public GlDhFramebuffer(int id) {
      this.id = id;
   }

   @Override
   public void addDepthAttachment(int textureId, boolean isCombinedStencil) {
      this.bind();
      int depthAttachment = isCombinedStencil ? '舚' : '贀';
      GL33.glFramebufferTexture2D(36160, depthAttachment, 3553, textureId, 0);
   }

   @Override
   public void addColorAttachment(int textureIndex, int textureId) {
      this.bind();
      GL33.glFramebufferTexture2D(36160, 36064 + textureIndex, 3553, textureId, 0);
   }

   @Override
   public void bind() {
      if (this.id == -1) {
         throw new IllegalStateException("Framebuffer does not exist!");
      } else {
         GLMC.glBindFramebuffer(36160, this.id);
      }
   }

   @Override
   public void destroy() {
      GL33.glDeleteFramebuffers(this.id);
      this.id = -1;
   }

   @Override
   public int getStatus() {
      this.bind();
      return GL33.glCheckFramebufferStatus(36160);
   }

   @Override
   public int getId() {
      return this.id;
   }

   @Override
   public boolean overrideThisFrame() {
      return true;
   }
}
