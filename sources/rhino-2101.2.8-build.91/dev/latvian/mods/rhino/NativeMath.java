package dev.latvian.mods.rhino;

final class NativeMath extends ScriptableObject {
   private static final double LOG2E = 1.4426950408889634;
   private static final Double Double32 = 32.0;

   static void init(Scriptable scope, boolean sealed, Context cx) {
      NativeMath obj = new NativeMath();
      obj.setPrototype(getObjectPrototype(scope, cx));
      obj.setParentScope(scope);
      obj.defineProperty(cx, scope, "toSource", 0, (lcx, lscope, thisObj, args) -> "Math", 2, 3);
      obj.defineProperty(cx, scope, "abs", 1, NativeMath::abs, 2, 3);
      obj.defineProperty(cx, scope, "acos", 1, NativeMath::acos, 2, 3);
      obj.defineProperty(cx, scope, "acosh", 1, NativeMath::acosh, 2, 3);
      obj.defineProperty(cx, scope, "asin", 1, NativeMath::asin, 2, 3);
      obj.defineProperty(cx, scope, "asinh", 1, NativeMath::asinh, 2, 3);
      obj.defineProperty(cx, scope, "atan", 1, NativeMath::atan, 2, 3);
      obj.defineProperty(cx, scope, "atanh", 1, NativeMath::atanh, 2, 3);
      obj.defineProperty(cx, scope, "atan2", 2, NativeMath::atan2, 2, 3);
      obj.defineProperty(cx, scope, "cbrt", 1, NativeMath::cbrt, 2, 3);
      obj.defineProperty(cx, scope, "ceil", 1, NativeMath::ceil, 2, 3);
      obj.defineProperty(cx, scope, "clz32", 1, NativeMath::clz32, 2, 3);
      obj.defineProperty(cx, scope, "cos", 1, NativeMath::cos, 2, 3);
      obj.defineProperty(cx, scope, "cosh", 1, NativeMath::cosh, 2, 3);
      obj.defineProperty(cx, scope, "exp", 1, NativeMath::exp, 2, 3);
      obj.defineProperty(cx, scope, "expm1", 1, NativeMath::expm1, 2, 3);
      obj.defineProperty(cx, scope, "f16round", 1, NativeMath::f16round, 2, 3);
      obj.defineProperty(cx, scope, "floor", 1, NativeMath::floor, 2, 3);
      obj.defineProperty(cx, scope, "fround", 1, NativeMath::fround, 2, 3);
      obj.defineProperty(cx, scope, "hypot", 2, NativeMath::hypot, 2, 3);
      obj.defineProperty(cx, scope, "imul", 2, NativeMath::imul, 2, 3);
      obj.defineProperty(cx, scope, "log", 1, NativeMath::log, 2, 3);
      obj.defineProperty(cx, scope, "log1p", 1, NativeMath::log1p, 2, 3);
      obj.defineProperty(cx, scope, "log10", 1, NativeMath::log10, 2, 3);
      obj.defineProperty(cx, scope, "log2", 1, NativeMath::log2, 2, 3);
      obj.defineProperty(cx, scope, "max", 2, NativeMath::max, 2, 3);
      obj.defineProperty(cx, scope, "min", 2, NativeMath::min, 2, 3);
      obj.defineProperty(cx, scope, "pow", 2, NativeMath::pow, 2, 3);
      obj.defineProperty(cx, scope, "random", 0, NativeMath::random, 2, 3);
      obj.defineProperty(cx, scope, "round", 1, NativeMath::round, 2, 3);
      obj.defineProperty(cx, scope, "sign", 1, NativeMath::sign, 2, 3);
      obj.defineProperty(cx, scope, "sin", 1, NativeMath::sin, 2, 3);
      obj.defineProperty(cx, scope, "sinh", 1, NativeMath::sinh, 2, 3);
      obj.defineProperty(cx, scope, "sqrt", 1, NativeMath::sqrt, 2, 3);
      obj.defineProperty(cx, scope, "tan", 1, NativeMath::tan, 2, 3);
      obj.defineProperty(cx, scope, "tanh", 1, NativeMath::tanh, 2, 3);
      obj.defineProperty(cx, scope, "trunc", 1, NativeMath::trunc, 2, 3);
      obj.defineProperty(cx, "E", ScriptRuntime.wrapNumber(2.718281828459045), 7);
      obj.defineProperty(cx, "PI", ScriptRuntime.wrapNumber(3.141592653589793), 7);
      obj.defineProperty(cx, "LN10", ScriptRuntime.wrapNumber(2.302585092994046), 7);
      obj.defineProperty(cx, "LN2", ScriptRuntime.wrapNumber(0.6931471805599453), 7);
      obj.defineProperty(cx, "LOG2E", ScriptRuntime.wrapNumber(1.4426950408889634), 7);
      obj.defineProperty(cx, "LOG10E", ScriptRuntime.wrapNumber(0.4342944819032518), 7);
      obj.defineProperty(cx, "SQRT1_2", ScriptRuntime.wrapNumber(0.7071067811865476), 7);
      obj.defineProperty(cx, "SQRT2", ScriptRuntime.wrapNumber(1.4142135623730951), 7);
      obj.defineProperty(cx, SymbolKey.TO_STRING_TAG, "Math", 3);
      if (sealed) {
         obj.sealObject(cx);
      }

      defineProperty(scope, "Math", obj, 2, cx);
   }

   private NativeMath() {
   }

   @Override
   public String getClassName() {
      return "Math";
   }

   private static Object abs(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      x = x == 0.0 ? 0.0 : (x < 0.0 ? -x : x);
      return ScriptRuntime.wrapNumber(x);
   }

   private static Object acos(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      x = !Double.isNaN(x) && -1.0 <= x && x <= 1.0 ? Math.acos(x) : 0.0 / 0.0;
      return ScriptRuntime.wrapNumber(x);
   }

   private static Object asin(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      x = !Double.isNaN(x) && -1.0 <= x && x <= 1.0 ? Math.asin(x) : 0.0 / 0.0;
      return ScriptRuntime.wrapNumber(x);
   }

   private static Object acosh(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      return !Double.isNaN(x) ? Math.log(x + Math.sqrt(x * x - 1.0)) : ScriptRuntime.NaNobj;
   }

   private static Object asinh(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      if (Double.isInfinite(x)) {
         return x;
      } else if (!Double.isNaN(x)) {
         if (x == 0.0) {
            return 1.0 / x > 0.0 ? ScriptRuntime.zeroObj : ScriptRuntime.negativeZeroObj;
         } else {
            return Math.log(x + Math.sqrt(x * x + 1.0));
         }
      } else {
         return ScriptRuntime.NaNobj;
      }
   }

   private static Object atan(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.atan(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object atanh(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      if (Double.isNaN(x) || !(-1.0 <= x) || !(x <= 1.0)) {
         return ScriptRuntime.NaNobj;
      } else if (x == 0.0) {
         return 1.0 / x > 0.0 ? ScriptRuntime.zeroObj : ScriptRuntime.negativeZeroObj;
      } else {
         return 0.5 * Math.log((1.0 + x) / (1.0 - x));
      }
   }

   private static Object atan2(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      return ScriptRuntime.wrapNumber(Math.atan2(x, ScriptRuntime.toNumber(cx, args, 1)));
   }

   private static Object cbrt(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.cbrt(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object ceil(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.ceil(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object clz32(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      if (x != 0.0 && !Double.isNaN(x) && !Double.isInfinite(x)) {
         long n = ScriptRuntime.toUint32(x);
         return n == 0L ? Double32 : (double)Integer.numberOfLeadingZeros((int)n);
      } else {
         return Double32;
      }
   }

   private static Object cos(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      return ScriptRuntime.wrapNumber(Double.isInfinite(x) ? 0.0 / 0.0 : Math.cos(x));
   }

   private static Object cosh(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.cosh(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object exp(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      x = x == 1.0 / 0.0 ? x : (x == -1.0 / 0.0 ? 0.0 : Math.exp(x));
      return ScriptRuntime.wrapNumber(x);
   }

   private static Object expm1(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.expm1(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object f16round(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(js_f16round(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object floor(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.floor(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object fround(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber((float)ScriptRuntime.toNumber(cx, args, 0));
   }

   private static Object hypot(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(js_hypot(args, cx));
   }

   private static Object imul(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(js_imul(args, cx));
   }

   private static Object log(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      return ScriptRuntime.wrapNumber(x < 0.0 ? 0.0 / 0.0 : Math.log(x));
   }

   private static Object log1p(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.log1p(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object log10(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.log10(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object log2(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      return ScriptRuntime.wrapNumber(x < 0.0 ? 0.0 / 0.0 : Math.log(x) * 1.4426950408889634);
   }

   private static Object max(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = -1.0 / 0.0;

      for (Object arg : args) {
         x = Math.max(x, ScriptRuntime.toNumber(cx, arg));
      }

      return ScriptRuntime.wrapNumber(x);
   }

   private static Object min(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = 1.0 / 0.0;

      for (Object arg : args) {
         x = Math.min(x, ScriptRuntime.toNumber(cx, arg));
      }

      return ScriptRuntime.wrapNumber(x);
   }

   private static Object pow(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      return ScriptRuntime.wrapNumber(js_pow(x, ScriptRuntime.toNumber(cx, args, 1)));
   }

   private static Object random(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.random());
   }

   private static Object round(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      if (!Double.isNaN(x) && !Double.isInfinite(x)) {
         long l = Math.round(x);
         if (l != 0L) {
            x = l;
         } else if (x < 0.0) {
            x = ScriptRuntime.negativeZero;
         } else if (x != 0.0) {
            x = 0.0;
         }
      }

      return ScriptRuntime.wrapNumber(x);
   }

   private static Object sign(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      if (!Double.isNaN(x)) {
         if (x == 0.0) {
            return 1.0 / x > 0.0 ? ScriptRuntime.zeroObj : ScriptRuntime.negativeZeroObj;
         } else {
            return Math.signum(x);
         }
      } else {
         return ScriptRuntime.NaNobj;
      }
   }

   private static Object sin(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double x = ScriptRuntime.toNumber(cx, args, 0);
      return ScriptRuntime.wrapNumber(Double.isInfinite(x) ? 0.0 / 0.0 : Math.sin(x));
   }

   private static Object sinh(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.sinh(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object sqrt(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.sqrt(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object tan(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.tan(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object tanh(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(Math.tanh(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static Object trunc(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.wrapNumber(js_trunc(ScriptRuntime.toNumber(cx, args, 0)));
   }

   private static double js_pow(double x, double y) {
      double result;
      if (Double.isNaN(y)) {
         result = y;
      } else if (y == 0.0) {
         result = 1.0;
      } else if (x == 0.0) {
         if (1.0 / x > 0.0) {
            result = y > 0.0 ? 0.0 : 1.0 / 0.0;
         } else {
            long y_long = (long)y;
            if (y_long == y && (y_long & 1L) != 0L) {
               result = y > 0.0 ? -0.0 : -1.0 / 0.0;
            } else {
               result = y > 0.0 ? 0.0 : 1.0 / 0.0;
            }
         }
      } else {
         result = Math.pow(x, y);
         if (Double.isNaN(result)) {
            if (y == 1.0 / 0.0) {
               if (x < -1.0 || 1.0 < x) {
                  result = 1.0 / 0.0;
               } else if (-1.0 < x && x < 1.0) {
                  result = 0.0;
               }
            } else if (y == -1.0 / 0.0) {
               if (x < -1.0 || 1.0 < x) {
                  result = 0.0;
               } else if (-1.0 < x && x < 1.0) {
                  result = 1.0 / 0.0;
               }
            } else if (x == 1.0 / 0.0) {
               result = y > 0.0 ? 1.0 / 0.0 : 0.0;
            } else if (x == -1.0 / 0.0) {
               long y_long = (long)y;
               if (y_long == y && (y_long & 1L) != 0L) {
                  result = y > 0.0 ? -1.0 / 0.0 : -0.0;
               } else {
                  result = y > 0.0 ? 1.0 / 0.0 : 0.0;
               }
            }
         }
      }

      return result;
   }

   private static double js_hypot(Object[] args, Context cx) {
      if (args == null) {
         return 0.0;
      } else {
         double y = 0.0;
         boolean hasNaN = false;
         boolean hasInfinity = false;

         for (Object o : args) {
            double d = ScriptRuntime.toNumber(cx, o);
            if (Double.isNaN(d)) {
               hasNaN = true;
            } else if (Double.isInfinite(d)) {
               hasInfinity = true;
            } else {
               y += d * d;
            }
         }

         if (hasInfinity) {
            return 1.0 / 0.0;
         } else {
            return hasNaN ? 0.0 / 0.0 : Math.sqrt(y);
         }
      }
   }

   private static double js_f16round(double x) {
      if (Double.isNaN(x)) {
         return 0.0 / 0.0;
      } else if (x == 0.0) {
         return x;
      } else if (Double.isInfinite(x)) {
         return x;
      } else {
         long bits = Double.doubleToLongBits(x);
         int sign = (int)(bits >>> 63);
         int exponent = (int)(bits >>> 52 & 2047L);
         long mantissa = bits & 4503599627370495L;
         exponent = exponent - 1023 + 15;
         if (exponent >= 31) {
            return sign != 0 ? -1.0 / 0.0 : 1.0 / 0.0;
         } else {
            return exponent < 0 ? handleSubnormalF16(sign, exponent, mantissa) : handleNormalF16(sign, exponent, mantissa);
         }
      }
   }

   private static double handleSubnormalF16(int sign, int exponent, long mantissa) {
      if (exponent < -10) {
         return sign != 0 ? -0.0 : 0.0;
      } else if (exponent == -10 && mantissa == 0L) {
         return sign != 0 ? -0.0 : 0.0;
      } else if (exponent == -10 && mantissa > 0L) {
         double smallestSubnormal = 5.960464477539063E-8;
         return sign != 0 ? -smallestSubnormal : smallestSubnormal;
      } else {
         int totalShift = 42 + (1 - exponent);
         mantissa |= 4503599627370496L;
         long roundBit = mantissa >> totalShift - 1 & 1L;
         long stickyBits = mantissa & (1L << totalShift - 1) - 1L;
         mantissa >>>= totalShift;
         if (roundBit == 1L && (stickyBits != 0L || (mantissa & 1L) == 1L)) {
            mantissa++;
         }

         if (mantissa == 0L) {
            return sign != 0 ? -0.0 : 0.0;
         } else if (mantissa >= 1024L) {
            return sign != 0 ? -6.103515625E-5 : 6.103515625E-5;
         } else {
            double value = Math.scalb(mantissa / 1024.0, -14);
            return sign != 0 ? -value : value;
         }
      }
   }

   private static double handleNormalF16(int sign, int exponent, long mantissa) {
      long fullMantissa = mantissa | 4503599627370496L;
      long roundBit = fullMantissa >> 41 & 1L;
      long stickyBits = fullMantissa & 2199023255551L;
      fullMantissa >>>= 42;
      if (exponent == 0) {
         if (fullMantissa == 2046L) {
            return reconstructSubnormalF16(sign, 1023L);
         }

         if (fullMantissa == 2047L && roundBit == 0L && stickyBits == 0L) {
            return reconstructNormalF16(sign, 1, 0L);
         }
      }

      mantissa = fullMantissa & 1023L;
      if (roundBit == 1L && (stickyBits != 0L || (mantissa & 1L) == 1L)) {
         mantissa++;
      }

      if (mantissa >= 1024L) {
         mantissa = 0L;
         if (++exponent >= 31) {
            return sign != 0 ? -1.0 / 0.0 : 1.0 / 0.0;
         }
      }

      return exponent == 0 ? reconstructSubnormalF16(sign, mantissa) : reconstructNormalF16(sign, exponent, mantissa);
   }

   private static double reconstructSubnormalF16(int sign, long mantissa) {
      if (mantissa == 0L) {
         return sign != 0 ? -0.0 : 0.0;
      } else {
         double value = Math.scalb(mantissa / 1024.0, -14);
         return sign != 0 ? -value : value;
      }
   }

   private static double reconstructNormalF16(int sign, int exponent, long mantissa) {
      long resultBits = (long)sign << 63 | (long)(exponent + 1023 - 15) << 52 | mantissa << 42;
      return Double.longBitsToDouble(resultBits);
   }

   private static double js_trunc(double d) {
      return d < 0.0 ? Math.ceil(d) : Math.floor(d);
   }

   private static int js_imul(Object[] args, Context cx) {
      if (args == null) {
         return 0;
      } else {
         int x = ScriptRuntime.toInt32(cx, args, 0);
         int y = ScriptRuntime.toInt32(cx, args, 1);
         return x * y;
      }
   }
}
