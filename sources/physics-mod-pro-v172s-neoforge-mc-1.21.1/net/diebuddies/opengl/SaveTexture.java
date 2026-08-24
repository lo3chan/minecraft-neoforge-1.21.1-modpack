package net.diebuddies.opengl;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

public class SaveTexture {
   public static void save(File file, int openglID) {
      GL32C.glBindTexture(3553, openglID);
      int width = GL32C.glGetTexLevelParameteri(3553, 0, 4096);
      int height = GL32C.glGetTexLevelParameteri(3553, 0, 4097);
      ByteBuffer buffer = MemoryUtil.memCalloc(width * height * 4);
      GL32C.glGetTexImage(3553, 0, 6408, 5121, buffer);

      try {
         if (file.exists()) {
            file.delete();
         }

         Files.createDirectories(file.getAbsoluteFile().getParentFile().toPath());
         STBImageWrite.stbi_write_png(file.getAbsolutePath(), width, height, 4, buffer, width * 4);
      } catch (IOException var6) {
         var6.printStackTrace();
      }

      MemoryUtil.memFree(buffer);
   }

   public static void saveFramebuffer(File file, int framebufferID) {
      GL32C.glReadBuffer(framebufferID);
      int fboTextureID = GL33C.glGetFramebufferAttachmentParameteri(36009, framebufferID, 36049);
      GL32C.glBindTexture(3553, fboTextureID);
      int width = GL32C.glGetTexLevelParameteri(3553, 0, 4096);
      int height = GL32C.glGetTexLevelParameteri(3553, 0, 4097);
      int textureID = GL32C.glGenTextures();
      GL32C.glBindTexture(3553, textureID);
      GL32C.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, (ByteBuffer)null);
      GL32C.glTexParameteri(3553, 10241, 9728);
      GL32C.glTexParameteri(3553, 10240, 9728);
      GL32C.glTexParameteri(3553, 10242, 33071);
      GL32C.glTexParameteri(3553, 10243, 33071);
      GL32C.glCopyTexImage2D(3553, 0, 6408, 0, 0, width, height, 0);
      int format = 6408;
      System.out.println(width + ", " + height + ", " + format);
      ByteBuffer buffer = MemoryUtil.memCalloc(width * height * 4);
      GL32C.glGetTexImage(3553, 0, format, 5121, buffer);

      try {
         if (file.exists()) {
            file.delete();
         }

         Files.createDirectories(file.getAbsoluteFile().getParentFile().toPath());
         STBImageWrite.stbi_flip_vertically_on_write(true);
         STBImageWrite.stbi_write_png(file.getAbsolutePath(), width, height, 4, buffer, width * 4);
         STBImageWrite.stbi_flip_vertically_on_write(false);
      } catch (IOException var9) {
         var9.printStackTrace();
      }

      MemoryUtil.memFree(buffer);
   }

   public static void saveFramebufferDepth(File file) {
      int fboTextureID = GL33C.glGetFramebufferAttachmentParameteri(36009, 36096, 36049);
      GL32C.glBindTexture(3553, fboTextureID);
      int width = GL32C.glGetTexLevelParameteri(3553, 0, 4096);
      int height = GL32C.glGetTexLevelParameteri(3553, 0, 4097);
      int internalFormat = GL32C.glGetTexLevelParameteri(3553, 0, 4099);
      int textureID = GL32C.glGenTextures();
      GL32C.glBindTexture(3553, textureID);
      GL32C.glTexImage2D(3553, 0, internalFormat, width, height, 0, 6402, 5126, (ByteBuffer)null);
      GL32C.glTexParameteri(3553, 10241, 9728);
      GL32C.glTexParameteri(3553, 10240, 9728);
      GL32C.glTexParameteri(3553, 10242, 33071);
      GL32C.glTexParameteri(3553, 10243, 33071);
      GL32C.glCopyTexImage2D(3553, 0, internalFormat, 0, 0, width, height, 0);
      System.out.println(width + ", " + height + ", " + internalFormat);
      FloatBuffer depthbuffer = MemoryUtil.memCallocFloat(width * height);
      GL32C.glGetTexImage(3553, 0, internalFormat, 5126, depthbuffer);
      ByteBuffer buffer = MemoryUtil.memCalloc(width * height * 4);

      for (int i = 0; i < width * height; i++) {
         int index = i * 4;
         float val = depthbuffer.get(i);
         buffer.put(index, (byte)(val * 255.0F));
         buffer.put(index + 1, (byte)(val * 255.0F));
         buffer.put(index + 2, (byte)(val * 255.0F));
         buffer.put(index + 3, (byte)-1);
      }

      try {
         if (file.exists()) {
            file.delete();
         }

         Files.createDirectories(file.getAbsoluteFile().getParentFile().toPath());
         STBImageWrite.stbi_flip_vertically_on_write(true);
         STBImageWrite.stbi_write_png(file.getAbsolutePath(), width, height, 4, buffer, width * 4);
         STBImageWrite.stbi_flip_vertically_on_write(false);
      } catch (IOException var11) {
         var11.printStackTrace();
      }

      MemoryUtil.memFree(depthbuffer);
      MemoryUtil.memFree(buffer);
   }

   public static Texture copyFramebufferDepthTexture(Texture texture) {
      int fboTextureID = GL33C.glGetFramebufferAttachmentParameteri(36009, 36096, 36049);
      GL32C.glBindTexture(3553, fboTextureID);
      int width = GL32C.glGetTexLevelParameteri(3553, 0, 4096);
      int height = GL32C.glGetTexLevelParameteri(3553, 0, 4097);
      int internalFormat = GL32C.glGetTexLevelParameteri(3553, 0, 4099);
      if (texture != null && texture.getWidth() == width && texture.getHeight() == height && texture.getInternalFormat() == internalFormat) {
         GL32C.glBindTexture(3553, texture.getID());
      } else {
         if (texture != null) {
            texture.destroy();
         }

         int textureID = GL32C.glGenTextures();
         if (texture == null) {
            texture = new Texture(textureID);
         }

         GL32C.glBindTexture(3553, textureID);
         GL32C.glTexImage2D(3553, 0, internalFormat, width, height, 0, 6402, 5126, (ByteBuffer)null);
         GL32C.glTexParameteri(3553, 10241, 9728);
         GL32C.glTexParameteri(3553, 10240, 9728);
         GL32C.glTexParameteri(3553, 10242, 33071);
         GL32C.glTexParameteri(3553, 10243, 33071);
         texture.setId(textureID);
         texture.setWidth(width);
         texture.setHeight(height);
         texture.setInternalFormat(internalFormat);
      }

      GL32C.glCopyTexImage2D(3553, 0, internalFormat, 0, 0, width, height, 0);
      return texture;
   }
}
