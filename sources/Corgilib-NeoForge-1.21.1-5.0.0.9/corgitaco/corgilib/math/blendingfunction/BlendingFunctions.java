package corgitaco.corgilib.math.blendingfunction;

public class BlendingFunctions {
   public static double easeInOutCirc(double x) {
      return x < 0.5 ? (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * x, 2.0))) / 2.0 : (Math.sqrt(1.0 - Math.pow(-2.0 * x + 2.0, 2.0)) + 1.0) / 2.0;
   }

   public static double easeOutCubic(double x) {
      return 1.0 - Math.pow(1.0 - x, 3.0);
   }

   public static double easeOutBounce(double x) {
      double n1 = 7.5625;
      double d1 = 2.75;
      if (x < 1.0 / d1) {
         return n1 * x * x;
      } else if (x < 2.0 / d1) {
         double var8;
         return n1 * (var8 = x - 1.5 / d1) * var8 + 0.75;
      } else {
         double var6;
         double var7;
         return x < 2.5 / d1 ? n1 * (var6 = x - 2.25 / d1) * var6 + 0.9375 : n1 * (var7 = x - 2.625 / d1) * var7 + 0.984375;
      }
   }

   public static double easeOutElastic(double x, double intensity) {
      double c4 = 2.0943951023931953;
      return x == 0.0 ? 0.0 : (x == 1.0 ? 1.0 : Math.pow(2.0, -intensity * x) * Math.sin((x * 10.0 - 0.75) * c4) + 1.0);
   }

   public static double easeInCirc(double x, double exponent) {
      return 1.0 - Math.sqrt(1.0 - Math.pow(x, exponent));
   }

   public static double easeOutQuint(double x) {
      return 1.0 - Math.pow(1.0 - x, 5.0);
   }
}
