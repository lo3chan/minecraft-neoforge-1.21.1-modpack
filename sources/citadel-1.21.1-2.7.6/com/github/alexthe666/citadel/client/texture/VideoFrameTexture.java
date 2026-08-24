package com.github.alexthe666.citadel.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.FastColor.ABGR32;

public class VideoFrameTexture extends DynamicTexture {
   public VideoFrameTexture(NativeImage image) {
      super(image);
   }

   public void setPixels(NativeImage nativeImage) {
      super.setPixels(nativeImage);
      if (this.getPixels() != null) {
         TextureUtil.prepareImage(this.getId(), this.getPixels().getWidth(), this.getPixels().getHeight());
         this.upload();
      }
   }

   public void setPixelsFromBufferedImage(BufferedImage bufferedImage) {
      for (int i = 0; i < Math.min(this.getPixels().getWidth(), bufferedImage.getWidth()); i++) {
         for (int j = 0; j < Math.min(this.getPixels().getHeight(), bufferedImage.getHeight()); j++) {
            int color = bufferedImage.getRGB(i, j);
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            this.getPixels().setPixelRGBA(i, j, ABGR32.color(255, b, g, r));
         }
      }

      this.upload();
   }
}
