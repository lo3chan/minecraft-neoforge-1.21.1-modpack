package com.seibel.distanthorizons.common.render.openGl.terrain;

import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.render.AbstractBlockTextureAtlas;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL32;

public class GlBlockTextureAtlas extends AbstractBlockTextureAtlas {
   public static final GlBlockTextureAtlas INSTANCE = new GlBlockTextureAtlas();
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   public static final int GL_BOUND_INDEX = 1;
   private final GlBlockTextureAtlas.PixelUploadGlState uploadGlState = new GlBlockTextureAtlas.PixelUploadGlState();
   private int textureId = 0;

   private GlBlockTextureAtlas() {
   }

   @Override
   protected void tryCreateOrResize(int width, int height) {
      if (this.textureId != 0) {
         GL32.glDeleteTextures(this.textureId);
      }

      this.textureId = GL32.glGenTextures();
      GL32.glBindTexture(3553, this.textureId);
      GL32.glTexImage2D(3553, 0, 32856, width, height, 0, 6408, 5121, (ByteBuffer)null);
      GL32.glTexParameteri(3553, 10241, 9728);
      GL32.glTexParameteri(3553, 10240, 9728);
      GL32.glTexParameteri(3553, 10242, 10497);
      GL32.glTexParameteri(3553, 10243, 10497);
      GL32.glTexParameteri(3553, 33085, 0);
   }

   public void bind() {
      GL32.glActiveTexture(33985);
      GL32.glBindTexture(3553, this.textureId);
      GL32.glActiveTexture(33984);
   }

   public void unbind() {
      GL32.glActiveTexture(33985);
      GL32.glBindTexture(3553, 0);
      GL32.glActiveTexture(33984);
   }

   @Override
   protected void beforeWriteToTexture() {
      this.uploadGlState.saveState();
      GL32.glBindTexture(3553, this.textureId);
      GL32.glPixelStorei(3314, 16);
      GL32.glPixelStorei(3316, 0);
      GL32.glPixelStorei(3315, 0);
      GL32.glPixelStorei(3317, 1);
   }

   @Override
   protected void writeToTexture(ByteBuffer pixelBuffer, int destinationX, int destinationY, int tileWidth, int tileHeight) {
      GL32.glTexSubImage2D(3553, 0, destinationX, destinationY, tileWidth, tileHeight, 6408, 5121, pixelBuffer);
   }

   @Override
   protected void afterWriteToTexture() {
      this.uploadGlState.close();
   }

   private static class PixelUploadGlState implements AutoCloseable {
      private int unpackRowLength = 0;
      private int unpackSkipPixels = 0;
      private int unpackSkipRows = 0;
      private int unpackAlignment = 0;
      private int textureBinding = 0;

      public void saveState() {
         this.unpackRowLength = GL32.glGetInteger(3314);
         this.unpackSkipPixels = GL32.glGetInteger(3316);
         this.unpackSkipRows = GL32.glGetInteger(3315);
         this.unpackAlignment = GL32.glGetInteger(3317);
         GlBlockTextureAtlas.GLMC.glActiveTexture(33984);
         this.textureBinding = GL32.glGetInteger(32873);
      }

      @Override
      public void close() {
         GL32.glBindTexture(3553, this.textureBinding);
         GL32.glPixelStorei(3314, this.unpackRowLength);
         GL32.glPixelStorei(3316, this.unpackSkipPixels);
         GL32.glPixelStorei(3315, this.unpackSkipRows);
         GL32.glPixelStorei(3317, this.unpackAlignment);
      }
   }
}
