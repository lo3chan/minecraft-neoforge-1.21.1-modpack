package io.wispforest.owo.ui.core;

public interface Easing {
   Easing LINEAR = x -> x;
   Easing SINE = x -> (float)(Math.sin(x * 3.141592653589793 - 1.5707963267948966) * 0.5 + 0.5);
   Easing QUADRATIC = x -> x < 0.5 ? 2.0F * x * x : (float)(1.0 - Math.pow(-2.0F * x + 2.0F, 2.0) / 2.0);
   Easing CUBIC = x -> x < 0.5 ? 4.0F * x * x * x : (float)(1.0 - Math.pow(-2.0F * x + 2.0F, 3.0) / 2.0);
   Easing QUARTIC = x -> x < 0.5 ? 8.0F * x * x * x * x : (float)(1.0 - Math.pow(-2.0F * x + 2.0F, 4.0) / 2.0);
   Easing EXPO = x -> {
      if (x == 0.0F) {
         return 0.0F;
      } else if (x == 1.0F) {
         return 1.0F;
      } else {
         return x < 0.5 ? (float)Math.pow(2.0, 20.0F * x - 10.0F) / 2.0F : (2.0F - (float)Math.pow(2.0, -20.0F * x + 10.0F)) / 2.0F;
      }
   };

   float apply(float var1);
}
