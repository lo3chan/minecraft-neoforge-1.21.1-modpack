package dev.kosmx.playerAnim.core.util;

import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

public class Easing {
   @Deprecated
   public static float easingFromEnum(@Nullable Ease type, float f) {
      return type != null ? type.invoke(f) : f;
   }

   public static Ease easeFromString(String string) {
      try {
         if (string.substring(0, 4).equalsIgnoreCase("EASE")) {
            string = string.substring(4);
         }

         return Ease.valueOf(string.toUpperCase());
      } catch (Exception var2) {
         return Ease.LINEAR;
      }
   }

   public static float sine(float n) {
      return 1.0F - (float)Math.cos(n * 3.141592653589793 / 2.0);
   }

   public static float cubic(float n) {
      return n * n * n;
   }

   public static float quadratic(float n) {
      return n * n;
   }

   public static Function<Float, Float> pow(float n) {
      return t -> (float)Math.pow(t.floatValue(), n);
   }

   public static float exp(float n) {
      return (float)Math.pow(2.0, 10.0F * (n - 1.0F));
   }

   public static float circle(float n) {
      return 1.0F - (float)Math.sqrt(1.0F - n * n);
   }

   public static Function<Float, Float> back(Float n) {
      float n2 = n == null ? 1.70158F : n * 1.70158F;
      return t -> t * t * ((n2 + 1.0F) * t - n2);
   }

   public static Function<Float, Float> elastic(Float n) {
      float n2 = n == null ? 1.0F : n;
      return t -> (float)(1.0 - Math.pow(Math.cos(t.floatValue() * 3.141592653589793 / 2.0), 3.0) * Math.cos(t * n2 * 3.141592653589793));
   }

   public static Function<Float, Float> bounce(Float n) {
      float n2 = n == null ? 0.5F : n;
      Function<Float, Float> one = x -> 7.5625F * x * x;
      Function<Float, Float> two = x -> (float)(30.25F * n2 * Math.pow(x - 0.54545456F, 2.0) + 1.0 - n2);
      Function<Float, Float> three = x -> (float)(121.0F * n2 * n2 * Math.pow(x - 0.8181818F, 2.0) + 1.0 - n2 * n2);
      Function<Float, Float> four = x -> (float)(484.0F * n2 * n2 * n2 * Math.pow(x - 0.95454544F, 2.0) + 1.0 - n2 * n2 * n2);
      return t -> Math.min(Math.min(one.apply(t), two.apply(t)), Math.min(three.apply(t), four.apply(t)));
   }

   public static Function<Float, Float> step(Float n) {
      float n2 = n == null ? 2.0F : n;
      if (n2 < 2.0F) {
         throw new IllegalArgumentException("Steps must be >= 2, got: " + n2);
      } else {
         int steps = (int)n2;
         return t -> {
            float result = 0.0F;
            if (t < 0.0F) {
               return result;
            } else {
               float stepLength = 1.0F / steps;
               if (t > (result = (steps - 1) * stepLength)) {
                  return result;
               } else {
                  int leftBorderIndex = 0;
                  int rightBorderIndex = steps - 1;

                  while (rightBorderIndex - leftBorderIndex != 1) {
                     int testIndex = leftBorderIndex + (rightBorderIndex - leftBorderIndex) / 2;
                     if (t >= testIndex * stepLength) {
                        leftBorderIndex = testIndex;
                     } else {
                        rightBorderIndex = testIndex;
                     }
                  }

                  return leftBorderIndex * stepLength;
               }
            }
         };
      }
   }

   public static float catmullRom(float n) {
      return 0.5F
         * (
            2.0F * (n + 1.0F)
               + (n + 2.0F - n) * 1.0F
               + (2.0F * n - 5.0F * (n + 1.0F) + 4.0F * (n + 2.0F) - (n + 3.0F)) * 1.0F
               + (3.0F * (n + 1.0F) - n - 3.0F * (n + 2.0F) + (n + 3.0F)) * 1.0F
         );
   }
}
