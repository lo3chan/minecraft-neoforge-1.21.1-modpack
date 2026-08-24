package com.seibel.distanthorizons.common.wrappers.misc;

import com.mojang.blaze3d.platform.NativeImage;
import com.seibel.distanthorizons.common.render.blaze.wrappers.texture.BlazeTextureViewWrapper;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.ILightMapWrapper;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL33;

public class LightMapWrapper_neoforge implements ILightMapWrapper {
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final int GL_BOUND_INDEX = 0;
   private int textureId = 0;
   private final BlazeTextureViewWrapper lightmapTextureWrapper = new BlazeTextureViewWrapper();

   public void uploadLightmap(NativeImage image) {
      int currentTexture = GLMC.getActiveTexture();
      if (this.textureId == 0) {
         this.createLightmap(image);
      } else {
         GLMC.glBindTexture(this.textureId);
      }

      image.upload(0, 0, 0, false);
      if (GL33.glIsTexture(currentTexture)) {
         GLMC.glBindTexture(currentTexture);
      }
   }

   private void createLightmap(NativeImage image) {
      this.textureId = GLMC.glGenTextures();
      GLMC.glBindTexture(this.textureId);
      GL33.glTexImage2D(3553, 0, image.format().glFormat(), image.getWidth(), image.getHeight(), 0, image.format().glFormat(), 5121, (ByteBuffer)null);
   }

   public void setLightmapId(int minecraftLightmapTextureId) {
      this.textureId = minecraftLightmapTextureId;
   }

   public BlazeTextureViewWrapper getTextureViewWrapper() {
      return this.lightmapTextureWrapper;
   }

   public int getOpenGlId() {
      return this.textureId;
   }
}
