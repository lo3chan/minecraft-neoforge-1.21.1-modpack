package com.seibel.distanthorizons.common.render.openGl.glObject.texture;

import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import java.nio.ByteBuffer;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL43C;

public class GlDhColorTexture {
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private final EGlDhInternalTextureFormat internalFormat;
   private final EGlDhPixelFormat format;
   private final EGlDhPixelType type;
   private int width;
   private int height;
   private boolean isValid = true;
   private final int id;
   private static final ByteBuffer NULL_BUFFER = null;

   public GlDhColorTexture(GlDhColorTexture.Builder builder) {
      this.internalFormat = builder.internalFormat;
      this.format = builder.format;
      this.type = builder.type;
      this.width = builder.width;
      this.height = builder.height;
      this.id = GL43C.glGenTextures();
      boolean isPixelFormatInteger = builder.internalFormat.getPixelFormat().isInteger();
      this.setupTexture(this.id, builder.width, builder.height, !isPixelFormatInteger);
      GL43C.glBindTexture(3553, 0);
   }

   private void setupTexture(int id, int width, int height, boolean allowsLinear) {
      this.resizeTexture(id, width, height);
      GL43C.glTexParameteri(3553, 10241, allowsLinear ? 9729 : 9728);
      GL43C.glTexParameteri(3553, 10240, allowsLinear ? 9729 : 9728);
      GL43C.glTexParameteri(3553, 10242, 33071);
      GL43C.glTexParameteri(3553, 10243, 33071);
      GL43C.glTexParameteri(3553, 33084, 0);
      GL43C.glTexParameteri(3553, 33085, 0);
   }

   private void resizeTexture(int texture, int width, int height) {
      GL43C.glBindTexture(3553, texture);
      GL43C.glTexImage2D(3553, 0, this.internalFormat.getGlFormat(), width, height, 0, this.format.getGlFormat(), this.type.getGlFormat(), NULL_BUFFER);
   }

   void resize(Vector2i textureScaleOverride) {
      this.resize(textureScaleOverride.x, textureScaleOverride.y);
   }

   public void resize(int width, int height) {
      this.throwIfInvalid();
      this.width = width;
      this.height = height;
      this.resizeTexture(this.id, width, height);
   }

   public EGlDhInternalTextureFormat getInternalFormat() {
      return this.internalFormat;
   }

   public int getTextureId() {
      this.throwIfInvalid();
      return this.id;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void destroy() {
      this.throwIfInvalid();
      this.isValid = false;
      GLMC.glDeleteTextures(this.id);
   }

   private void throwIfInvalid() {
      if (!this.isValid) {
         throw new IllegalStateException("Attempted to use a deleted composite render target");
      }
   }

   public static GlDhColorTexture.Builder builder() {
      return new GlDhColorTexture.Builder();
   }

   public static class Builder {
      private EGlDhInternalTextureFormat internalFormat = EGlDhInternalTextureFormat.RGBA8;
      private int width = 0;
      private int height = 0;
      private EGlDhPixelFormat format = EGlDhPixelFormat.RGBA;
      private EGlDhPixelType type = EGlDhPixelType.UNSIGNED_BYTE;

      private Builder() {
      }

      public GlDhColorTexture.Builder setInternalFormat(EGlDhInternalTextureFormat format) {
         this.internalFormat = format;
         return this;
      }

      public GlDhColorTexture.Builder setDimensions(int width, int height) {
         if (width <= 0) {
            throw new IllegalArgumentException("Width must be greater than zero");
         } else if (height <= 0) {
            throw new IllegalArgumentException("Height must be greater than zero");
         } else {
            this.width = width;
            this.height = height;
            return this;
         }
      }

      public GlDhColorTexture.Builder setPixelFormat(EGlDhPixelFormat pixelFormat) {
         this.format = pixelFormat;
         return this;
      }

      public GlDhColorTexture.Builder setPixelType(EGlDhPixelType pixelType) {
         this.type = pixelType;
         return this;
      }

      public GlDhColorTexture build() {
         return new GlDhColorTexture(this);
      }
   }
}
