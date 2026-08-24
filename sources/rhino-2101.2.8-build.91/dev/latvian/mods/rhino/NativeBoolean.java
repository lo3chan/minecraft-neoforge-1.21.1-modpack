package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.util.DefaultValueTypeHint;

final class NativeBoolean extends ScriptableObject {
   private static final String CLASS_NAME = "Boolean";
   private final boolean booleanValue;

   static void init(Scriptable scope, boolean sealed, Context cx) {
      LambdaConstructor constructor = new LambdaConstructor(cx, scope, "Boolean", 1, NativeBoolean::js_constructorFunc, NativeBoolean::js_constructor);
      constructor.setPrototypePropertyAttributes(7);
      constructor.setPrototypeScriptable(new NativeBoolean(false), cx);
      constructor.definePrototypeMethod(cx, scope, "toString", 0, NativeBoolean::js_toString, 2, 3);
      constructor.definePrototypeMethod(cx, scope, "toSource", 0, NativeBoolean::js_toSource, 2, 3);
      constructor.definePrototypeMethod(cx, scope, "valueOf", 0, NativeBoolean::js_valueOf, 2, 3);
      ScriptableObject.defineProperty(scope, "Boolean", constructor, 2, cx);
      if (sealed) {
         constructor.sealObject(cx);
      }
   }

   NativeBoolean(boolean b) {
      this.booleanValue = b;
   }

   @Override
   public String getClassName() {
      return "Boolean";
   }

   @Override
   public Object getDefaultValue(Context cx, DefaultValueTypeHint typeHint) {
      return typeHint == DefaultValueTypeHint.BOOLEAN ? this.booleanValue : super.getDefaultValue(cx, typeHint);
   }

   private static boolean toValue(Context cx, Scriptable thisObj) {
      return LambdaConstructor.convertThisObject(cx, thisObj, NativeBoolean.class).booleanValue;
   }

   private static Object js_constructorFunc(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ScriptRuntime.toBoolean(cx, args.length > 0 ? args[0] : Undefined.INSTANCE);
   }

   private static NativeBoolean js_constructor(Context cx, Scriptable scope, Object[] args) {
      boolean b = ScriptRuntime.toBoolean(cx, args.length > 0 ? args[0] : Undefined.INSTANCE);
      return new NativeBoolean(b);
   }

   private static String js_toString(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return toValue(cx, thisObj) ? "true" : "false";
   }

   private static Object js_valueOf(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return toValue(cx, thisObj);
   }

   private static Object js_toSource(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return "(new Boolean(" + ScriptRuntime.toString(cx, toValue(cx, thisObj)) + "))";
   }
}
