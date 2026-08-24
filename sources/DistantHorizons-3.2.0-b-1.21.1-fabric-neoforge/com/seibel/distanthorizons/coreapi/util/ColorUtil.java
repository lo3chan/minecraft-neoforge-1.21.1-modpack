package com.seibel.distanthorizons.coreapi.util;

import java.awt.Color;

public class ColorUtil {
   public static final int INVISIBLE = argbToInt(0, 0, 0, 0);
   public static final int BLACK = rgbToInt(0, 0, 0);
   public static final int WHITE = rgbToInt(255, 255, 255);
   public static final int RED = rgbToInt(255, 0, 0);
   public static final int DARK_RED = rgbToInt(100, 0, 0);
   public static final int GREEN = rgbToInt(0, 255, 0);
   public static final int DARK_GREEN = rgbToInt(80, 140, 80);
   public static final int BLUE = rgbToInt(0, 0, 255);
   public static final int YELLOW = rgbToInt(255, 255, 0);
   public static final int CYAN = rgbToInt(0, 255, 255);
   public static final int MAGENTA = rgbToInt(255, 0, 255);
   public static final int ORANGE = rgbToInt(255, 128, 0);
   public static final int DARK_ORANGE = rgbToInt(125, 62, 0);
   public static final int TAN = rgbToInt(183, 165, 119);
   public static final int PINK = rgbToInt(255, 128, 128);
   public static final int HOT_PINK = rgbToInt(255, 105, 180);
   public static final int GRAY = rgbToInt(128, 128, 128);
   public static final int LIGHT_GRAY = rgbToInt(192, 192, 192);
   public static final int DARK_GRAY = rgbToInt(64, 64, 64);
   public static final int BROWN = rgbToInt(68, 46, 24);
   public static final int LIGHT_BROWN = rgbToInt(130, 112, 67);
   public static final int PURPLE = rgbToInt(128, 0, 128);

   public static int rgbToInt(int red, int green, int blue) {
      return 0xFF000000 | red << 16 | green << 8 | blue;
   }

   public static int argbToInt(int alpha, int red, int green, int blue) {
      return alpha << 24 | red << 16 | green << 8 | blue;
   }

   public static int argbToInt(float alpha, float red, float green, float blue) {
      return argbToInt((int)(alpha * 255.0F), (int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F));
   }

   public static int getAlpha(int color) {
      return color >>> 24 & 0xFF;
   }

   public static int setAlpha(int color, int newAlpha) {
      return newAlpha << 24 | getRed(color) << 16 | getGreen(color) << 8 | getBlue(color);
   }

   public static int getRed(int color) {
      return color >> 16 & 0xFF;
   }

   public static int setRed(int color, int newRed) {
      return getAlpha(color) << 24 | newRed << 16 | getGreen(color) << 8 | getBlue(color);
   }

   public static int getGreen(int color) {
      return color >> 8 & 0xFF;
   }

   public static int setGreen(int color, int newGreen) {
      return getAlpha(color) << 24 | getRed(color) << 16 | newGreen << 8 | getBlue(color);
   }

   public static int getBlue(int color) {
      return color & 0xFF;
   }

   public static int setBlue(int color, int newBlue) {
      return getAlpha(color) << 24 | getRed(color) << 16 | getGreen(color) << 8 | newBlue;
   }

   public static void throwIfColorValueOutOfIntRange(String colorName, int value) throws IllegalArgumentException {
      if (value < 0 || value > 255) {
         throw new IllegalArgumentException("[" + colorName + "] with the value [" + value + "] is out of the expected range 0 - 255 (exclusive).");
      }
   }

   public static int applyShade(int color, int shade) {
      return shade < 0
         ? getAlpha(color) << 24 | Math.max(getRed(color) + shade, 0) << 16 | Math.max(getGreen(color) + shade, 0) << 8 | Math.max(getBlue(color) + shade, 0)
         : getAlpha(color) << 24
            | Math.min(getRed(color) + shade, 255) << 16
            | Math.min(getGreen(color) + shade, 255) << 8
            | Math.min(getBlue(color) + shade, 255);
   }

   public static int applyShade(int color, float shade) {
      return shade < 1.0F
         ? getAlpha(color) << 24
            | (int)Math.max(getRed(color) * shade, 0.0F) << 16
            | (int)Math.max(getGreen(color) * shade, 0.0F) << 8
            | (int)Math.max(getBlue(color) * shade, 0.0F)
         : getAlpha(color) << 24
            | (int)Math.min(getRed(color) * shade, 255.0F) << 16
            | (int)Math.min(getGreen(color) * shade, 255.0F) << 8
            | (int)Math.min(getBlue(color) * shade, 255.0F);
   }

   public static int multiplyARGBwithRGB(int argb, int rgb) {
      return getAlpha(argb) << 24 | getRed(argb) * getRed(rgb) / 255 << 16 | getGreen(argb) * getGreen(rgb) / 255 << 8 | getBlue(argb) * getBlue(rgb) / 255;
   }

   public static int multiplyARGBwithARGB(int color1, int color2) {
      return getAlpha(color1) * getAlpha(color2) / 255 << 24
         | getRed(color1) * getRed(color2) / 255 << 16
         | getGreen(color1) * getGreen(color2) / 255 << 8
         | getBlue(color1) * getBlue(color2) / 255;
   }

   public static float[] argbToAhsv(int color) {
      float a = getAlpha(color) / 255.0F;
      float r = getRed(color) / 255.0F;
      float g = getGreen(color) / 255.0F;
      float b = getBlue(color) / 255.0F;
      float min = Math.min(Math.min(r, g), b);
      float max = Math.max(Math.max(r, g), b);
      float delta = max - min;
      if (max != 0.0F) {
         float s = delta / max;
         float h;
         if (delta == 0.0F) {
            h = 0.0F;
         } else {
            if (r == max) {
               h = (g - b) / delta;
            } else if (g == max) {
               h = 2.0F + (b - r) / delta;
            } else {
               h = 4.0F + (r - g) / delta;
            }

            h *= 60.0F;
            if (h < 0.0F) {
               h += 360.0F;
            }
         }

         return new float[]{a, h, s, max};
      } else {
         return new float[]{a, 0.0F, 0.0F, 0.0F};
      }
   }

   public static int ahsvToArgb(float a, float h, float s, float v) {
      if (a > 1.0F) {
         a = 1.0F;
      }

      if (h > 360.0F) {
         h -= 350.0F;
      }

      if (s > 1.0F) {
         s = 1.0F;
      }

      if (v > 1.0F) {
         v = 1.0F;
      }

      if (s == 0.0F) {
         return argbToInt(a, v, v, v);
      } else {
         h /= 60.0F;
         int i = (int)Math.floor(h);
         float f = h - i;
         float p = v * (1.0F - s);
         float q = v * (1.0F - s * f);
         float t = v * (1.0F - s * (1.0F - f));
         switch (i) {
            case 0:
               return argbToInt(a, v, t, p);
            case 1:
               return argbToInt(a, q, v, p);
            case 2:
               return argbToInt(a, p, v, t);
            case 3:
               return argbToInt(a, p, q, v);
            case 4:
               return argbToInt(a, t, p, v);
            default:
               return argbToInt(a, v, p, q);
         }
      }
   }

   public static String toHexString(int color) {
      return "A:"
         + Integer.toHexString(getAlpha(color))
         + ",R:"
         + Integer.toHexString(getRed(color))
         + ",G:"
         + Integer.toHexString(getGreen(color))
         + ",B:"
         + Integer.toHexString(getBlue(color));
   }

   public static String toString(int color) {
      return "A:" + getAlpha(color) + ",R:" + getRed(color) + ",G:" + getGreen(color) + ",B:" + getBlue(color);
   }

   public static Color toColorObjRGB(int color) {
      return new Color(getRed(color), getGreen(color), getBlue(color));
   }

   public static Color toColorObjARGB(int color) {
      return new Color(getRed(color), getGreen(color), getBlue(color), getAlpha(color));
   }

   public static int toColorInt(Color color) {
      return argbToInt(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
   }
}
