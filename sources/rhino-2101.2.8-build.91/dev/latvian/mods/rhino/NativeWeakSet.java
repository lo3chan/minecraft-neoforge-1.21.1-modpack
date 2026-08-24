package dev.latvian.mods.rhino;

import java.util.WeakHashMap;

public class NativeWeakSet extends ScriptableObject {
   private static final String CLASS_NAME = "WeakSet";
   private final transient WeakHashMap<Scriptable, Boolean> map = new WeakHashMap<>();
   private boolean instanceOfWeakSet = false;

   static void init(Context cx, Scriptable scope, boolean sealed) {
      LambdaConstructor constructor = new LambdaConstructor(cx, scope, "WeakSet", 0, 2, NativeWeakSet::jsConstructor);
      constructor.setPrototypePropertyAttributes(7);
      constructor.definePrototypeMethod(
         cx, scope, "add", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "add", lcx).js_add(lcx, NativeMap.key(args)), 2, 3
      );
      constructor.definePrototypeMethod(
         cx, scope, "delete", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "delete", lcx).js_delete(NativeMap.key(args)), 2, 3
      );
      constructor.definePrototypeMethod(cx, scope, "has", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "has", lcx).js_has(NativeMap.key(args)), 2, 3);
      constructor.definePrototypeProperty(cx, SymbolKey.TO_STRING_TAG, "WeakSet", 3);
      ScriptRuntimeES6.addSymbolSpecies(cx, scope, constructor);
      ScriptableObject.defineProperty(scope, "WeakSet", constructor, 2, cx);
      if (sealed) {
         constructor.sealObject(cx);
      }
   }

   @Override
   public String getClassName() {
      return "WeakSet";
   }

   private static Scriptable jsConstructor(Context cx, Scriptable scope, Object[] args) {
      NativeWeakSet ns = new NativeWeakSet();
      ns.instanceOfWeakSet = true;
      if (args.length > 0) {
         NativeSet.loadFromIterable(cx, scope, ns, NativeMap.key(args));
      }

      return ns;
   }

   private Object js_add(Context cx, Object key) {
      if (!isValidValue(key)) {
         throw ScriptRuntime.typeError1(cx, "msg.arg.not.object", ScriptRuntime.typeof(cx, key));
      } else {
         this.map.put((Scriptable)key, Boolean.TRUE);
         return this;
      }
   }

   private Object js_delete(Object key) {
      return !isValidValue(key) ? Boolean.FALSE : this.map.remove(key) != null;
   }

   private Object js_has(Object key) {
      return !isValidValue(key) ? Boolean.FALSE : this.map.containsKey(key);
   }

   private static boolean isValidValue(Object v) {
      return ScriptRuntime.isUnregisteredSymbol(v) || ScriptRuntime.isObject(v);
   }

   private static NativeWeakSet realThis(Scriptable thisObj, String name, Context cx) {
      NativeWeakSet ns = LambdaConstructor.convertThisObject(cx, thisObj, NativeWeakSet.class);
      if (!ns.instanceOfWeakSet) {
         throw ScriptRuntime.typeError1(cx, "msg.incompat.call", name);
      } else {
         return ns;
      }
   }
}
