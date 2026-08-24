package net.cibernet.alchemancy.util;

import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;

public class ColorUtils {
   public static int interpolateColorsOverTime(float timePerColor, int... colors) {
      float progress = (float)(System.currentTimeMillis() % ((long)(timePerColor * 1000.0F) * colors.length)) / 1000.0F / timePerColor;
      return ARGB32.lerp(progress % 1.0F, colors[(int)progress % colors.length], colors[((int)progress + 1) % colors.length]);
   }

   public static int interpolateColorsAndWait(float lerpTime, float waitTime, int... colors) {
      float progress = (float)(System.currentTimeMillis() % ((long)((lerpTime + waitTime) * 1000.0F) * colors.length)) / 1000.0F / (lerpTime + waitTime);
      return ARGB32.lerp(
         Math.min(1.0F, progress % 1.0F / (lerpTime / (lerpTime + waitTime))),
         colors[(int)progress % colors.length],
         colors[((int)progress + 1) % colors.length]
      );
   }

   public static int flashColorsOverTime(double time, int... colors) {
      return colors[(int)Math.abs(System.currentTimeMillis() / time % colors.length)];
   }

   public static int sineColorsOverTime(float time, int colorA, int colorB) {
      float partialSecond = (float)(System.currentTimeMillis() % (1000L * (long)time)) / 1000.0F;
      return ARGB32.lerp(Mth.sin(6.2831855F * (partialSecond / time)) * 0.5F + 0.5F, colorA, colorB);
   }

   public static String colorToHexString(int color) {
      return String.format("%06X", color).toUpperCase();
   }
}
