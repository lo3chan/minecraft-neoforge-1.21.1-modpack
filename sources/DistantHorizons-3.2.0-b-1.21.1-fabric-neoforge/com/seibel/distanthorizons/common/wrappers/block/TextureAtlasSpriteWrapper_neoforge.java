package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class TextureAtlasSpriteWrapper_neoforge {
   public static int getPixelARGB(TextureAtlasSprite sprite, int frameIndex, int x, int y) {
      if (sprite.contents().animatedTexture != null) {
         x += sprite.contents().animatedTexture.getFrameX(frameIndex) * sprite.contents().width();
         y += sprite.contents().animatedTexture.getFrameY(frameIndex) * sprite.contents().width();
      }

      int rgba = sprite.contents().originalImage.getPixelRGBA(x, y);
      return convertRgbaToArgb(rgba);
   }

   private static int convertRgbaToArgb(int rgba) {
      int r = rgba & 0xFF;
      int g = (rgba & 0xFF00) >>> 8;
      int b = (rgba & 0xFF0000) >>> 16;
      int a = (rgba & 0xFF000000) >>> 24;
      return ColorUtil.argbToInt(a, r, g, b);
   }

   public static int getWidth(TextureAtlasSprite texture) {
      return texture.contents().width();
   }

   public static int getHeight(TextureAtlasSprite texture) {
      return texture.contents().height();
   }

   public static float getMinU(TextureAtlasSprite sprite) {
      return sprite.getU0();
   }

   public static float getMaxU(TextureAtlasSprite sprite) {
      return sprite.getU1();
   }

   public static float getMinV(TextureAtlasSprite sprite) {
      return sprite.getV0();
   }

   public static float getMaxV(TextureAtlasSprite sprite) {
      return sprite.getV1();
   }
}
