package dev.latvian.mods.rhino;

import java.util.Iterator;

public class NativeSet extends ScriptableObject {
   private static final String CLASS_NAME = "Set";
   static final String ITERATOR_TAG = "Set Iterator";
   static final SymbolKey GETSIZE = new SymbolKey("[Symbol.getSize]");
   private final Hashtable entries;
   private boolean instanceOfSet = false;

   public NativeSet(Context cx) {
      this.entries = new Hashtable(cx);
   }

   static void init(Context cx, Scriptable scope, boolean sealed) {
      LambdaConstructor constructor = new LambdaConstructor(cx, scope, "Set", 0, 2, NativeSet::jsConstructor);
      constructor.setPrototypePropertyAttributes(7);
      constructor.definePrototypeMethod(cx, scope, "add", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "add", lcx).js_add(lcx, key(args)), 2, 3);
      constructor.definePrototypeMethod(
         cx, scope, "delete", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "delete", lcx).js_delete(lcx, key(args)), 2, 3
      );
      constructor.definePrototypeMethod(cx, scope, "has", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "has", lcx).js_has(lcx, key(args)), 2, 3);
      constructor.definePrototypeMethod(cx, scope, "clear", 0, (lcx, lscope, thisObj, args) -> realThis(thisObj, "clear", lcx).js_clear(lcx), 2, 3);
      constructor.definePrototypeMethod(
         cx,
         scope,
         "values",
         0,
         (lcx, lscope, thisObj, args) -> realThis(thisObj, "values", lcx).js_iterator(lscope, NativeCollectionIterator.Type.VALUES, lcx),
         2,
         3
      );
      constructor.definePrototypeAlias(cx, "values", "keys", 3);
      constructor.definePrototypeAlias(cx, "values", SymbolKey.ITERATOR, 2);
      constructor.definePrototypeMethod(
         cx,
         scope,
         "entries",
         0,
         (lcx, lscope, thisObj, args) -> realThis(thisObj, "entries", lcx).js_iterator(lscope, NativeCollectionIterator.Type.BOTH, lcx),
         2,
         3
      );
      constructor.definePrototypeMethod(
         cx,
         scope,
         "forEach",
         1,
         (lcx, lscope, thisObj, args) -> realThis(thisObj, "forEach", lcx).js_forEach(lcx, lscope, key(args), args.length > 1 ? args[1] : Undefined.INSTANCE),
         2,
         3
      );
      constructor.definePrototypeMethod(
         cx, scope, "intersection", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "intersection", lcx).js_intersection(lcx, lscope, args), 2, 3
      );
      constructor.definePrototypeMethod(
         cx, scope, "union", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "union", lcx).js_union(lcx, lscope, args), 2, 3
      );
      constructor.definePrototypeMethod(
         cx, scope, "difference", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "difference", lcx).js_difference(lcx, lscope, args), 2, 3
      );
      constructor.definePrototypeMethod(
         cx,
         scope,
         "symmetricDifference",
         1,
         (lcx, lscope, thisObj, args) -> realThis(thisObj, "symmetricDifference", lcx).js_symmetricDifference(lcx, lscope, args),
         2,
         3
      );
      constructor.definePrototypeMethod(
         cx, scope, "isSubsetOf", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "isSubsetOf", lcx).js_isSubsetOf(lcx, lscope, args), 2, 3
      );
      constructor.definePrototypeMethod(
         cx, scope, "isSupersetOf", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "isSupersetOf", lcx).js_isSupersetOf(lcx, lscope, args), 2, 3
      );
      constructor.definePrototypeMethod(
         cx, scope, "isDisjointFrom", 1, (lcx, lscope, thisObj, args) -> realThis(thisObj, "isDisjointFrom", lcx).js_isDisjointFrom(lcx, lscope, args), 2, 3
      );
      constructor.definePrototypeProperty(cx, "size", thisObj -> realThis(thisObj, "size", cx).js_getSize(), 2);
      constructor.definePrototypeProperty(cx, SymbolKey.TO_STRING_TAG, "Set", 3);
      ScriptRuntimeES6.addSymbolSpecies(cx, scope, constructor);
      ScriptableObject.defineProperty(scope, "Set", constructor, 2, cx);
      if (sealed) {
         constructor.sealObject(cx);
      }
   }

   private static Object key(Object[] args) {
      return args.length > 0 ? args[0] : Undefined.INSTANCE;
   }

   private static Scriptable jsConstructor(Context cx, Scriptable scope, Object[] args) {
      NativeSet ns = new NativeSet(cx);
      ns.instanceOfSet = true;
      if (args.length > 0) {
         loadFromIterable(cx, scope, ns, key(args));
      }

      return ns;
   }

   static void loadFromIterable(Context cx, Scriptable scope, ScriptableObject set, Object arg1) {
      if (arg1 != null && !Undefined.INSTANCE.equals(arg1)) {
         Object ito = ScriptRuntime.callIterator(arg1, cx, scope);
         if (!Undefined.INSTANCE.equals(ito)) {
            ScriptableObject dummy = ensureScriptableObject(cx.newObject(scope, set.getClassName()), cx);
            Callable add = ScriptRuntime.getPropFunctionAndThis(cx, scope, dummy.getPrototype(cx), "add");
            cx.lastStoredScriptable();

            try (IteratorLikeIterable it = new IteratorLikeIterable(cx, scope, ito)) {
               for (Object val : it) {
                  Object finalVal = val == NOT_FOUND ? Undefined.INSTANCE : val;
                  add.call(cx, scope, set, new Object[]{finalVal});
               }
            }
         }
      }
   }

   private static NativeSet realThis(Scriptable thisObj, String name, Context cx) {
      NativeSet ns = LambdaConstructor.convertThisObject(cx, thisObj, NativeSet.class);
      if (!ns.instanceOfSet) {
         throw ScriptRuntime.typeError1(cx, "msg.incompat.call", name);
      } else {
         return ns;
      }
   }

   @Override
   public String getClassName() {
      return "Set";
   }

   private Object js_add(Context cx, Object k) {
      Object key = k;
      if (k instanceof Number && ((Number)k).doubleValue() == ScriptRuntime.negativeZero) {
         key = ScriptRuntime.zeroObj;
      }

      this.entries.put(cx, key, key);
      return this;
   }

   private Object js_delete(Context cx, Object arg) {
      return this.entries.deleteEntry(cx, arg);
   }

   private Object js_has(Context cx, Object arg) {
      return arg instanceof Number && ((Number)arg).doubleValue() == ScriptRuntime.negativeZero
         ? this.entries.has(cx, ScriptRuntime.zeroObj)
         : this.entries.has(cx, arg);
   }

   private Object js_clear(Context cx) {
      this.entries.clear(cx);
      return Undefined.INSTANCE;
   }

   private Object js_getSize() {
      return this.entries.size();
   }

   private Object js_iterator(Scriptable scope, NativeCollectionIterator.Type type, Context cx) {
      return new NativeCollectionIterator(scope, "Set Iterator", type, this.entries.iterator(), cx);
   }

   private Object js_forEach(Context cx, Scriptable scope, Object arg1, Object arg2) {
      if (arg1 instanceof Callable f) {
         boolean isStrict = cx.isStrictMode();
         Iterator i = this.entries.iterator();

         while (i.hasNext()) {
            Scriptable thisObj = ScriptRuntime.toObjectOrNull(cx, arg2, scope);
            if (thisObj == null && !isStrict) {
               thisObj = scope;
            }

            if (thisObj == null) {
               thisObj = Undefined.SCRIPTABLE_INSTANCE;
            }

            Hashtable.Entry e = (Hashtable.Entry)i.next();
            f.call(cx, scope, thisObj, new Object[]{e.value, e.value, this});
         }

         return Undefined.INSTANCE;
      } else {
         throw ScriptRuntime.notFunctionError(cx, arg1);
      }
   }

   private Object js_intersection(Context cx, Scriptable scope, Object[] args) {
      Object otherObj = args.length > 0 ? args[0] : Undefined.INSTANCE;
      NativeSet result = (NativeSet)cx.newObject(scope, "Set");
      result.instanceOfSet = true;
      Scriptable scriptable = ensureScriptable(otherObj, cx);
      Object sizeVal = getProperty(scriptable, "size", cx);
      Object hasVal = getProperty(scriptable, "has", cx);
      Object keysVal = getProperty(scriptable, "keys", cx);
      validateSetLike(cx, sizeVal, hasVal, keysVal);
      return this.js_intersectionSetLike(cx, scope, otherObj, result, sizeVal, hasVal, keysVal);
   }

   private Object js_intersectionSetLike(Context cx, Scriptable scope, Object otherObj, NativeSet result, Object sizeVal, Object hasVal, Object keysVal) {
      if (!(hasVal instanceof Callable)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "has", ScriptRuntime.typeof(cx, hasVal));
      } else if (!(keysVal instanceof Callable)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "keys", ScriptRuntime.typeof(cx, keysVal));
      } else {
         Callable hasMethod = (Callable)hasVal;
         Callable keysMethod = (Callable)keysVal;
         int otherSize = toSetSize(cx, sizeVal);
         int thisSize = this.entries.size();
         if (thisSize <= otherSize) {
            for (Hashtable.Entry entry : this.entries) {
               Object key = entry.key;
               Object inOther = callHas(cx, scope, otherObj, hasMethod, key);
               if (ScriptRuntime.toBoolean(cx, inOther)) {
                  result.js_add(cx, key);
               }
            }
         } else {
            Object iterator = ScriptRuntime.callIterator(keysMethod.call(cx, scope, ensureScriptable(otherObj, cx), ScriptRuntime.EMPTY_OBJECTS), cx, scope);

            try (IteratorLikeIterable it = new IteratorLikeIterable(cx, scope, iterator)) {
               for (Object key : it) {
                  if (this.js_has(cx, key) == Boolean.TRUE) {
                     result.js_add(cx, key);
                  }
               }
            }
         }

         return result;
      }
   }

   private Object js_union(Context cx, Scriptable scope, Object[] args) {
      Object otherObj = args.length > 0 ? args[0] : Undefined.INSTANCE;
      NativeSet result = (NativeSet)cx.newObject(scope, "Set");
      result.instanceOfSet = true;

      for (Hashtable.Entry entry : this.entries) {
         result.js_add(cx, entry.key);
      }

      Scriptable scriptable = ensureScriptable(otherObj, cx);
      Object sizeVal = getProperty(scriptable, "size", cx);
      Object hasVal = getProperty(scriptable, "has", cx);
      Object keysVal = getProperty(scriptable, "keys", cx);
      validateSetLike(cx, sizeVal, hasVal, keysVal);
      toSetSize(cx, sizeVal);
      if (!(hasVal instanceof Callable)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "has", ScriptRuntime.typeof(cx, hasVal));
      } else if (!(keysVal instanceof Callable keysMethod)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "keys", ScriptRuntime.typeof(cx, keysVal));
      } else {
         Object iterator = ScriptRuntime.callIterator(keysMethod.call(cx, scope, scriptable, ScriptRuntime.EMPTY_OBJECTS), cx, scope);

         try (IteratorLikeIterable it = new IteratorLikeIterable(cx, scope, iterator)) {
            for (Object key : it) {
               result.js_add(cx, key);
            }
         }

         return result;
      }
   }

   private Object js_difference(Context cx, Scriptable scope, Object[] args) {
      Object otherObj = args.length > 0 ? args[0] : Undefined.INSTANCE;
      NativeSet result = (NativeSet)cx.newObject(scope, "Set");
      result.instanceOfSet = true;
      Scriptable scriptable = ensureScriptable(otherObj, cx);
      Object sizeVal = getProperty(scriptable, "size", cx);
      Object hasVal = getProperty(scriptable, "has", cx);
      Object keysVal = getProperty(scriptable, "keys", cx);
      validateSetLike(cx, sizeVal, hasVal, keysVal);
      return this.js_differenceSetLike(cx, scope, otherObj, result, sizeVal, hasVal, keysVal);
   }

   private Object js_differenceSetLike(Context cx, Scriptable scope, Object otherObj, NativeSet result, Object sizeVal, Object hasVal, Object keysVal) {
      if (!(hasVal instanceof Callable)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "has", ScriptRuntime.typeof(cx, hasVal));
      } else if (!(keysVal instanceof Callable)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "keys", ScriptRuntime.typeof(cx, keysVal));
      } else {
         Callable hasMethod = (Callable)hasVal;
         Callable keysMethod = (Callable)keysVal;
         int otherSize = toSetSize(cx, sizeVal);
         int thisSize = this.entries.size();
         if (thisSize > otherSize) {
            for (Hashtable.Entry entry : this.entries) {
               result.js_add(cx, entry.key);
            }

            Object iterator = ScriptRuntime.callIterator(keysMethod.call(cx, scope, ensureScriptable(otherObj, cx), ScriptRuntime.EMPTY_OBJECTS), cx, scope);

            try (IteratorLikeIterable it = new IteratorLikeIterable(cx, scope, iterator)) {
               for (Object key : it) {
                  if (key instanceof Number && ((Number)key).doubleValue() == ScriptRuntime.negativeZero) {
                     key = ScriptRuntime.zeroObj;
                  }

                  result.js_delete(cx, key);
               }
            }
         } else {
            for (Hashtable.Entry entry : this.entries) {
               Object key = entry.key;
               Object inOther = callHas(cx, scope, otherObj, hasMethod, key);
               if (!ScriptRuntime.toBoolean(cx, inOther)) {
                  result.js_add(cx, key);
               }
            }
         }

         return result;
      }
   }

   private Object js_symmetricDifference(Context cx, Scriptable scope, Object[] args) {
      Object otherObj = args.length > 0 ? args[0] : Undefined.INSTANCE;
      NativeSet result = (NativeSet)cx.newObject(scope, "Set");
      result.instanceOfSet = true;
      Scriptable scriptable = ensureScriptable(otherObj, cx);
      Object sizeVal = getProperty(scriptable, "size", cx);
      Object hasVal = getProperty(scriptable, "has", cx);
      Object keysVal = getProperty(scriptable, "keys", cx);
      validateSetLike(cx, sizeVal, hasVal, keysVal);
      toSetSize(cx, sizeVal);
      if (!(hasVal instanceof Callable)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "has", ScriptRuntime.typeof(cx, hasVal));
      } else if (!(keysVal instanceof Callable)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "keys", ScriptRuntime.typeof(cx, keysVal));
      } else {
         Callable hasMethod = (Callable)hasVal;
         Callable keysMethod = (Callable)keysVal;

         for (Hashtable.Entry entry : this.entries) {
            Object key = entry.key;
            Object inOther = callHas(cx, scope, otherObj, hasMethod, key);
            if (!ScriptRuntime.toBoolean(cx, inOther)) {
               result.js_add(cx, key);
            }
         }

         Object iterator = ScriptRuntime.callIterator(keysMethod.call(cx, scope, scriptable, ScriptRuntime.EMPTY_OBJECTS), cx, scope);

         try (IteratorLikeIterable it = new IteratorLikeIterable(cx, scope, iterator)) {
            for (Object key : it) {
               if (this.js_has(cx, key) != Boolean.TRUE) {
                  result.js_add(cx, key);
               }
            }
         }

         return result;
      }
   }

   private Object js_isSubsetOf(Context cx, Scriptable scope, Object[] args) {
      Object otherObj = args.length > 0 ? args[0] : Undefined.INSTANCE;
      Scriptable scriptable = ensureScriptable(otherObj, cx);
      Object sizeVal = getProperty(scriptable, "size", cx);
      Object hasVal = getProperty(scriptable, "has", cx);
      Object keysVal = getProperty(scriptable, "keys", cx);
      validateSetLike(cx, sizeVal, hasVal, keysVal);
      if (!(hasVal instanceof Callable)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "has", ScriptRuntime.typeof(cx, hasVal));
      } else if (!(keysVal instanceof Callable)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "keys", ScriptRuntime.typeof(cx, keysVal));
      } else {
         Callable hasMethod = (Callable)hasVal;
         int otherSize = toSetSize(cx, sizeVal);
         int thisSize = this.entries.size();
         if (thisSize > otherSize) {
            return Boolean.FALSE;
         } else {
            for (Hashtable.Entry entry : this.entries) {
               Object key = entry.key;
               Object inOther = callHas(cx, scope, otherObj, hasMethod, key);
               if (!ScriptRuntime.toBoolean(cx, inOther)) {
                  return Boolean.FALSE;
               }
            }

            return Boolean.TRUE;
         }
      }
   }

   private Object js_isSupersetOf(Context cx, Scriptable scope, Object[] args) {
      Object otherObj = args.length > 0 ? args[0] : Undefined.INSTANCE;
      Scriptable scriptable = ensureScriptable(otherObj, cx);
      Object sizeVal = getProperty(scriptable, "size", cx);
      Object hasVal = getProperty(scriptable, "has", cx);
      Object keysVal = getProperty(scriptable, "keys", cx);
      validateSetLike(cx, sizeVal, hasVal, keysVal);
      toSetSize(cx, sizeVal);
      if (!(hasVal instanceof Callable)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "has", ScriptRuntime.typeof(cx, hasVal));
      } else if (!(keysVal instanceof Callable keysMethod)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "keys", ScriptRuntime.typeof(cx, keysVal));
      } else {
         Object iterator = ScriptRuntime.callIterator(keysMethod.call(cx, scope, scriptable, ScriptRuntime.EMPTY_OBJECTS), cx, scope);

         try (IteratorLikeIterable it = new IteratorLikeIterable(cx, scope, iterator)) {
            for (Object value : it) {
               if (this.js_has(cx, value) != Boolean.TRUE) {
                  return Boolean.FALSE;
               }
            }
         }

         return Boolean.TRUE;
      }
   }

   private Object js_isDisjointFrom(Context cx, Scriptable scope, Object[] args) {
      Object otherObj = args.length > 0 ? args[0] : Undefined.INSTANCE;
      Scriptable scriptable = ensureScriptable(otherObj, cx);
      Object sizeVal = getProperty(scriptable, "size", cx);
      Object hasVal = getProperty(scriptable, "has", cx);
      Object keysVal = getProperty(scriptable, "keys", cx);
      validateSetLike(cx, sizeVal, hasVal, keysVal);
      if (!(hasVal instanceof Callable)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "has", ScriptRuntime.typeof(cx, hasVal));
      } else if (!(keysVal instanceof Callable)) {
         throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "keys", ScriptRuntime.typeof(cx, keysVal));
      } else {
         Callable hasMethod = (Callable)hasVal;
         Callable keysMethod = (Callable)keysVal;
         int otherSize = toSetSize(cx, sizeVal);
         int thisSize = this.entries.size();
         if (thisSize <= otherSize) {
            for (Hashtable.Entry entry : this.entries) {
               Object key = entry.key;
               Object inOther = callHas(cx, scope, otherObj, hasMethod, key);
               if (ScriptRuntime.toBoolean(cx, inOther)) {
                  return Boolean.FALSE;
               }
            }
         } else {
            Object iterator = ScriptRuntime.callIterator(keysMethod.call(cx, scope, scriptable, ScriptRuntime.EMPTY_OBJECTS), cx, scope);

            try (IteratorLikeIterable it = new IteratorLikeIterable(cx, scope, iterator)) {
               for (Object key : it) {
                  if (this.js_has(cx, key) == Boolean.TRUE) {
                     return Boolean.FALSE;
                  }
               }
            }
         }

         return Boolean.TRUE;
      }
   }

   private static Object callHas(Context cx, Scriptable scope, Object obj, Object hasMethod, Object key) {
      return ((Callable)hasMethod).call(cx, scope, ensureScriptable(obj, cx), new Object[]{key});
   }

   private static int toSetSize(Context cx, Object sizeVal) {
      double otherSizeDouble = ScriptRuntime.toNumber(cx, sizeVal);
      if (Double.isNaN(otherSizeDouble)) {
         throw ScriptRuntime.typeError(cx, "size is not a number");
      } else {
         return Double.isInfinite(otherSizeDouble) ? 2147483647 : (int)Math.floor(otherSizeDouble);
      }
   }

   private static void validateSetLike(Context cx, Object sizeVal, Object hasVal, Object keysVal) {
      if (sizeVal == NOT_FOUND) {
         throw ScriptRuntime.typeError(cx, "Set-like object must have a 'size' property");
      } else if (hasVal == NOT_FOUND) {
         throw ScriptRuntime.typeError(cx, "Set-like object must have a 'has' method");
      } else if (keysVal == NOT_FOUND) {
         throw ScriptRuntime.typeError(cx, "Set-like object must have a 'keys' method");
      }
   }
}
