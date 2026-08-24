package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;

public class CustomFunction extends BaseFunction {
   private final String functionName;
   private final CustomFunction.Func func;
   private final TypeInfo[] argTypes;

   public CustomFunction(String functionName, CustomFunction.Func func, TypeInfo[] argTypes) {
      this.functionName = functionName;
      this.func = func;
      this.argTypes = argTypes.length == 0 ? TypeInfo.EMPTY_ARRAY : argTypes;
   }

   @Override
   public String getFunctionName() {
      return this.functionName;
   }

   @Override
   public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Object[] origArgs = args;

      for (int i = 0; i < args.length; i++) {
         Object arg = args[i];
         Object coerced = cx.jsToJava(arg, this.argTypes[i]);
         if (coerced != arg) {
            if (origArgs == args) {
               args = (Object[])args.clone();
            }

            args[i] = coerced;
         }
      }

      return this.func.call(cx, args);
   }

   @FunctionalInterface
   public interface Func {
      Object call(Context var1, Object[] var2);
   }

   @FunctionalInterface
   public interface NoArgFunc extends CustomFunction.Func {
      Object call(Context var1);

      @Override
      default Object call(Context cx, Object[] args) {
         return this.call(cx);
      }
   }
}
