package net.diebuddies.math;

public class EaseOutElastic implements Curve {
   @Override
   public float get(float time) {
      float c4 = 2.0943952F;
      return (float)java.lang.Math.pow(2.0, -10.0F * time) * (float)java.lang.Math.sin((time * 10.0F - 0.75F) * c4) + 1.0F;
   }
}
