package dev.latvian.mods.rhino;

import java.util.WeakHashMap;

public class NativeWeakMap extends ScriptableObject {
   private static final String CLASS_NAME = "WeakMap";
   private static final Object NULL_VALUE = new Object();
   private final transient WeakHashMap<Scriptable, Object> map = new WeakHashMap<>();
   private boolean instanceOfWeakMap = false;

   static void init(Context cx, Scriptable scope, boolean sealed) {
      LambdaConstructor constructor = new LambdaConstructor(cx, scope, "WeakMap", 0, 2, NativeWeakMap::jsConstructor);
      constructor.setPrototypePropertyAttributes(7);
      constructor.definePrototypeMethod(
         cx,
         scope,
         "set",
         2,
         (lcx, lscope, thisObj, args) -> realThis(thisObj, "set", lcx).js_set(lcx, NativeMap.key(args), args.length > 1 ? args[1] : Undefined.INSTANCE),
         2,
         3
      );
      constructor.definePrototypeMethod(
         cx, scope, "delete", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "delete", lcx).js_delete(NativeMap.key(args)), 2, 3
      );
      constructor.definePrototypeMethod(cx, scope, "get", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "get", lcx).js_get(NativeMap.key(args)), 2, 3);
      constructor.definePrototypeMethod(cx, scope, "has", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "has", lcx).js_has(NativeMap.key(args)), 2, 3);
      constructor.definePrototypeProperty(cx, SymbolKey.TO_STRING_TAG, "WeakMap", 3);
      ScriptRuntimeES6.addSymbolSpecies(cx, scope, constructor);
      ScriptableObject.defineProperty(scope, "WeakMap", constructor, 2, cx);
      if (sealed) {
         constructor.sealObject(cx);
      }
   }

   @Override
   public String getClassName() {
      return "WeakMap";
   }

   private static Scriptable jsConstructor(Context cx, Scriptable scope, Object[] args) {
      NativeWeakMap nm = new NativeWeakMap();
      nm.instanceOfWeakMap = true;
      if (args.length > 0) {
         NativeMap.loadFromIterable(cx, scope, nm, NativeMap.key(args));
      }

      return nm;
   }

   private Object js_delete(Object key) {
      return !isValidKey(key) ? Boolean.FALSE : this.map.remove(key) != null;
   }

   private Object js_get(Object key) {
      if (!isValidKey(key)) {
         return Undefined.INSTANCE;
      } else {
         Object result = this.map.get(key);
         if (result == null) {
            return Undefined.INSTANCE;
         } else {
            return result == NULL_VALUE ? null : result;
         }
      }
   }

   private Object js_has(Object key) {
      return !isValidKey(key) ? Boolean.FALSE : this.map.containsKey(key);
   }

   private Object js_set(Context cx, Object key, Object v) {
      if (!isValidKey(key)) {
         throw ScriptRuntime.typeError1(cx, "msg.arg.not.object", ScriptRuntime.typeof(cx, key));
      } else {
         Object value = v == null ? NULL_VALUE : v;
         this.map.put((Scriptable)key, value);
         return this;
      }
   }

   private static boolean isValidKey(Object key) {
      return ScriptRuntime.isUnregisteredSymbol(key) || ScriptRuntime.isObject(key);
   }

   private static NativeWeakMap realThis(Scriptable thisObj, String name, Context cx) {
      NativeWeakMap nm = LambdaConstructor.convertThisObject(cx, thisObj, NativeWeakMap.class);
      if (!nm.instanceOfWeakMap) {
         throw ScriptRuntime.typeError1(cx, "msg.incompat.call", name);
      } else {
         return nm;
      }
   }
}
