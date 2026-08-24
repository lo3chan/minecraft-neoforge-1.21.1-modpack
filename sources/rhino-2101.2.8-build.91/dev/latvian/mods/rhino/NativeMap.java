package dev.latvian.mods.rhino;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NativeMap extends ScriptableObject {
   private static final String CLASS_NAME = "Map";
   static final String ITERATOR_TAG = "Map Iterator";
   private final Hashtable entries;
   private boolean instanceOfMap = false;

   public NativeMap(Context cx) {
      this.entries = new Hashtable(cx);
   }

   static void init(Context cx, Scriptable scope, boolean sealed) {
      LambdaConstructor constructor = new LambdaConstructor(cx, scope, "Map", 0, 2, NativeMap::jsConstructor);
      constructor.setPrototypePropertyAttributes(7);
      constructor.defineConstructorMethod(cx, scope, "groupBy", 2, NativeMap::jsGroupBy, 2);
      constructor.definePrototypeMethod(
         cx,
         scope,
         "set",
         2,
         (lcx, lscope, thisObj, args) -> realThis(thisObj, "set", lcx).js_set(lcx, key(args), args.length > 1 ? args[1] : Undefined.INSTANCE),
         2,
         3
      );
      constructor.definePrototypeMethod(
         cx, scope, "delete", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "delete", lcx).js_delete(lcx, key(args)), 2, 3
      );
      constructor.definePrototypeMethod(cx, scope, "get", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "get", lcx).js_get(lcx, key(args)), 2, 3);
      constructor.definePrototypeMethod(cx, scope, "has", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "has", lcx).js_has(lcx, key(args)), 2, 3);
      constructor.definePrototypeMethod(cx, scope, "clear", 0, (lcx, lscope, thisObj, args) -> realThis(thisObj, "clear", lcx).js_clear(lcx), 2, 3);
      constructor.definePrototypeMethod(
         cx,
         scope,
         "keys",
         0,
         (lcx, lscope, thisObj, args) -> realThis(thisObj, "keys", lcx).js_iterator(lscope, NativeCollectionIterator.Type.KEYS, lcx),
         2,
         3
      );
      constructor.definePrototypeMethod(
         cx,
         scope,
         "values",
         0,
         (lcx, lscope, thisObj, args) -> realThis(thisObj, "values", lcx).js_iterator(lscope, NativeCollectionIterator.Type.VALUES, lcx),
         2,
         3
      );
      constructor.definePrototypeMethod(
         cx,
         scope,
         "forEach",
         1,
         (lcx, lscope, thisObj, args) -> realThis(thisObj, "forEach", lcx)
            .js_forEach(lcx, lscope, args.length > 0 ? args[0] : Undefined.INSTANCE, args.length > 1 ? args[1] : Undefined.INSTANCE),
         2,
         3
      );
      constructor.definePrototypeMethod(
         cx,
         scope,
         "entries",
         0,
         (lcx, lscope, thisObj, args) -> realThis(thisObj, "entries", lcx).js_iterator(lscope, NativeCollectionIterator.Type.BOTH, lcx),
         2,
         3
      );
      constructor.definePrototypeAlias(cx, "entries", SymbolKey.ITERATOR, 2);
      constructor.definePrototypeProperty(cx, "size", thisObj -> realThis(thisObj, "size", cx).js_getSize(), 2);
      constructor.definePrototypeProperty(cx, SymbolKey.TO_STRING_TAG, "Map", 3);
      ScriptRuntimeES6.addSymbolSpecies(cx, scope, constructor);
      ScriptableObject.defineProperty(scope, "Map", constructor, 2, cx);
      if (sealed) {
         constructor.sealObject(cx);
      }
   }

   static Object key(Object[] args) {
      return args.length > 0 ? args[0] : Undefined.INSTANCE;
   }

   private static Scriptable jsConstructor(Context cx, Scriptable scope, Object[] args) {
      NativeMap nm = new NativeMap(cx);
      nm.instanceOfMap = true;
      if (args.length > 0) {
         loadFromIterable(cx, scope, nm, key(args));
      }

      return nm;
   }

   private static Object jsGroupBy(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Object items = args.length < 1 ? Undefined.INSTANCE : args[0];
      Object callback = args.length < 2 ? Undefined.INSTANCE : args[1];
      Map<Object, List<Object>> groups = AbstractEcmaObjectOperations.groupBy(
         cx, scope, "Map", "groupBy", items, callback, AbstractEcmaObjectOperations.KEY_COERCION.COLLECTION
      );
      NativeMap map = (NativeMap)cx.newObject(scope, "Map");

      for (Entry<Object, List<Object>> entry : groups.entrySet()) {
         Scriptable elements = cx.newArray(scope, entry.getValue().toArray());
         map.entries.put(cx, entry.getKey(), elements);
      }

      return map;
   }

   static void loadFromIterable(Context cx, Scriptable scope, ScriptableObject map, Object arg1) {
      if (arg1 != null && !Undefined.INSTANCE.equals(arg1)) {
         Object ito = ScriptRuntime.callIterator(arg1, cx, scope);
         if (!Undefined.INSTANCE.equals(ito)) {
            ScriptableObject dummy = ensureScriptableObject(cx.newObject(scope, map.getClassName()), cx);
            Callable set = ScriptRuntime.getPropFunctionAndThis(cx, scope, dummy.getPrototype(cx), "set");
            cx.lastStoredScriptable();

            try (IteratorLikeIterable it = new IteratorLikeIterable(cx, scope, ito)) {
               for (Object val : it) {
                  Scriptable sVal = ensureScriptable(val, cx);
                  if (sVal instanceof Symbol) {
                     throw ScriptRuntime.typeError1(cx, "msg.arg.not.object", ScriptRuntime.typeof(cx, sVal));
                  }

                  Object finalKey = sVal.get(cx, 0, sVal);
                  if (finalKey == NOT_FOUND) {
                     finalKey = Undefined.INSTANCE;
                  }

                  Object finalVal = sVal.get(cx, 1, sVal);
                  if (finalVal == NOT_FOUND) {
                     finalVal = Undefined.INSTANCE;
                  }

                  set.call(cx, scope, map, new Object[]{finalKey, finalVal});
               }
            }
         }
      }
   }

   private static NativeMap realThis(Scriptable thisObj, String name, Context cx) {
      NativeMap nm = LambdaConstructor.convertThisObject(cx, thisObj, NativeMap.class);
      if (!nm.instanceOfMap) {
         throw ScriptRuntime.typeError1(cx, "msg.incompat.call", name);
      } else {
         return nm;
      }
   }

   @Override
   public String getClassName() {
      return "Map";
   }

   private Object js_set(Context cx, Object k, Object v) {
      Object key = k;
      if (k instanceof Number && ((Number)k).doubleValue() == ScriptRuntime.negativeZero) {
         key = ScriptRuntime.zeroObj;
      }

      this.entries.put(cx, key, v);
      return this;
   }

   private Object js_delete(Context cx, Object arg) {
      return this.entries.deleteEntry(cx, arg);
   }

   private Object js_get(Context cx, Object arg) {
      Hashtable.Entry entry = this.entries.getEntry(cx, arg);
      return entry == null ? Undefined.INSTANCE : entry.value;
   }

   private Object js_has(Context cx, Object arg) {
      return this.entries.has(cx, arg);
   }

   private Object js_getSize() {
      return this.entries.size();
   }

   private Object js_iterator(Scriptable scope, NativeCollectionIterator.Type type, Context cx) {
      return new NativeCollectionIterator(scope, "Map Iterator", type, this.entries.iterator(), cx);
   }

   private Object js_clear(Context cx) {
      this.entries.clear(cx);
      return Undefined.INSTANCE;
   }

   private Object js_forEach(Context cx, Scriptable scope, Object arg1, Object arg2) {
      if (arg1 instanceof Callable f) {
         boolean isStrict = cx.isStrictMode();

         for (Hashtable.Entry entry : this.entries) {
            Scriptable thisObj = ScriptRuntime.toObjectOrNull(cx, arg2, scope);
            if (thisObj == null && !isStrict) {
               thisObj = scope;
            }

            if (thisObj == null) {
               thisObj = Undefined.SCRIPTABLE_INSTANCE;
            }

            f.call(cx, scope, thisObj, new Object[]{entry.value, entry.key, this});
         }

         return Undefined.INSTANCE;
      } else {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", arg1, ScriptRuntime.typeof(cx, arg1));
      }
   }
}
