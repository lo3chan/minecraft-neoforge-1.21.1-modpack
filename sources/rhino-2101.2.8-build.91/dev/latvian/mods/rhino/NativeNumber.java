package dev.latvian.mods.rhino;

final class NativeNumber extends ScriptableObject {
   public static final double MAX_SAFE_INTEGER = 9.007199254740991E15;
   private static final String CLASS_NAME = "Number";
   private static final int MAX_PRECISION = 100;
   private static final double MIN_SAFE_INTEGER = -9.007199254740991E15;
   private static final double EPSILON = 2.220446049250313E-16;
   private final Context localContext;
   private final double doubleValue;

   static void init(Scriptable scope, boolean sealed, Context cx) {
      LambdaConstructor constructor = new LambdaConstructor(cx, scope, "Number", 1, NativeNumber::js_constructorFunc, NativeNumber::js_constructor);
      constructor.setPrototypePropertyAttributes(7);
      constructor.setPrototypeScriptable(new NativeNumber(cx, 0.0), cx);
      int propAttr = 7;
      constructor.defineProperty(cx, "NaN", ScriptRuntime.NaNobj, 7);
      constructor.defineProperty(cx, "POSITIVE_INFINITY", ScriptRuntime.wrapNumber(1.0 / 0.0), 7);
      constructor.defineProperty(cx, "NEGATIVE_INFINITY", ScriptRuntime.wrapNumber(-1.0 / 0.0), 7);
      constructor.defineProperty(cx, "MAX_VALUE", ScriptRuntime.wrapNumber(1.7976931348623157E308), 7);
      constructor.defineProperty(cx, "MIN_VALUE", ScriptRuntime.wrapNumber(5.0E-324), 7);
      constructor.defineProperty(cx, "MAX_SAFE_INTEGER", ScriptRuntime.wrapNumber(9.007199254740991E15), 7);
      constructor.defineProperty(cx, "MIN_SAFE_INTEGER", ScriptRuntime.wrapNumber(-9.007199254740991E15), 7);
      constructor.defineProperty(cx, "EPSILON", ScriptRuntime.wrapNumber(2.220446049250313E-16), 7);
      constructor.defineConstructorMethod(cx, scope, "isFinite", 1, NativeNumber::js_isFinite, 2, 3);
      constructor.defineConstructorMethod(cx, scope, "isNaN", 1, NativeNumber::js_isNaN, 2, 3);
      constructor.defineConstructorMethod(cx, scope, "isInteger", 1, NativeNumber::js_isInteger, 2, 3);
      constructor.defineConstructorMethod(cx, scope, "isSafeInteger", 1, NativeNumber::js_isSafeInteger, 2, 3);
      Object parseFloat = ScriptRuntime.getTopLevelProp(cx, constructor, "parseFloat");
      if (parseFloat instanceof Function) {
         constructor.defineProperty(cx, "parseFloat", parseFloat, 2);
      }

      Object parseInt = ScriptRuntime.getTopLevelProp(cx, constructor, "parseInt");
      if (parseInt instanceof Function) {
         constructor.defineProperty(cx, "parseInt", parseInt, 2);
      }

      constructor.definePrototypeMethod(cx, scope, "toString", 1, NativeNumber::js_toString, 2, 3);
      constructor.definePrototypeMethod(cx, scope, "toLocaleString", 0, NativeNumber::js_toString, 2, 3);
      constructor.definePrototypeMethod(cx, scope, "toSource", 0, NativeNumber::js_toSource, 2, 3);
      constructor.definePrototypeMethod(cx, scope, "valueOf", 0, (lcx, lscope, thisObj, args) -> toSelf(lcx, thisObj).doubleValue, 2, 3);
      constructor.definePrototypeMethod(cx, scope, "toFixed", 1, NativeNumber::js_toFixed, 2, 3);
      constructor.definePrototypeMethod(cx, scope, "toExponential", 1, NativeNumber::js_toExponential, 2, 3);
      constructor.definePrototypeMethod(cx, scope, "toPrecision", 1, NativeNumber::js_toPrecision, 2, 3);
      ScriptableObject.defineProperty(scope, "Number", constructor, 2, cx);
      if (sealed) {
         constructor.sealObject(cx);
      }
   }

   NativeNumber(Context cx, double number) {
      this.localContext = cx;
      this.doubleValue = number;
   }

   @Override
   public String getClassName() {
      return "Number";
   }

   @Override
   public MemberType getTypeOf() {
      return MemberType.NUMBER;
   }

   private static Scriptable js_constructor(Context cx, Scriptable scope, Object[] args) {
      double val = args.length > 0 ? ScriptRuntime.toNumber(cx, args[0]) : 0.0;
      return new NativeNumber(cx, val);
   }

   private static Object js_constructorFunc(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return args.length > 0 ? ScriptRuntime.toNumber(cx, args[0]) : 0.0;
   }

   private static NativeNumber toSelf(Context cx, Scriptable thisObj) {
      return LambdaConstructor.convertThisObject(cx, thisObj, NativeNumber.class);
   }

   private static Object js_toString(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      int base = args.length != 0 && !Undefined.isUndefined(args[0]) ? ScriptRuntime.toInt32(cx, args[0]) : 10;
      return ScriptRuntime.numberToString(cx, toSelf(cx, thisObj).doubleValue, base);
   }

   private static Object js_toSource(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return "(new Number(" + ScriptRuntime.toString(cx, toSelf(cx, thisObj).doubleValue) + "))";
   }

   private static Object js_toFixed(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double value = toSelf(cx, thisObj).doubleValue;
      return num_to(cx, value, args, 2, 2, 0, 0);
   }

   private static Object js_toExponential(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double value = toSelf(cx, thisObj).doubleValue;
      if (Double.isNaN(value)) {
         return "NaN";
      } else if (Double.isInfinite(value)) {
         return value >= 0.0 ? "Infinity" : "-Infinity";
      } else {
         return num_to(cx, value, args, 1, 3, 0, 1);
      }
   }

   private static Object js_toPrecision(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      double value = toSelf(cx, thisObj).doubleValue;
      if (args.length == 0 || Undefined.isUndefined(args[0])) {
         return ScriptRuntime.numberToString(cx, value, 10);
      } else if (Double.isNaN(value)) {
         return "NaN";
      } else if (!Double.isInfinite(value)) {
         return num_to(cx, value, args, 0, 4, 1, 0);
      } else {
         return value >= 0.0 ? "Infinity" : "-Infinity";
      }
   }

   private static Number argToNumber(Object[] args) {
      return args.length > 0 && args[0] instanceof Number ? (Number)args[0] : null;
   }

   @Override
   public String toString() {
      return ScriptRuntime.numberToString(this.localContext, this.doubleValue, 10);
   }

   private static String num_to(Context cx, double val, Object[] args, int zeroArgMode, int oneArgMode, int precisionMin, int precisionOffset) {
      int precision;
      if (args.length == 0) {
         precision = 0;
         oneArgMode = zeroArgMode;
      } else {
         double p = ScriptRuntime.toInteger(cx, args[0]);
         if (p < precisionMin || p > 100.0) {
            String msg = ScriptRuntime.getMessage1("msg.bad.precision", ScriptRuntime.toString(cx, args[0]));
            throw ScriptRuntime.rangeError(cx, msg);
         }

         precision = ScriptRuntime.toInt32(p);
      }

      StringBuilder sb = new StringBuilder();
      DToA.JS_dtostr(sb, oneArgMode, precision + precisionOffset, val);
      return sb.toString();
   }

   private static Object js_isFinite(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Number n = argToNumber(args);
      return n == null ? Boolean.FALSE : isFinite(n, cx);
   }

   static Object isFinite(Object val, Context cx) {
      double nd = ScriptRuntime.toNumber(cx, val);
      return !Double.isInfinite(nd) && !Double.isNaN(nd);
   }

   private static Object js_isNaN(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Number val = argToNumber(args);
      if (val == null) {
         return false;
      } else if (val instanceof Double) {
         return ((Double)val).isNaN();
      } else {
         double d = val.doubleValue();
         return Double.isNaN(d);
      }
   }

   private static Object js_isInteger(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Number val = argToNumber(args);
      if (val == null) {
         return false;
      } else {
         return val instanceof Double ? isDoubleInteger((Double)val) : isDoubleInteger(val.doubleValue());
      }
   }

   private static Object js_isSafeInteger(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Number val = argToNumber(args);
      if (val == null) {
         return false;
      } else {
         return val instanceof Double ? isDoubleSafeInteger((Double)val) : isDoubleSafeInteger(val.doubleValue());
      }
   }

   private static boolean isDoubleInteger(Double d) {
      return !d.isInfinite() && !d.isNaN() && Math.floor(d) == d;
   }

   private static boolean isDoubleInteger(double d) {
      return !Double.isInfinite(d) && !Double.isNaN(d) && Math.floor(d) == d;
   }

   private static boolean isDoubleSafeInteger(Double d) {
      return isDoubleInteger(d) && d <= 9.007199254740991E15 && d >= -9.007199254740991E15;
   }

   private static boolean isDoubleSafeInteger(double d) {
      return isDoubleInteger(d) && d <= 9.007199254740991E15 && d >= -9.007199254740991E15;
   }
}
