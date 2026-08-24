package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import net.minecraft.class_1058;

public class TextureAtlasSpriteWrapper_fabric {
   public static int getPixelARGB(class_1058 sprite, int frameIndex, int x, int y) {
      if (sprite.method_45851().field_40541 != null) {
         x += sprite.method_45851().field_40541.method_33446(frameIndex) * sprite.method_45851().method_45807();
         y += sprite.method_45851().field_40541.method_33451(frameIndex) * sprite.method_45851().method_45807();
      }

      int rgba = sprite.method_45851().field_40539.method_4315(x, y);
      return convertRgbaToArgb(rgba);
   }

   private static int convertRgbaToArgb(int rgba) {
      int r = rgba & 0xFF;
      int g = (rgba & 0xFF00) >>> 8;
      int b = (rgba & 0xFF0000) >>> 16;
      int a = (rgba & 0xFF000000) >>> 24;
      return ColorUtil.argbToInt(a, r, g, b);
   }

   public static int getWidth(class_1058 texture) {
      return texture.method_45851().method_45807();
   }

   public static int getHeight(class_1058 texture) {
      return texture.method_45851().method_45815();
   }

   public static float getMinU(class_1058 sprite) {
      return sprite.method_4594();
   }

   public static float getMaxU(class_1058 sprite) {
      return sprite.method_4577();
   }

   public static float getMinV(class_1058 sprite) {
      return sprite.method_4593();
   }

   public static float getMaxV(class_1058 sprite) {
      return sprite.method_4575();
   }
}
