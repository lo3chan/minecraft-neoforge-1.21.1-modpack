package com.seibel.distanthorizons.common.render.openGl.glObject.texture;

import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL43C;

public class GlDhDepthTexture {
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private int id;

   public GlDhDepthTexture(int width, int height, EGlDhDepthBufferFormat format) {
      this.id = GL43C.glGenTextures();
      this.resize(width, height, format);
      GL43C.glTexParameteri(3553, 10241, 9728);
      GL43C.glTexParameteri(3553, 10240, 9728);
      GL43C.glTexParameteri(3553, 10242, 33071);
      GL43C.glTexParameteri(3553, 10243, 33071);
      GL43C.glTexParameteri(3553, 33084, 0);
      GL43C.glTexParameteri(3553, 33085, 0);
      GL43C.glBindTexture(3553, 0);
   }

   public GlDhDepthTexture(int id) {
      this.id = id;
   }

   public void resize(int width, int height, EGlDhDepthBufferFormat format) {
      GL43C.glBindTexture(3553, this.getTextureId());
      GL43C.glTexImage2D(3553, 0, format.getGlInternalFormat(), width, height, 0, format.getGlType(), format.getGlFormat(), (ByteBuffer)null);
   }

   public int getTextureId() {
      if (this.id == -1) {
         throw new IllegalStateException("Depth texture does not exist!");
      } else {
         return this.id;
      }
   }

   public void destroy() {
      GLMC.glDeleteTextures(this.getTextureId());
      this.id = -1;
   }
}
