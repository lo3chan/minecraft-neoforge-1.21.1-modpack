package com.anthonyhilyard.prism.util;

import net.minecraft.util.Mth;
import org.apache.commons.lang3.math.NumberUtils;

public class ColorUtil {
   private static int round(float value) {
      return (int)(value * 255.0F + 0.5F);
   }

   public static int combineARGB(int a, int r, int g, int b) {
      a = Mth.clamp(a, 0, 255);
      r = Mth.clamp(r, 0, 255);
      g = Mth.clamp(g, 0, 255);
      b = Mth.clamp(b, 0, 255);
      return a << 24 | r << 16 | g << 8 | b << 0;
   }

   public static int combineRGB(int r, int g, int b) {
      return combineARGB(255, r, g, b);
   }

   public static int HSVtoRGB(int hue, int saturation, int value) {
      return AHSVtoARGB(255, hue, saturation, value);
   }

   public static int HSVtoRGB(float hue, float saturation, float value) {
      return AHSVtoARGB(1.0F, hue, saturation, value);
   }

   public static int AHSVtoARGB(int alpha, int hue, int saturation, int value) {
      return AHSVtoARGB(alpha / 255.0F, hue / 360.0F, saturation / 255.0F, value / 255.0F);
   }

   public static int AHSVtoARGB(float alpha, float hue, float saturation, float value) {
      alpha = Mth.clamp(alpha, 0.0F, 1.0F);
      hue = Mth.clamp(hue, 0.0F, 1.0F);
      saturation = Mth.clamp(saturation, 0.0F, 1.0F);
      value = Mth.clamp(value, 0.0F, 1.0F);
      int r = 0;
      int g = 0;
      int b = 0;
      if (saturation == 0.0F) {
         r = g = b = round(value);
      } else {
         int h = (int)(hue * 6.0F);
         float ff = hue * 6.0F - h;
         float p = value * (1.0F - saturation);
         float q = value * (1.0F - saturation * ff);
         float t = value * (1.0F - saturation * (1.0F - ff));
         switch (h) {
            case 0:
               r = round(value);
               g = round(t);
               b = round(p);
               break;
            case 1:
               r = round(q);
               g = round(value);
               b = round(p);
               break;
            case 2:
               r = round(p);
               g = round(value);
               b = round(t);
               break;
            case 3:
               r = round(p);
               g = round(q);
               b = round(value);
               break;
            case 4:
               r = round(t);
               g = round(p);
               b = round(value);
               break;
            case 5:
               r = round(value);
               g = round(p);
               b = round(q);
         }
      }

      return combineARGB(round(alpha), r, g, b);
   }

   public static float[] RGBtoHSV(float r, float g, float b) {
      float[] result = ARGBtoAHSV(1.0F, r, g, b);
      return new float[]{result[1], result[2], result[3]};
   }

   public static float[] RGBtoHSV(int r, int g, int b) {
      float[] result = ARGBtoAHSV(255, r, g, b);
      return new float[]{result[1], result[2], result[3]};
   }

   public static float[] ARGBtoAHSV(float a, float r, float g, float b) {
      return ARGBtoAHSV(round(a), round(r), round(g), round(b));
   }

   public static float[] ARGBtoAHSV(int a, int r, int g, int b) {
      a = Mth.clamp(a, 0, 255);
      r = Mth.clamp(r, 0, 255);
      g = Mth.clamp(g, 0, 255);
      b = Mth.clamp(b, 0, 255);
      int cmax = NumberUtils.max(r, g, b);
      int cmin = NumberUtils.min(r, g, b);
      float value = cmax / 255.0F;
      float saturation;
      if (cmax != 0) {
         saturation = (float)(cmax - cmin) / cmax;
      } else {
         saturation = 0.0F;
      }

      float hue;
      if (saturation == 0.0F) {
         hue = 0.0F;
      } else {
         float redc = (float)(cmax - r) / (cmax - cmin);
         float greenc = (float)(cmax - g) / (cmax - cmin);
         float bluec = (float)(cmax - b) / (cmax - cmin);
         if (r == cmax) {
            hue = bluec - greenc;
         } else if (g == cmax) {
            hue = 2.0F + redc - bluec;
         } else {
            hue = 4.0F + greenc - redc;
         }

         hue /= 6.0F;
         if (hue < 0.0F) {
            hue++;
         }
      }

      return new float[]{a, hue, saturation, value};
   }
}
