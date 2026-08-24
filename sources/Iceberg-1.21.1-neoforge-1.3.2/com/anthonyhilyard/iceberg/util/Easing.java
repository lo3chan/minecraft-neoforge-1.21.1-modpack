package com.anthonyhilyard.iceberg.util;

import net.minecraft.network.chat.TextColor;

public final class Easing {
   public static float Ease(float a, float b, float t) {
      return Ease(a, b, t, Easing.EasingType.Quad);
   }

   public static float Ease(float a, float b, float t, Easing.EasingType type) {
      return Ease(a, b, t, type, Easing.EasingDirection.InOut);
   }

   public static TextColor Ease(TextColor a, TextColor b, float t, Easing.EasingType type) {
      int aV = a.getValue();
      int bV = b.getValue();
      int aA = aV >> 24 & 0xFF;
      int aR = aV >> 16 & 0xFF;
      int aG = aV >> 8 & 0xFF;
      int aB = aV >> 0 & 0xFF;
      int bA = bV >> 24 & 0xFF;
      int bR = bV >> 16 & 0xFF;
      int bG = bV >> 8 & 0xFF;
      int bB = bV >> 0 & 0xFF;
      return TextColor.fromRgb(
         (int)Ease(aA, bA, t, type) << 24 | (int)Ease(aR, bR, t, type) << 16 | (int)Ease(aG, bG, t, type) << 8 | (int)Ease(aB, bB, t, type) << 0
      );
   }

   public static float Ease(float a, float b, float t, Easing.EasingType type, Easing.EasingDirection direction) {
      switch (type) {
         case None:
         default:
            return None(a, b, t);
         case Linear:
            return Linear(a, b, t);
         case Quad:
            return Quad(a, b, t, direction);
         case Cubic:
            return Cubic(a, b, t, direction);
      }
   }

   private static float None(float a, float b, float t) {
      return t < 0.5F ? a : b;
   }

   private static float Linear(float a, float b, float t) {
      return a + (b - a) * t;
   }

   private static float Quad(float a, float b, float t, Easing.EasingDirection direction) {
      switch (direction) {
         case In:
            return a + (b - a) * t * t;
         case Out:
            return a + (b - a) * (1.0F - (1.0F - t) * (1.0F - t));
         case InOut:
         default:
            t *= 2.0F;
            if (t < 1.0F) {
               return a + (b - a) * 0.5F * t * t;
            } else {
               t -= 2.0F;
               return a + (a - b) * 0.5F * (t * t - 2.0F);
            }
      }
   }

   private static float Cubic(float a, float b, float t, Easing.EasingDirection direction) {
      switch (direction) {
         case In:
            return a + (b - a) * t * t * t;
         case Out:
            return a + (b - a) * (1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t));
         case InOut:
         default:
            t *= 2.0F;
            if (t < 1.0F) {
               return a + (b - a) * 0.5F * t * t * t;
            } else {
               t -= 2.0F;
               return a + (b - a) * 0.5F * (t * t * t + 2.0F);
            }
      }
   }

   public static enum EasingDirection {
      In,
      Out,
      InOut;
   }

   public static enum EasingType {
      None,
      Linear,
      Quad,
      Cubic;
   }
}
