package io.wispforest.owo.ui.util;

public final class Delta {
   private Delta() {
   }

   public static float compute(float current, float target, float delta) {
      float diff = target - current;
      delta = diff * delta;
      return Math.abs(delta) > Math.abs(diff) ? diff : delta;
   }

   public static double compute(double current, double target, double delta) {
      double diff = target - current;
      delta = diff * delta;
      return Math.abs(delta) > Math.abs(diff) ? diff : delta;
   }
}
