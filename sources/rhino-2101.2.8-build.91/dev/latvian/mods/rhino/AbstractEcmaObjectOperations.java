package dev.latvian.mods.rhino;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AbstractEcmaObjectOperations {
   static boolean hasOwnProperty(Context cx, Object o, Object property) {
      Scriptable obj = ScriptableObject.ensureScriptable(o, cx);
      boolean result;
      if (property instanceof Symbol sym) {
         result = ScriptableObject.ensureSymbolScriptable(o, cx).has(cx, sym, obj);
      } else {
         ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, property);
         if (s.stringId == null) {
            result = obj.has(cx, s.index, obj);
         } else {
            result = obj.has(cx, s.stringId, obj);
         }
      }

      return result;
   }

   static boolean testIntegrityLevel(Context cx, Object o, AbstractEcmaObjectOperations.INTEGRITY_LEVEL level) {
      ScriptableObject obj = ScriptableObject.ensureScriptableObject(o, cx);
      if (obj.isExtensible()) {
         return Boolean.FALSE;
      } else {
         for (Object name : obj.getIds(cx, true, true)) {
            ScriptableObject desc = obj.getOwnPropertyDescriptor(cx, name);
            if (Boolean.TRUE.equals(desc.get(cx, "configurable"))) {
               return Boolean.FALSE;
            }

            if (level == AbstractEcmaObjectOperations.INTEGRITY_LEVEL.FROZEN
               && ScriptableObject.isDataDescriptor(desc, cx)
               && Boolean.TRUE.equals(desc.get(cx, "writable"))) {
               return Boolean.FALSE;
            }
         }

         return Boolean.TRUE;
      }
   }

   static boolean setIntegrityLevel(Context cx, Object o, AbstractEcmaObjectOperations.INTEGRITY_LEVEL level) {
      ScriptableObject obj = ScriptableObject.ensureScriptableObject(o, cx);

      for (Object key : obj.getIds(cx, true, true)) {
         ScriptableObject desc = obj.getOwnPropertyDescriptor(cx, key);
         if (level == AbstractEcmaObjectOperations.INTEGRITY_LEVEL.SEALED) {
            if (Boolean.TRUE.equals(desc.get(cx, "configurable"))) {
               desc.put(cx, "configurable", desc, Boolean.FALSE);
               obj.defineOwnProperty(cx, key, desc, false);
            }
         } else {
            if (ScriptableObject.isDataDescriptor(desc, cx) && Boolean.TRUE.equals(desc.get(cx, "writable"))) {
               desc.put(cx, "writable", desc, Boolean.FALSE);
            }

            if (Boolean.TRUE.equals(desc.get(cx, "configurable"))) {
               desc.put(cx, "configurable", desc, Boolean.FALSE);
            }

            obj.defineOwnProperty(cx, key, desc, false);
         }
      }

      obj.preventExtensions();
      return true;
   }

   static Map<Object, List<Object>> groupBy(
      Context cx, Scriptable scope, IdFunctionObject f, Object items, Object callback, AbstractEcmaObjectOperations.KEY_COERCION keyCoercion
   ) {
      return groupBy(cx, scope, f.getTag(), f.getFunctionName(), items, callback, keyCoercion);
   }

   static Map<Object, List<Object>> groupBy(
      Context cx, Scriptable scope, Object tag, Object methodName, Object items, Object callback, AbstractEcmaObjectOperations.KEY_COERCION keyCoercion
   ) {
      ScriptRuntimeES6.requireObjectCoercible(cx, items, tag, methodName);
      if (!(callback instanceof Callable)) {
         throw ScriptRuntime.notFunctionError(cx, callback);
      } else {
         Map<Object, List<Object>> groups = new LinkedHashMap<>();
         Object iterator = ScriptRuntime.callIterator(items, cx, scope);

         try (IteratorLikeIterable it = new IteratorLikeIterable(cx, scope, iterator)) {
            double i = 0.0;

            for (Object o : it) {
               if (i > 9.007199254740991E15) {
                  it.close();
                  throw ScriptRuntime.typeError(cx, "Too many values to iterate");
               }

               Object[] args = new Object[]{o, i};
               Object key = ((Callable)callback).call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, args);
               if (keyCoercion == AbstractEcmaObjectOperations.KEY_COERCION.PROPERTY) {
                  if (!ScriptRuntime.isSymbol(key)) {
                     key = ScriptRuntime.toString(cx, key);
                  }
               } else {
                  assert keyCoercion == AbstractEcmaObjectOperations.KEY_COERCION.COLLECTION;

                  if (key instanceof Number && ((Number)key).doubleValue() == ScriptRuntime.negativeZero) {
                     key = ScriptRuntime.zeroObj;
                  }
               }

               List<Object> group = groups.computeIfAbsent(key, k -> new ArrayList<>());
               group.add(o);
               i++;
            }
         }

         return groups;
      }
   }

   public static Constructable speciesConstructor(Context cx, Scriptable s, Constructable defaultConstructor) {
      Object constructor = ScriptableObject.getProperty(s, "constructor", cx);
      if (constructor == Scriptable.NOT_FOUND || Undefined.isUndefined(constructor)) {
         return defaultConstructor;
      } else if (!ScriptRuntime.isObject(constructor)) {
         throw ScriptRuntime.typeError1(cx, "msg.arg.not.object", ScriptRuntime.typeof(cx, constructor).toString());
      } else {
         Object species = ScriptableObject.getProperty((Scriptable)constructor, SymbolKey.SPECIES, cx);
         if (species == Scriptable.NOT_FOUND || species == null || Undefined.isUndefined(species)) {
            return defaultConstructor;
         } else if (!(species instanceof Constructable)) {
            throw ScriptRuntime.typeError1(cx, "msg.not.ctor", ScriptRuntime.typeof(cx, species).toString());
         } else {
            return (Constructable)species;
         }
      }
   }

   static void put(Context cx, Scriptable o, String p, Object v, boolean isThrow) {
      Scriptable base = ScriptableObject.getBase(o, p, cx);
      if (base == null) {
         base = o;
      }

      if (base instanceof ScriptableObject so) {
         if (so.putImpl(cx, p, 0, o, v, isThrow)) {
            return;
         }

         o.put(cx, p, o, v);
      } else {
         base.put(cx, p, o, v);
      }
   }

   static void put(Context cx, Scriptable o, int p, Object v, boolean isThrow) {
      Scriptable base = ScriptableObject.getBase(cx, o, p);
      if (base == null) {
         base = o;
      }

      if (base instanceof ScriptableObject so) {
         if (so.putImpl(cx, null, p, o, v, isThrow)) {
            return;
         }

         o.put(cx, p, o, v);
      } else {
         base.put(cx, p, o, v);
      }
   }

   static void put(Context cx, Scriptable o, Symbol p, Object v, boolean isThrow) {
      Scriptable base = ScriptableObject.getBase(cx, o, p);
      if (base == null) {
         base = o;
      }

      if (base instanceof ScriptableObject so) {
         if (so.putImpl(cx, p, 0, o, v, isThrow)) {
            return;
         }

         ScriptableObject.ensureSymbolScriptable(o, cx).put(cx, p, o, v);
      } else {
         ScriptableObject.ensureSymbolScriptable(base, cx).put(cx, p, o, v);
      }
   }

   static enum INTEGRITY_LEVEL {
      FROZEN,
      SEALED;
   }

   static enum KEY_COERCION {
      PROPERTY,
      COLLECTION;
   }
}
