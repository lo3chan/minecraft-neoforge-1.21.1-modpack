package fuzs.puzzleslib.api.util.v1;

import net.minecraft.util.Mth;

public final class HSV {
   private HSV() {
   }

   public static int hue(int color) {
      return color >> 16 & 0xFF;
   }

   public static int saturation(int color) {
      return color >> 8 & 0xFF;
   }

   public static int value(int color) {
      return color & 0xFF;
   }

   public static int color(int hue, int saturation, int value) {
      return hue << 16 | saturation << 8 | value;
   }

   public static int colorFromFloat(float hue, float saturation, float value) {
      return color(ARGB.as8BitChannel(hue), ARGB.as8BitChannel(saturation), ARGB.as8BitChannel(value));
   }

   public static float hueFloat(int color) {
      return ARGB.from8BitChannel(hue(color));
   }

   public static float saturationFloat(int color) {
      return ARGB.from8BitChannel(saturation(color));
   }

   public static float valueFloat(int color) {
      return ARGB.from8BitChannel(value(color));
   }

   public static int rgbToHsv(int color) {
      return rgbToHsv(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color));
   }

   public static int rgbToHsv(float red, float green, float blue) {
      float max = Math.max(red, Math.max(green, blue));
      float min = Math.min(red, Math.min(green, blue));
      float delta = max - min;
      float hue = 0.0F;
      if (delta != 0.0F) {
         if (max == red) {
            hue = (green - blue) / delta % 6.0F;
         } else if (max == green) {
            hue = (blue - red) / delta + 2.0F;
         } else {
            hue = (red - green) / delta + 4.0F;
         }

         hue /= 6.0F;
         if (hue < 0.0F) {
            hue++;
         }
      }

      float saturation = max == 0.0F ? 0.0F : delta / max;
      return colorFromFloat(hue, saturation, max);
   }

   public static int hsvToRgb(int color) {
      return hsvToRgb(hueFloat(color), saturationFloat(color), valueFloat(color));
   }

   public static int hsvToRgb(float hue, float saturation, float value) {
      return Mth.hsvToRgb(hue, saturation, value);
   }
}
