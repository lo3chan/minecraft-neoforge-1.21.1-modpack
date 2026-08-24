package vazkii.psi.api.internal.math;

public class Gamma {
   public static double gamma(double x) {
      if (x <= 0.0) {
         String msg = String.format("Invalid input argument %f. Argument must be positive.", x);
         throw new IllegalArgumentException(msg);
      } else {
         double gamma = 0.5772156649015329;
         if (x < 0.001) {
            return 1.0 / (x * (1.0 + 0.5772156649015329 * x));
         } else if (!(x < 12.0)) {
            return x > 171.624 ? 1.0 / 0.0 : Math.exp(logGamma(x));
         } else {
            int n = 0;
            boolean arg_was_less_than_one = x < 1.0;
            double y;
            if (arg_was_less_than_one) {
               y = x + 1.0;
            } else {
               n = (int)Math.floor(x) - 1;
               y = x - n;
            }

            double[] p = new double[]{
               -1.716185138865495,
               24.76565080557592,
               -379.80425647094563,
               629.3311553128184,
               866.9662027904133,
               -31451.272968848367,
               -36144.413418691176,
               66456.14382024054
            };
            double[] q = new double[]{
               -30.840230011973897,
               315.35062697960416,
               -1015.1563674902192,
               -3107.771671572311,
               22538.11842098015,
               4755.846277527881,
               -134659.9598649693,
               -115132.25967555349
            };
            double num = 0.0;
            double den = 1.0;
            double z = y - 1.0;

            for (int i = 0; i < 8; i++) {
               num = (num + p[i]) * z;
               den = den * z + q[i];
            }

            double result = num / den + 1.0;
            if (arg_was_less_than_one) {
               result /= y - 1.0;
            } else {
               for (int var20 = 0; var20 < n; var20++) {
                  result *= y++;
               }
            }

            return result;
         }
      }
   }

   public static double logGamma(double x) {
      if (x <= 0.0) {
         String msg = String.format("Invalid input argument %f. Argument must be positive.", x);
         throw new IllegalArgumentException(msg);
      } else if (x < 12.0) {
         return Math.log(Math.abs(gamma(x)));
      } else {
         double[] c = new double[]{
            0.08333333333333333,
            -0.002777777777777778,
            7.936507936507937E-4,
            -5.952380952380953E-4,
            8.417508417508417E-4,
            -0.0019175269175269176,
            0.00641025641025641,
            -0.029550653594771242
         };
         double z = 1.0 / (x * x);
         double sum = c[7];

         for (int i = 6; i >= 0; i--) {
            sum *= z;
            sum += c[i];
         }

         double series = sum / x;
         double halfLogTwoPi = 0.9189385332046728;
         return (x - 0.5) * Math.log(x) - x + halfLogTwoPi + series;
      }
   }
}
