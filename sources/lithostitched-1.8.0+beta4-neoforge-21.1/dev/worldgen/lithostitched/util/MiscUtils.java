package dev.worldgen.lithostitched.util;

import java.util.function.Consumer;

public class MiscUtils {
   private static final float[] SIN = make(new float[65536], values -> {
      for (int $$1 = 0; $$1 < values.length; $$1++) {
         values[$$1] = (float)Math.sin($$1 * 3.141592653589793 * 2.0 / 65536.0);
      }
   });

   private static <T> T make(T t, Consumer<? super T> consumer) {
      consumer.accept(t);
      return t;
   }

   public static float sin(float value) {
      return SIN[(int)(value * 10430.378F) & 65535];
   }

   public static float cos(float value) {
      return SIN[(int)(value * 10430.378F + 16384.0F) & 65535];
   }
}
