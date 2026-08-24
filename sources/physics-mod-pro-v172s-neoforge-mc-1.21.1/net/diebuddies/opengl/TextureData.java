package net.diebuddies.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.stb.STBImage;

public class TextureData {
   public ByteBuffer[] buffer;
   public FloatBuffer[] fbuffer;
   public int width;
   public int height;
   public int internalFormat;
   public int format;
   public int type;
   private String path;

   public TextureData(String path, FloatBuffer[] buffer, int format, int internalFormat, int type, int width, int height) {
      this.fbuffer = buffer;
      this.width = width;
      this.internalFormat = internalFormat;
      this.height = height;
      this.format = format;
      this.type = type;
      this.path = path;
   }

   public TextureData(String path, ByteBuffer[] buffer, int format, int internalFormat, int type, int width, int height) {
      this.buffer = buffer;
      this.width = width;
      this.internalFormat = internalFormat;
      this.height = height;
      this.format = format;
      this.type = type;
      this.path = path;
   }

   public TextureData(String path, ByteBuffer buffer, int format, int internalFormat, int type, int width, int height) {
      this(path, new ByteBuffer[]{buffer}, format, internalFormat, type, width, height);
   }

   public TextureData(String path, ByteBuffer buffer, int format, int width, int height) {
      this(path, buffer, format, format, 5121, width, height);
   }

   public ByteBuffer[] getBuffer() {
      return this.buffer;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getInternalFormat() {
      return this.internalFormat;
   }

   public int getFormat() {
      return this.format;
   }

   public int getType() {
      return this.type;
   }

   public String getPath() {
      return this.path;
   }

   public void destroy() {
      for (ByteBuffer buffer : this.buffer) {
         STBImage.stbi_image_free(buffer);
      }
   }
}
