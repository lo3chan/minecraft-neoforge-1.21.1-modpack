package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.util.DataObject;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class NativeArray extends ScriptableObject implements List, DataObject {
   private static final Object ARRAY_TAG = "Array";
   private static final String CLASS_NAME = "Array";
   private static final Long NEGATIVE_ONE = -1L;
   private static final String[] UNSCOPABLES = new String[]{
      "at",
      "copyWithin",
      "entries",
      "fill",
      "find",
      "findIndex",
      "findLast",
      "findLastIndex",
      "flat",
      "flatMap",
      "includes",
      "keys",
      "toReversed",
      "toSorted",
      "toSpliced",
      "values"
   };
   private static final int Id_length = 1;
   private static final int MAX_INSTANCE_ID = 1;
   private final Context localContext;
   private long length;
   private int lengthAttr = 6;
   private transient int modCount;
   private Object[] dense;
   private boolean denseOnly;
   private static int maximumInitialCapacity = 10000;
   private static final int DEFAULT_INITIAL_CAPACITY = 10;
   private static final double GROW_FACTOR = 1.5;
   private static final int MAX_PRE_GROW_SIZE = 1431655764;

   static void init(Scriptable scope, boolean sealed, Context cx) {
      LambdaConstructor ctor = new LambdaConstructor(cx, scope, "Array", 1, NativeArray::jsConstructor);
      NativeArray proto = new NativeArray(cx, 0L);
      ctor.setPrototypeScriptable(proto, cx);
      defineMethodOnConstructor(cx, ctor, scope, "of", 0, NativeArray::js_of);
      defineMethodOnConstructor(cx, ctor, scope, "from", 1, NativeArray::js_from);
      defineMethodOnConstructor(cx, ctor, scope, "isArray", 1, NativeArray::js_isArrayMethod);
      exposeMethodOnConstructor(cx, ctor, scope, "join", 1, NativeArray::js_join);
      exposeMethodOnConstructor(cx, ctor, scope, "reverse", 0, NativeArray::js_reverse);
      exposeMethodOnConstructor(cx, ctor, scope, "sort", 1, NativeArray::js_sort);
      exposeMethodOnConstructor(cx, ctor, scope, "push", 1, NativeArray::js_push);
      exposeMethodOnConstructor(cx, ctor, scope, "pop", 0, NativeArray::js_pop);
      exposeMethodOnConstructor(cx, ctor, scope, "shift", 0, NativeArray::js_shift);
      exposeMethodOnConstructor(cx, ctor, scope, "unshift", 1, NativeArray::js_unshift);
      exposeMethodOnConstructor(cx, ctor, scope, "splice", 2, NativeArray::js_splice);
      exposeMethodOnConstructor(cx, ctor, scope, "concat", 1, NativeArray::js_concat);
      exposeMethodOnConstructor(cx, ctor, scope, "slice", 2, NativeArray::js_slice);
      exposeMethodOnConstructor(cx, ctor, scope, "indexOf", 1, NativeArray::js_indexOf);
      exposeMethodOnConstructor(cx, ctor, scope, "lastIndexOf", 1, NativeArray::js_lastIndexOf);
      exposeMethodOnConstructor(cx, ctor, scope, "every", 1, NativeArray::js_every);
      exposeMethodOnConstructor(cx, ctor, scope, "filter", 1, NativeArray::js_filter);
      exposeMethodOnConstructor(cx, ctor, scope, "forEach", 1, NativeArray::js_forEach);
      exposeMethodOnConstructor(cx, ctor, scope, "map", 1, NativeArray::js_map);
      exposeMethodOnConstructor(cx, ctor, scope, "some", 1, NativeArray::js_some);
      exposeMethodOnConstructor(cx, ctor, scope, "find", 1, NativeArray::js_find);
      exposeMethodOnConstructor(cx, ctor, scope, "findIndex", 1, NativeArray::js_findIndex);
      exposeMethodOnConstructor(cx, ctor, scope, "findLast", 1, NativeArray::js_findLast);
      exposeMethodOnConstructor(cx, ctor, scope, "findLastIndex", 1, NativeArray::js_findLastIndex);
      exposeMethodOnConstructor(cx, ctor, scope, "reduce", 1, NativeArray::js_reduce);
      exposeMethodOnConstructor(cx, ctor, scope, "reduceRight", 1, NativeArray::js_reduceRight);
      defineMethodOnPrototype(cx, ctor, scope, "toString", 0, NativeArray::js_toString);
      defineMethodOnPrototype(cx, ctor, scope, "toLocaleString", 0, NativeArray::js_toLocaleString);
      defineMethodOnPrototype(cx, ctor, scope, "toSource", 0, NativeArray::js_toSource);
      defineMethodOnPrototype(cx, ctor, scope, "join", 1, NativeArray::js_join);
      defineMethodOnPrototype(cx, ctor, scope, "reverse", 0, NativeArray::js_reverse);
      defineMethodOnPrototype(cx, ctor, scope, "sort", 1, NativeArray::js_sort);
      defineMethodOnPrototype(cx, ctor, scope, "push", 1, NativeArray::js_push);
      defineMethodOnPrototype(cx, ctor, scope, "pop", 0, NativeArray::js_pop);
      defineMethodOnPrototype(cx, ctor, scope, "shift", 0, NativeArray::js_shift);
      defineMethodOnPrototype(cx, ctor, scope, "unshift", 1, NativeArray::js_unshift);
      defineMethodOnPrototype(cx, ctor, scope, "splice", 2, NativeArray::js_splice);
      defineMethodOnPrototype(cx, ctor, scope, "concat", 1, NativeArray::js_concat);
      defineMethodOnPrototype(cx, ctor, scope, "slice", 2, NativeArray::js_slice);
      defineMethodOnPrototype(cx, ctor, scope, "indexOf", 1, NativeArray::js_indexOf);
      defineMethodOnPrototype(cx, ctor, scope, "lastIndexOf", 1, NativeArray::js_lastIndexOf);
      defineMethodOnPrototype(cx, ctor, scope, "includes", 1, NativeArray::js_includes);
      defineMethodOnPrototype(cx, ctor, scope, "fill", 1, NativeArray::js_fill);
      defineMethodOnPrototype(cx, ctor, scope, "copyWithin", 2, NativeArray::js_copyWithin);
      defineMethodOnPrototype(cx, ctor, scope, "at", 1, NativeArray::js_at);
      defineMethodOnPrototype(cx, ctor, scope, "flat", 0, NativeArray::js_flat);
      defineMethodOnPrototype(cx, ctor, scope, "flatMap", 1, NativeArray::js_flatMap);
      defineMethodOnPrototype(cx, ctor, scope, "every", 1, NativeArray::js_every);
      defineMethodOnPrototype(cx, ctor, scope, "filter", 1, NativeArray::js_filter);
      defineMethodOnPrototype(cx, ctor, scope, "forEach", 1, NativeArray::js_forEach);
      defineMethodOnPrototype(cx, ctor, scope, "map", 1, NativeArray::js_map);
      defineMethodOnPrototype(cx, ctor, scope, "some", 1, NativeArray::js_some);
      defineMethodOnPrototype(cx, ctor, scope, "find", 1, NativeArray::js_find);
      defineMethodOnPrototype(cx, ctor, scope, "findIndex", 1, NativeArray::js_findIndex);
      defineMethodOnPrototype(cx, ctor, scope, "findLast", 1, NativeArray::js_findLast);
      defineMethodOnPrototype(cx, ctor, scope, "findLastIndex", 1, NativeArray::js_findLastIndex);
      defineMethodOnPrototype(cx, ctor, scope, "reduce", 1, NativeArray::js_reduce);
      defineMethodOnPrototype(cx, ctor, scope, "reduceRight", 1, NativeArray::js_reduceRight);
      defineMethodOnPrototype(cx, ctor, scope, "keys", 0, NativeArray::js_keys);
      defineMethodOnPrototype(cx, ctor, scope, "entries", 0, NativeArray::js_entries);
      defineMethodOnPrototype(cx, ctor, scope, "values", 0, NativeArray::js_values);
      defineMethodOnPrototype(cx, ctor, scope, "toReversed", 0, NativeArray::js_toReversed);
      defineMethodOnPrototype(cx, ctor, scope, "toSorted", 1, NativeArray::js_toSorted);
      defineMethodOnPrototype(cx, ctor, scope, "toSpliced", 2, NativeArray::js_toSpliced);
      defineMethodOnPrototype(cx, ctor, scope, "with", 2, NativeArray::js_with);
      ctor.definePrototypeAlias(cx, "values", SymbolKey.ITERATOR, 2);
      ScriptRuntimeES6.addSymbolSpecies(cx, scope, ctor);
      proto.defineProperty(cx, SymbolKey.UNSCOPABLES, makeUnscopables(cx, scope), 3);
      ctor.setPrototypePropertyAttributes(7);
      ScriptableObject.defineProperty(scope, "Array", ctor, 2, cx);
      if (sealed) {
         ctor.sealObject(cx);
         ((NativeArray)ctor.getPrototypeProperty(cx)).sealObject(cx);
      }
   }

   private static void defineMethodOnConstructor(Context cx, LambdaConstructor constructor, Scriptable scope, String name, int length, Callable target) {
      constructor.defineConstructorMethod(cx, scope, name, length, target, 2, 3);
   }

   private static void defineMethodOnPrototype(Context cx, LambdaConstructor constructor, Scriptable scope, String name, int length, Callable target) {
      constructor.definePrototypeMethod(cx, scope, name, length, target, 2, 3);
   }

   private static void exposeMethodOnConstructor(Context cx, LambdaConstructor constructor, Scriptable scope, String name, int length, Callable target) {
      constructor.defineConstructorMethod(cx, scope, name, length, (c, s, thisObj, args) -> {
         Scriptable realThis = ScriptRuntime.toObject(c, scope, args[0]);
         Object[] realArgs = Arrays.copyOfRange(args, 1, args.length);
         return target.call(c, s, realThis, realArgs);
      }, 2, 3);
   }

   static int getMaximumInitialCapacity() {
      return maximumInitialCapacity;
   }

   static void setMaximumInitialCapacity(int maximumInitialCapacity) {
      NativeArray.maximumInitialCapacity = maximumInitialCapacity;
   }

   public NativeArray(Context cx, long lengthArg) {
      this.localContext = cx;
      this.denseOnly = lengthArg <= maximumInitialCapacity;
      if (this.denseOnly) {
         int intLength = (int)lengthArg;
         if (intLength < 10) {
            intLength = 10;
         }

         this.dense = new Object[intLength];
         Arrays.fill(this.dense, Scriptable.NOT_FOUND);
      }

      this.length = lengthArg;
      this.createLengthProp();
   }

   public NativeArray(Context cx, Object[] array) {
      this.localContext = cx;
      this.denseOnly = true;
      this.dense = array;
      this.length = array.length;
      this.createLengthProp();
   }

   @Override
   public String getClassName() {
      return "Array";
   }

   @Override
   public void setPrototype(Scriptable p) {
      super.setPrototype(p);
      if (!(p instanceof NativeArray)) {
         this.setDenseOnly(false);
      }
   }

   private static Object makeUnscopables(Context cx, Scriptable scope) {
      NativeObject obj = (NativeObject)cx.newObject(scope);
      ScriptableObject desc = ScriptableObject.buildDataDescriptor(obj, true, 0, cx);

      for (String k : UNSCOPABLES) {
         obj.defineOwnProperty(cx, k, desc);
      }

      obj.setPrototype(null);
      return obj;
   }

   @Override
   public Object get(Context cx, int index, Scriptable start) {
      if (!this.denseOnly && this.isGetterOrSetter(null, index, false)) {
         return super.get(cx, index, start);
      } else {
         return this.dense != null && 0 <= index && index < this.dense.length ? this.dense[index] : super.get(cx, index, start);
      }
   }

   @Override
   public boolean has(Context cx, int index, Scriptable start) {
      if (!this.denseOnly && this.isGetterOrSetter(null, index, false)) {
         return super.has(cx, index, start);
      } else {
         return this.dense != null && 0 <= index && index < this.dense.length ? this.dense[index] != NOT_FOUND : super.has(cx, index, start);
      }
   }

   private static long toArrayIndex(Context cx, Object id) {
      if (id instanceof String) {
         return toArrayIndex(cx, (String)id);
      } else {
         return id instanceof Number ? toArrayIndex(((Number)id).doubleValue()) : -1L;
      }
   }

   private static long toArrayIndex(Context cx, String id) {
      long index = toArrayIndex(ScriptRuntime.toNumber(cx, id));
      return Long.toString(index).equals(id) ? index : -1L;
   }

   private static long toArrayIndex(double d) {
      if (!Double.isNaN(d)) {
         long index = ScriptRuntime.toUint32(d);
         if (index == d && index != 4294967295L) {
            return index;
         }
      }

      return -1L;
   }

   private static int toDenseIndex(Context cx, Object id) {
      long index = toArrayIndex(cx, id);
      return 0L <= index && index < 2147483647L ? (int)index : -1;
   }

   @Override
   public void put(Context cx, String id, Scriptable start, Object value) {
      super.put(cx, id, start, value);
      if (start == this) {
         long index = toArrayIndex(cx, id);
         if (index >= this.length) {
            this.length = index + 1L;
            this.modCount++;
            this.denseOnly = false;
         }
      }
   }

   private boolean ensureCapacity(int capacity) {
      if (capacity > this.dense.length) {
         if (capacity > 1431655764) {
            this.denseOnly = false;
            return false;
         }

         capacity = Math.max(capacity, (int)(this.dense.length * 1.5));
         Object[] newDense = new Object[capacity];
         System.arraycopy(this.dense, 0, newDense, 0, this.dense.length);
         Arrays.fill(newDense, this.dense.length, newDense.length, Scriptable.NOT_FOUND);
         this.dense = newDense;
      }

      return true;
   }

   @Override
   public void put(Context cx, int index, Scriptable start, Object value) {
      if (start == this && !this.isSealed(cx) && this.dense != null && 0 <= index && (this.denseOnly || !this.isGetterOrSetter(null, index, true))) {
         if (!this.isExtensible() && this.length <= index) {
            return;
         }

         if (index < this.dense.length) {
            this.dense[index] = value;
            if (this.length <= index) {
               this.length = index + 1L;
               this.modCount++;
            }

            return;
         }

         if (this.denseOnly && index < this.dense.length * 1.5 && this.ensureCapacity(index + 1)) {
            this.dense[index] = value;
            this.length = index + 1L;
            this.modCount++;
            return;
         }

         this.denseOnly = false;
      }

      super.put(cx, index, start, value);
      if (start == this && (this.lengthAttr & 1) == 0 && this.length <= index) {
         this.length = index + 1L;
         this.modCount++;
      }
   }

   @Override
   public void delete(Context cx, int index) {
      if (this.dense == null || 0 > index || index >= this.dense.length || this.isSealed(cx) || !this.denseOnly && this.isGetterOrSetter(null, index, true)) {
         super.delete(cx, index);
      } else {
         this.dense[index] = NOT_FOUND;
      }
   }

   @Override
   public Object[] getIds(Context cx, boolean nonEnumerable, boolean getSymbols) {
      Object[] superIds = super.getIds(cx, nonEnumerable, getSymbols);
      if (this.dense == null) {
         return superIds;
      } else {
         int N = this.dense.length;
         long currentLength = this.length;
         if (N > currentLength) {
            N = (int)currentLength;
         }

         if (N == 0) {
            return superIds;
         } else {
            int superLength = superIds.length;
            Object[] ids = new Object[N + superLength];
            int presentCount = 0;

            for (int i = 0; i != N; i++) {
               if (this.dense[i] != NOT_FOUND) {
                  ids[presentCount] = i;
                  presentCount++;
               }
            }

            if (presentCount != N) {
               Object[] tmp = new Object[presentCount + superLength];
               System.arraycopy(ids, 0, tmp, 0, presentCount);
               ids = tmp;
            }

            System.arraycopy(superIds, 0, ids, presentCount, superLength);
            return ids;
         }
      }
   }

   public List<Integer> getIndexIds(Context cx) {
      Object[] ids = this.getIds(cx);
      List<Integer> indices = new ArrayList<>(ids.length);

      for (Object id : ids) {
         int int32Id = ScriptRuntime.toInt32(cx, id);
         if (int32Id >= 0 && ScriptRuntime.toString(cx, int32Id).equals(ScriptRuntime.toString(cx, id))) {
            indices.add(int32Id);
         }
      }

      return indices;
   }

   private ScriptableObject defaultIndexPropertyDescriptor(Object value, Context cx) {
      Scriptable scope = this.getParentScope();
      if (scope == null) {
         scope = this;
      }

      ScriptableObject desc = new NativeObject(cx.factory);
      ScriptRuntime.setBuiltinProtoAndParent(cx, scope, desc, TopLevel.Builtins.Object);
      desc.defineProperty(cx, "value", value, 0);
      desc.defineProperty(cx, "writable", Boolean.TRUE, 0);
      desc.defineProperty(cx, "enumerable", Boolean.TRUE, 0);
      desc.defineProperty(cx, "configurable", Boolean.TRUE, 0);
      return desc;
   }

   @Override
   public int getAttributes(Context cx, int index) {
      return this.dense != null && index >= 0 && index < this.dense.length && this.dense[index] != NOT_FOUND ? 0 : super.getAttributes(cx, index);
   }

   @Override
   protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
      if (this.dense != null) {
         int index = toDenseIndex(cx, id);
         if (0 <= index && index < this.dense.length && this.dense[index] != NOT_FOUND) {
            Object value = this.dense[index];
            return this.defaultIndexPropertyDescriptor(value, cx);
         }
      }

      return super.getOwnPropertyDescriptor(cx, id);
   }

   @Override
   protected void defineOwnProperty(Context cx, Object id, ScriptableObject desc, boolean checkValid) {
      long index = toArrayIndex(cx, id);
      if (index >= this.length) {
         this.length = index + 1L;
         this.modCount++;
      }

      if (index != -1L && this.dense != null) {
         Object[] values = this.dense;
         this.dense = null;
         this.denseOnly = false;

         for (int i = 0; i < values.length; i++) {
            if (values[i] != NOT_FOUND) {
               if (!this.isExtensible()) {
                  this.setAttributes(cx, i, 0);
               }

               this.put(cx, i, this, values[i]);
            }
         }
      }

      super.defineOwnProperty(cx, id, desc, checkValid);
      if ("length".equals(id)) {
         this.lengthAttr = this.getAttributes(cx, "length");
      }
   }

   static Scriptable jsConstructor(Context cx, Scriptable scope, Object[] args) {
      if (args.length == 0) {
         return new NativeArray(cx, 0L);
      } else {
         Object arg0 = args[0];
         NativeArray res;
         if (args.length <= 1 && arg0 instanceof Number) {
            long len = ScriptRuntime.toUint32(cx, arg0);
            if (len != ((Number)arg0).doubleValue()) {
               String msg = ScriptRuntime.getMessage0("msg.arraylength.bad");
               throw ScriptRuntime.rangeError(cx, msg);
            }

            res = new NativeArray(cx, len);
         } else {
            res = new NativeArray(cx, args);
         }

         return res;
      }
   }

   private void createLengthProp() {
      ScriptableObject.defineBuiltInProperty(
         this, "length", 6, NativeArray::lengthGetter, NativeArray::lengthSetter, NativeArray::lengthAttrSetter, NativeArray::arraySetLength
      );
   }

   private static Object lengthGetter(NativeArray array, Scriptable start, Context cx) {
      return ScriptRuntime.wrapNumber(array.length);
   }

   private static boolean lengthSetter(NativeArray builtIn, Object value, Scriptable owner, Scriptable start, boolean isThrow, Context cx) {
      builtIn.setLength(cx, value);
      return true;
   }

   private static void lengthAttrSetter(NativeArray builtIn, int attrs) {
      builtIn.lengthAttr = attrs;
   }

   protected static void arraySetLength(
      NativeArray builtIn, BuiltInSlot<NativeArray> current, Object id, ScriptableObject desc, boolean checkValid, Object key, int index, Context cx
   ) {
      Object value = getProperty(desc, "value", cx);
      if (value == NOT_FOUND) {
         builtIn.defineOrdinaryProperty(cx, id, desc, checkValid, key, index);
      } else {
         long newLength = checkLength(cx, value);
         Object writable = getProperty(desc, "writable", cx);
         if (newLength >= builtIn.length) {
            builtIn.defineOrdinaryProperty(cx, id, desc, checkValid, key, index);
         } else {
            boolean currentWritable = (current.getAttributes() & 1) == 0;
            if (!currentWritable) {
               throw ScriptRuntime.typeError1(cx, "msg.change.value.with.writable.false", id);
            } else {
               boolean newWritable = true;
               if (writable != NOT_FOUND) {
                  newWritable = isTrue(writable, cx);
                  putProperty(desc, "writable", true, cx);
               }

               builtIn.defineOrdinaryProperty(cx, id, desc, checkValid, key, index);
               int currentAttrs = current.getAttributes();
               int newAttrs = newWritable ? currentAttrs & -2 : currentAttrs | 1;
               current.setAttributes(newAttrs);
            }
         }
      }
   }

   private static Scriptable callConstructorOrCreateArray(Context cx, Scriptable scope, Scriptable arg, long length, boolean lengthAlways) {
      Scriptable result = null;
      if (arg instanceof Constructable) {
         try {
            Object[] args = !lengthAlways && length <= 0L ? ScriptRuntime.EMPTY_OBJECTS : new Object[]{length};
            result = ((Constructable)arg).construct(cx, scope, args);
         } catch (EcmaError var8) {
            if (!"TypeError".equals(var8.getName())) {
               throw var8;
            }
         }
      }

      if (result == null) {
         result = cx.newArray(scope, length > 2147483647L ? 0 : (int)length);
      }

      return result;
   }

   private static Object js_from(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable items = ScriptRuntime.toObject(cx, scope, args.length >= 1 ? args[0] : Undefined.INSTANCE);
      Object mapArg = args.length >= 2 ? args[1] : Undefined.INSTANCE;
      Scriptable thisArg = Undefined.SCRIPTABLE_INSTANCE;
      boolean mapping = !Undefined.isUndefined(mapArg);
      Function mapFn = null;
      if (mapping) {
         if (!(mapArg instanceof Function)) {
            throw ScriptRuntime.typeError0(cx, "msg.map.function.not");
         }

         mapFn = (Function)mapArg;
         if (args.length >= 3) {
            thisArg = ensureScriptable(args[2], cx);
         }
      }

      Object iteratorProp = ScriptableObject.getProperty(items, SymbolKey.ITERATOR, cx);
      if (!(items instanceof NativeArray) && iteratorProp != Scriptable.NOT_FOUND && !Undefined.isUndefined(iteratorProp)) {
         Object iterator = ScriptRuntime.callIterator(items, cx, scope);
         if (!Undefined.isUndefined(iterator)) {
            Scriptable result = callConstructorOrCreateArray(cx, scope, thisObj, 0L, false);
            long k = 0L;

            try (IteratorLikeIterable it = new IteratorLikeIterable(cx, scope, iterator)) {
               for (Object temp : it) {
                  if (mapping) {
                     temp = mapFn.call(cx, scope, thisArg, new Object[]{temp, k});
                  }

                  ArrayLikeAbstractOperations.defineElem(cx, result, k, temp);
                  k++;
               }
            }

            setLengthProperty(cx, result, k);
            return result;
         }
      }

      long length = getLengthProperty(cx, items);
      Scriptable result = callConstructorOrCreateArray(cx, scope, thisObj, length, true);

      for (long k = 0L; k < length; k++) {
         Object temp = getElem(cx, items, k);
         if (mapping) {
            temp = mapFn.call(cx, scope, thisArg, new Object[]{temp, k});
         }

         ArrayLikeAbstractOperations.defineElem(cx, result, k, temp);
      }

      setLengthProperty(cx, result, length);
      return result;
   }

   private static Object js_of(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable result = callConstructorOrCreateArray(cx, scope, thisObj, args.length, true);
      if (result instanceof ScriptableObject) {
         ScriptableObject desc = ScriptableObject.buildDataDescriptor(result, null, 0, cx);

         for (int i = 0; i < args.length; i++) {
            desc.put(cx, "value", desc, args[i]);
            ((ScriptableObject)result).defineOwnProperty(cx, i, desc);
         }
      } else {
         for (int i = 0; i < args.length; i++) {
            ArrayLikeAbstractOperations.defineElem(cx, result, i, args[i]);
         }
      }

      setLengthProperty(cx, result, args.length);
      return result;
   }

   public long getLength() {
      return this.length;
   }

   @Deprecated
   public long jsGet_length() {
      return this.getLength();
   }

   void setDenseOnly(boolean denseOnly) {
      if (denseOnly && !this.denseOnly) {
         throw new IllegalArgumentException();
      } else {
         this.denseOnly = denseOnly;
      }
   }

   boolean getDenseOnly() {
      return this.denseOnly;
   }

   private void setLength(Context cx, Object val) {
      if ((this.lengthAttr & 1) == 0) {
         double d = ScriptRuntime.toNumber(cx, val);
         long longVal = ScriptRuntime.toUint32(d);
         if (longVal != d) {
            String msg = ScriptRuntime.getMessage0("msg.arraylength.bad");
            throw ScriptRuntime.rangeError(cx, msg);
         } else {
            if (this.denseOnly) {
               if (longVal < this.length) {
                  Arrays.fill(this.dense, (int)longVal, this.dense.length, NOT_FOUND);
                  this.length = longVal;
                  this.modCount++;
                  return;
               }

               if (longVal < 1431655764L && longVal < this.length * 1.5 && this.ensureCapacity((int)longVal)) {
                  this.length = longVal;
                  this.modCount++;
                  return;
               }

               this.denseOnly = false;
            }

            if (longVal < this.length) {
               if (this.length - longVal > 4096L) {
                  Object[] e = this.getIds(cx);

                  for (Object id : e) {
                     if (id instanceof String strId) {
                        long index = toArrayIndex(cx, strId);
                        if (index >= longVal) {
                           this.delete(cx, strId);
                        }
                     } else {
                        int index = (Integer)id;
                        if (index >= longVal) {
                           this.delete(cx, index);
                        }
                     }
                  }
               } else {
                  for (long i = longVal; i < this.length; i++) {
                     deleteElem(this, i, cx);
                  }
               }
            }

            this.length = longVal;
            this.modCount++;
         }
      }
   }

   private static long checkLength(Context cx, Object val) {
      double d = ScriptRuntime.toNumber(cx, val);
      long longVal = ScriptRuntime.toUint32(cx, val);
      if (longVal != d) {
         String msg = ScriptRuntime.getMessage0("msg.arraylength.bad");
         throw ScriptRuntime.rangeError(cx, msg);
      } else {
         return longVal;
      }
   }

   static long getLengthProperty(Context cx, Scriptable obj) {
      if (obj instanceof NativeString) {
         return ((NativeString)obj).getLength();
      } else if (obj instanceof NativeArray) {
         return ((NativeArray)obj).getLength();
      } else {
         Object len = ScriptableObject.getProperty(obj, "length", cx);
         if (len == Scriptable.NOT_FOUND) {
            return 0L;
         } else {
            double doubleLen = ScriptRuntime.toNumber(cx, len);
            if (doubleLen > 9.007199254740991E15) {
               return 9007199254740991L;
            } else {
               return doubleLen < 0.0 ? 0L : (long)doubleLen;
            }
         }
      }
   }

   private static Object setLengthProperty(Context cx, Scriptable target, long length) {
      Object len = ScriptRuntime.wrapNumber(length);
      ScriptableObject.putProperty(target, "length", len, cx);
      return len;
   }

   private static void deleteElem(Scriptable target, long index, Context cx) {
      int i = (int)index;
      if (i == index) {
         target.delete(cx, i);
      } else {
         target.delete(cx, Long.toString(index));
      }
   }

   private static Object getElem(Context cx, Scriptable target, long index) {
      Object elem = ArrayLikeAbstractOperations.getRawElem(target, index, cx);
      return elem != Scriptable.NOT_FOUND ? elem : Undefined.INSTANCE;
   }

   private static void defineElemOrThrow(Context cx, Scriptable target, long index, Object value) {
      if (index > 9.007199254740991E15) {
         throw ScriptRuntime.typeError1(cx, "msg.arraylength.too.big", String.valueOf(index));
      } else {
         ArrayLikeAbstractOperations.defineElem(cx, target, index, value);
      }
   }

   private static void setElem(Context cx, Scriptable target, long index, Object value) {
      if (index > 2147483647L) {
         String id = Long.toString(index);
         ScriptableObject.putProperty(target, id, value, cx);
      } else {
         ScriptableObject.putProperty(target, (int)index, value, cx);
      }
   }

   private static void setRawElem(Context cx, Scriptable target, long index, Object value) {
      if (value == NOT_FOUND) {
         deleteElem(target, index, cx);
      } else {
         setElem(cx, target, index, value);
      }
   }

   private static String js_toString(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return toStringHelper(cx, scope, thisObj, false);
   }

   private static String js_toLocaleString(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return toStringHelper(cx, scope, thisObj, true);
   }

   private static String js_toSource(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return toSource(cx, scope, thisObj);
   }

   private static String toStringHelper(Context cx, Scriptable scope, Scriptable thisObj, boolean toLocale) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      int length = (int)getLengthProperty(cx, o);
      if (length == 0) {
         return "[]";
      } else {
         StringBuilder result = new StringBuilder(256);
         result.append('[');

         for (int i = 0; i < length; i++) {
            if (i > 0) {
               result.append(", ");
            }

            Object elem = ArrayLikeAbstractOperations.getRawElem(o, i, cx);
            if (elem != NOT_FOUND && elem != null && elem != Undefined.INSTANCE) {
               result.append(ScriptRuntime.uneval(cx, scope, elem));
            }
         }

         result.append(']');
         return result.toString();
      }
   }

   private static String toSource(Context cx, Scriptable scope, Scriptable thisObj) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      int length = (int)getLengthProperty(cx, o);
      if (length == 0) {
         return "[]";
      } else {
         StringBuilder result = new StringBuilder(256);
         result.append('[');

         for (int i = 0; i < length; i++) {
            if (i > 0) {
               result.append(", ");
            }

            Object elem = ArrayLikeAbstractOperations.getRawElem(o, i, cx);
            if (elem != NOT_FOUND && elem != null && elem != Undefined.INSTANCE) {
               result.append(ScriptRuntime.uneval(cx, scope, elem));
            }
         }

         result.append(']');
         return result.toString();
      }
   }

   private static String js_join(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      long llength = getLengthProperty(cx, o);
      int length = (int)llength;
      if (llength != length) {
         throw Context.reportRuntimeError1("msg.arraylength.too.big", String.valueOf(llength), cx);
      } else {
         String separator = args.length >= 1 && args[0] != Undefined.INSTANCE ? ScriptRuntime.toString(cx, args[0]) : ",";
         if (o instanceof NativeArray na && na.denseOnly) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < length; i++) {
               if (i != 0) {
                  sb.append(separator);
               }

               if (i < na.dense.length) {
                  Object temp = na.dense[i];
                  if (temp != null && temp != Undefined.INSTANCE && temp != Scriptable.NOT_FOUND) {
                     sb.append(ScriptRuntime.toString(cx, temp));
                  }
               }
            }

            return sb.toString();
         } else if (length == 0) {
            return "";
         } else {
            String[] buf = new String[length];
            int total_size = 0;

            for (int i = 0; i != length; i++) {
               Object temp = getElem(cx, o, i);
               if (temp != null && temp != Undefined.INSTANCE) {
                  String str = ScriptRuntime.toString(cx, temp);
                  total_size += str.length();
                  buf[i] = str;
               }
            }

            total_size += (length - 1) * separator.length();
            StringBuilder sb = new StringBuilder(total_size);

            for (int ix = 0; ix != length; ix++) {
               if (ix != 0) {
                  sb.append(separator);
               }

               String str = buf[ix];
               if (str != null) {
                  sb.append(str);
               }
            }

            return sb.toString();
         }
      }
   }

   private static Scriptable js_reverse(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      if (o instanceof NativeArray na && na.denseOnly) {
         int i = 0;

         for (int j = (int)na.length - 1; i < j; j--) {
            Object temp = na.dense[i];
            na.dense[i] = na.dense[j];
            na.dense[j] = temp;
            i++;
         }

         return o;
      } else {
         long len = getLengthProperty(cx, o);
         long half = len / 2L;

         for (long i = 0L; i < half; i++) {
            long j = len - i - 1L;
            Object temp1 = ArrayLikeAbstractOperations.getRawElem(o, i, cx);
            Object temp2 = ArrayLikeAbstractOperations.getRawElem(o, j, cx);
            setRawElem(cx, o, i, temp2);
            setRawElem(cx, o, j, temp1);
         }

         return o;
      }
   }

   private static Scriptable js_sort(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      Comparator<Object> comparator = ArrayLikeAbstractOperations.getSortComparator(cx, scope, args);
      return sort(cx, o, comparator);
   }

   private static Scriptable sort(Context cx, Scriptable o, Comparator<Object> comparator) {
      long llength = getLengthProperty(cx, o);
      int length = (int)llength;
      if (llength != length) {
         throw Context.reportRuntimeError1("msg.arraylength.too.big", String.valueOf(llength), cx);
      } else {
         Object[] working = new Object[length];

         for (int i = 0; i != length; i++) {
            working[i] = ArrayLikeAbstractOperations.getRawElem(o, i, cx);
         }

         try {
            Arrays.sort(working, comparator);
         } catch (IllegalArgumentException var8) {
            return o;
         }

         for (int i = 0; i < length; i++) {
            setRawElem(cx, o, i, working[i]);
         }

         return o;
      }
   }

   private static Object js_push(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      if (o instanceof NativeArray na && na.denseOnly && na.ensureCapacity((int)na.length + args.length)) {
         for (Object arg : args) {
            na.dense[(int)(na.length++)] = arg;
            na.modCount++;
         }

         return ScriptRuntime.wrapNumber(na.length);
      } else {
         long length = getLengthProperty(cx, o);

         for (int i = 0; i < args.length; i++) {
            setElem(cx, o, length + i, args[i]);
         }

         length += args.length;
         return setLengthProperty(cx, o, length);
      }
   }

   private static Object js_pop(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      if (o instanceof NativeArray na && na.denseOnly && na.length > 0L) {
         na.length--;
         na.modCount++;
         Object result = na.dense[(int)na.length];
         na.dense[(int)na.length] = NOT_FOUND;
         return result;
      } else {
         long length = getLengthProperty(cx, o);
         Object result;
         if (length > 0L) {
            result = getElem(cx, o, --length);
            deleteElem(o, length, cx);
         } else {
            result = Undefined.INSTANCE;
         }

         setLengthProperty(cx, o, length);
         return result;
      }
   }

   private static Object js_shift(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      if (o instanceof NativeArray na && na.denseOnly && na.length > 0L) {
         na.length--;
         na.modCount++;
         Object result = na.dense[0];
         System.arraycopy(na.dense, 1, na.dense, 0, (int)na.length);
         na.dense[(int)na.length] = NOT_FOUND;
         return result == NOT_FOUND ? Undefined.INSTANCE : result;
      } else {
         long length = getLengthProperty(cx, o);
         Object result;
         if (length > 0L) {
            long i = 0L;
            length--;
            result = getElem(cx, o, i);
            if (length > 0L) {
               for (long var13 = 1L; var13 <= length; var13++) {
                  Object temp = ArrayLikeAbstractOperations.getRawElem(o, var13, cx);
                  setRawElem(cx, o, var13 - 1L, temp);
               }
            }

            deleteElem(o, length, cx);
         } else {
            result = Undefined.INSTANCE;
         }

         setLengthProperty(cx, o, length);
         return result;
      }
   }

   private static Object js_unshift(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      if (o instanceof NativeArray na && na.denseOnly && na.ensureCapacity((int)na.length + args.length)) {
         System.arraycopy(na.dense, 0, na.dense, args.length, (int)na.length);
         System.arraycopy(args, 0, na.dense, 0, args.length);
         na.length += args.length;
         na.modCount++;
         return ScriptRuntime.wrapNumber(na.length);
      } else {
         long length = getLengthProperty(cx, o);
         int argc = args.length;
         if (argc > 0) {
            if (length + argc > 9.007199254740991E15) {
               throw ScriptRuntime.typeError1(cx, "msg.arraylength.too.big", length + argc);
            }

            if (length > 0L) {
               for (long last = length - 1L; last >= 0L; last--) {
                  Object temp = ArrayLikeAbstractOperations.getRawElem(o, last, cx);
                  setRawElem(cx, o, last + argc, temp);
               }
            }

            for (int i = 0; i < args.length; i++) {
               setElem(cx, o, i, args[i]);
            }
         }

         length += argc;
         return setLengthProperty(cx, o, length);
      }
   }

   private static Object js_splice(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      NativeArray na = null;
      Object result = ArrayLikeAbstractOperations.arraySpeciesCreate(cx, scope, o, 0);
      NativeArray nar = null;
      boolean denseFrom = false;
      boolean denseRes = false;
      if (o instanceof NativeArray) {
         na = (NativeArray)o;
         denseFrom = na.denseOnly;
      }

      if (result instanceof NativeArray) {
         nar = (NativeArray)result;
         denseRes = nar.denseOnly;
      }

      scope = getTopLevelScope(scope);
      int argc = args.length;
      if (argc == 0) {
         return cx.newArray(scope, 0);
      } else {
         long length = getLengthProperty(cx, o);
         long begin = ArrayLikeAbstractOperations.toSliceIndex(ScriptRuntime.toInteger(cx, args[0]), length);
         argc--;
         long actualDeleteCount;
         if (args.length == 1) {
            actualDeleteCount = length - begin;
         } else {
            double dcount = ScriptRuntime.toInteger(cx, args[1]);
            if (dcount < 0.0) {
               actualDeleteCount = 0L;
            } else if (dcount > length - begin) {
               actualDeleteCount = length - begin;
            } else {
               actualDeleteCount = (long)dcount;
            }

            argc--;
         }

         long end = begin + actualDeleteCount;
         long delta = argc - actualDeleteCount;
         if (length + delta > 9.007199254740991E15) {
            throw ScriptRuntime.typeError1(cx, "msg.arraylength.too.big", length + delta);
         } else if (actualDeleteCount > 2147483647L) {
            String msg = ScriptRuntime.getMessage0("msg.arraylength.bad");
            throw ScriptRuntime.rangeError(cx, msg);
         } else {
            if (actualDeleteCount != 0L) {
               if (denseFrom && denseRes) {
                  int intLen = (int)(end - begin);
                  Object[] copy = new Object[intLen];
                  System.arraycopy(na.dense, (int)begin, copy, 0, intLen);
                  nar.dense = copy;
                  nar.setLength(cx, intLen);
               } else {
                  for (long last = begin; last != end; last++) {
                     Object temp = ArrayLikeAbstractOperations.getRawElem(o, last, cx);
                     if (temp != NOT_FOUND) {
                        ArrayLikeAbstractOperations.defineElem(cx, (ScriptableObject)result, last - begin, temp);
                     }
                  }

                  setLengthProperty(cx, (ScriptableObject)result, end - begin);
               }
            }

            if (denseFrom && length + delta < 2147483647L && na.ensureCapacity((int)(length + delta))) {
               System.arraycopy(na.dense, (int)end, na.dense, (int)(begin + argc), (int)(length - end));
               if (argc > 0) {
                  System.arraycopy(args, 2, na.dense, (int)begin, argc);
               }

               if (delta < 0L) {
                  Arrays.fill(na.dense, (int)(length + delta), (int)length, NOT_FOUND);
               }

               na.length = length + delta;
               na.modCount++;
               return result;
            } else {
               if (delta > 0L) {
                  for (long lastx = length - 1L; lastx >= end; lastx--) {
                     Object temp = ArrayLikeAbstractOperations.getRawElem(o, lastx, cx);
                     setRawElem(cx, o, lastx + delta, temp);
                  }
               } else if (delta < 0L) {
                  for (long lastx = end; lastx < length; lastx++) {
                     Object temp = ArrayLikeAbstractOperations.getRawElem(o, lastx, cx);
                     setRawElem(cx, o, lastx + delta, temp);
                  }

                  for (long k = length - 1L; k >= length + delta; k--) {
                     deleteElem(o, k, cx);
                  }
               }

               int argoffset = args.length - argc;

               for (int i = 0; i < argc; i++) {
                  setElem(cx, o, begin + i, args[i + argoffset]);
               }

               setLengthProperty(cx, o, length + delta);
               return result;
            }
         }
      }
   }

   private static boolean isConcatSpreadable(Context cx, Scriptable scope, Object val) {
      if (val instanceof Scriptable) {
         Object spreadable = ScriptableObject.getProperty((Scriptable)val, SymbolKey.IS_CONCAT_SPREADABLE, cx);
         if (spreadable != Scriptable.NOT_FOUND && !Undefined.isUndefined(spreadable)) {
            return ScriptRuntime.toBoolean(cx, spreadable);
         }
      }

      return js_isArray(val);
   }

   private static long concatSpreadArg(Context cx, Scriptable result, Scriptable arg, long offset) {
      long srclen = getLengthProperty(cx, arg);
      long newlen = srclen + offset;
      if (newlen > 9.007199254740991E15) {
         throw ScriptRuntime.typeError1(cx, "msg.arraylength.too.big", newlen);
      } else if (newlen <= 2147483647L
         && result instanceof NativeArray denseResult
         && denseResult.denseOnly
         && arg instanceof NativeArray denseArg
         && denseArg.denseOnly) {
         denseResult.ensureCapacity((int)newlen);
         System.arraycopy(denseArg.dense, 0, denseResult.dense, (int)offset, (int)srclen);
         return newlen;
      } else {
         long dstpos = offset;

         for (long srcpos = 0L; srcpos < srclen; dstpos++) {
            Object temp = ArrayLikeAbstractOperations.getRawElem(arg, srcpos, cx);
            if (temp != Scriptable.NOT_FOUND) {
               ArrayLikeAbstractOperations.defineElem(cx, result, dstpos, temp);
            }

            srcpos++;
         }

         return newlen;
      }
   }

   private static long doConcat(Context cx, Scriptable scope, Scriptable result, Object arg, long offset) {
      if (isConcatSpreadable(cx, scope, arg)) {
         return concatSpreadArg(cx, result, (Scriptable)arg, offset);
      } else {
         ArrayLikeAbstractOperations.defineElem(cx, result, offset, arg);
         return offset + 1L;
      }
   }

   private static Scriptable js_concat(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      scope = getTopLevelScope(scope);
      Scriptable result = ArrayLikeAbstractOperations.arraySpeciesCreate(cx, scope, o, 0);
      long length = doConcat(cx, scope, result, o, 0L);

      for (Object arg : args) {
         length = doConcat(cx, scope, result, arg, length);
      }

      setLengthProperty(cx, result, length);
      return result;
   }

   private static Scriptable js_slice(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      long len = getLengthProperty(cx, o);
      long begin;
      long end;
      if (args.length == 0) {
         begin = 0L;
         end = len;
      } else {
         begin = ArrayLikeAbstractOperations.toSliceIndex(ScriptRuntime.toInteger(cx, args[0]), len);
         if (args.length != 1 && args[1] != Undefined.INSTANCE) {
            end = ArrayLikeAbstractOperations.toSliceIndex(ScriptRuntime.toInteger(cx, args[1]), len);
         } else {
            end = len;
         }
      }

      if (end - begin > 2147483647L) {
         String msg = ScriptRuntime.getMessage0("msg.arraylength.bad");
         throw ScriptRuntime.rangeError(cx, msg);
      } else {
         Scriptable result = ArrayLikeAbstractOperations.arraySpeciesCreate(cx, scope, o, 0);

         for (long slot = begin; slot < end; slot++) {
            Object temp = ArrayLikeAbstractOperations.getRawElem(o, slot, cx);
            if (temp != NOT_FOUND) {
               ArrayLikeAbstractOperations.defineElem(cx, result, slot - begin, temp);
            }
         }

         setLengthProperty(cx, result, Math.max(0L, end - begin));
         return result;
      }
   }

   private static Object js_indexOf(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Object compareTo = args.length > 0 ? args[0] : Undefined.INSTANCE;
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      long length = getLengthProperty(cx, o);
      long start;
      if (args.length < 2) {
         start = 0L;
      } else {
         start = (long)ScriptRuntime.toInteger(cx, args[1]);
         if (start < 0L) {
            start += length;
            if (start < 0L) {
               start = 0L;
            }
         }

         if (start > length - 1L) {
            return NEGATIVE_ONE;
         }
      }

      if (o instanceof NativeArray na && na.denseOnly) {
         Scriptable proto = na.getPrototype(cx);

         for (int i = (int)start; i < length; i++) {
            Object val = na.dense[i];
            if (val == NOT_FOUND && proto != null) {
               val = ScriptableObject.getProperty(proto, i, cx);
            }

            if (val != NOT_FOUND && ScriptRuntime.shallowEq(cx, val, compareTo)) {
               return (long)i;
            }
         }

         return NEGATIVE_ONE;
      } else {
         for (long i = start; i < length; i++) {
            Object valx = ArrayLikeAbstractOperations.getRawElem(o, i, cx);
            if (valx != NOT_FOUND && ScriptRuntime.shallowEq(cx, valx, compareTo)) {
               return i;
            }
         }

         return NEGATIVE_ONE;
      }
   }

   private static Object js_lastIndexOf(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Object compareTo = args.length > 0 ? args[0] : Undefined.INSTANCE;
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      long length = getLengthProperty(cx, o);
      long start;
      if (args.length < 2) {
         start = length - 1L;
      } else {
         start = (long)ScriptRuntime.toInteger(cx, args[1]);
         if (start >= length) {
            start = length - 1L;
         } else if (start < 0L) {
            start += length;
         }

         if (start < 0L) {
            return NEGATIVE_ONE;
         }
      }

      if (o instanceof NativeArray na && na.denseOnly) {
         Scriptable proto = na.getPrototype(cx);

         for (int i = (int)start; i >= 0; i--) {
            Object val = na.dense[i];
            if (val == NOT_FOUND && proto != null) {
               val = ScriptableObject.getProperty(proto, i, cx);
            }

            if (val != NOT_FOUND && ScriptRuntime.shallowEq(cx, val, compareTo)) {
               return (long)i;
            }
         }

         return NEGATIVE_ONE;
      } else {
         for (long i = start; i >= 0L; i--) {
            Object valx = ArrayLikeAbstractOperations.getRawElem(o, i, cx);
            if (valx != NOT_FOUND && ScriptRuntime.shallowEq(cx, valx, compareTo)) {
               return i;
            }
         }

         return NEGATIVE_ONE;
      }
   }

   private static Boolean js_includes(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      long len = getLengthProperty(cx, o);
      if (len == 0L) {
         return Boolean.FALSE;
      } else {
         long k;
         if (args.length < 2) {
            k = 0L;
         } else {
            k = (long)ScriptRuntime.toInteger(cx, args[1]);
            if (k < 0L) {
               k += len;
               if (k < 0L) {
                  k = 0L;
               }
            }

            if (k > len - 1L) {
               return Boolean.FALSE;
            }
         }

         Object compareTo = args.length > 0 ? args[0] : Undefined.INSTANCE;
         if (o instanceof NativeArray na && na.denseOnly) {
            Scriptable proto = na.getPrototype(cx);

            for (int i = (int)k; i < len; i++) {
               Object elementK = na.dense[i];
               if (elementK == NOT_FOUND && proto != null) {
                  elementK = ScriptableObject.getProperty(proto, i, cx);
               }

               if (elementK == NOT_FOUND) {
                  elementK = Undefined.INSTANCE;
               }

               if (ScriptRuntime.sameZero(cx, elementK, compareTo)) {
                  return Boolean.TRUE;
               }
            }

            return Boolean.FALSE;
         } else {
            while (k < len) {
               Object elementKx = ArrayLikeAbstractOperations.getRawElem(o, k, cx);
               if (elementKx == NOT_FOUND) {
                  elementKx = Undefined.INSTANCE;
               }

               if (ScriptRuntime.sameZero(cx, elementKx, compareTo)) {
                  return Boolean.TRUE;
               }

               k++;
            }

            return Boolean.FALSE;
         }
      }
   }

   private static Object js_fill(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      long len = getLengthProperty(cx, o);
      long relativeStart = 0L;
      if (args.length >= 2) {
         relativeStart = (long)ScriptRuntime.toInteger(cx, args[1]);
      }

      long k;
      if (relativeStart < 0L) {
         k = Math.max(len + relativeStart, 0L);
      } else {
         k = Math.min(relativeStart, len);
      }

      long relativeEnd = len;
      if (args.length >= 3 && !Undefined.isUndefined(args[2])) {
         relativeEnd = (long)ScriptRuntime.toInteger(cx, args[2]);
      }

      long fin;
      if (relativeEnd < 0L) {
         fin = Math.max(len + relativeEnd, 0L);
      } else {
         fin = Math.min(relativeEnd, len);
      }

      Object value = args.length > 0 ? args[0] : Undefined.INSTANCE;

      for (long i = k; i < fin; i++) {
         setRawElem(cx, thisObj, i, value);
      }

      return thisObj;
   }

   private static Object js_copyWithin(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      long len = getLengthProperty(cx, o);
      Object targetArg = args.length >= 1 ? args[0] : Undefined.INSTANCE;
      long relativeTarget = (long)ScriptRuntime.toInteger(cx, targetArg);
      long to;
      if (relativeTarget < 0L) {
         to = Math.max(len + relativeTarget, 0L);
      } else {
         to = Math.min(relativeTarget, len);
      }

      Object startArg = args.length >= 2 ? args[1] : Undefined.INSTANCE;
      long relativeStart = (long)ScriptRuntime.toInteger(cx, startArg);
      long from;
      if (relativeStart < 0L) {
         from = Math.max(len + relativeStart, 0L);
      } else {
         from = Math.min(relativeStart, len);
      }

      long relativeEnd = len;
      if (args.length >= 3 && !Undefined.isUndefined(args[2])) {
         relativeEnd = (long)ScriptRuntime.toInteger(cx, args[2]);
      }

      long fin;
      if (relativeEnd < 0L) {
         fin = Math.max(len + relativeEnd, 0L);
      } else {
         fin = Math.min(relativeEnd, len);
      }

      long count = Math.min(fin - from, len - to);
      int direction = 1;
      if (from < to && to < from + count) {
         direction = -1;
         from = from + count - 1L;
         to = to + count - 1L;
      }

      if (o instanceof NativeArray && count <= 2147483647L) {
         NativeArray na = (NativeArray)o;
         if (na.denseOnly) {
            while (count > 0L) {
               na.dense[(int)to] = na.dense[(int)from];
               from += direction;
               to += direction;
               count--;
            }

            return thisObj;
         }
      }

      while (count > 0L) {
         Object temp = ArrayLikeAbstractOperations.getRawElem(o, from, cx);
         if (temp != Scriptable.NOT_FOUND && !Undefined.isUndefined(temp)) {
            setElem(cx, o, to, temp);
         } else {
            deleteElem(o, to, cx);
         }

         from += direction;
         to += direction;
         count--;
      }

      return thisObj;
   }

   private static Object js_at(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      long len = getLengthProperty(cx, o);
      long relativeIndex = 0L;
      if (args.length >= 1) {
         relativeIndex = (long)ScriptRuntime.toInteger(cx, args[0]);
      }

      long k = relativeIndex >= 0L ? relativeIndex : len + relativeIndex;
      return k >= 0L && k < len ? getElem(cx, thisObj, k) : Undefined.INSTANCE;
   }

   private static Object js_flat(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      double depth;
      if (args.length >= 1 && !Undefined.isUndefined(args[0])) {
         depth = ScriptRuntime.toInteger(cx, args[0]);
      } else {
         depth = 1.0;
      }

      return flat(cx, scope, o, depth);
   }

   private static Scriptable flat(Context cx, Scriptable scope, Scriptable source, double depth) {
      long length = getLengthProperty(cx, source);
      Scriptable result = ArrayLikeAbstractOperations.arraySpeciesCreate(cx, scope, source, 0);
      long j = 0L;

      for (long i = 0L; i < length; i++) {
         Object elem = ArrayLikeAbstractOperations.getRawElem(source, i, cx);
         if (elem != Scriptable.NOT_FOUND) {
            if (depth >= 1.0 && js_isArray(elem)) {
               Scriptable arr = flat(cx, scope, (Scriptable)elem, depth - 1.0);
               long arrLength = getLengthProperty(cx, arr);

               for (long k = 0L; k < arrLength; k++) {
                  Object temp = ArrayLikeAbstractOperations.getRawElem(arr, k, cx);
                  defineElemOrThrow(cx, result, j++, temp);
               }
            } else {
               defineElemOrThrow(cx, result, j++, elem);
            }
         }
      }

      setLengthProperty(cx, result, j);
      return result;
   }

   private static Object js_flatMap(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      Object callbackArg = args.length > 0 ? args[0] : Undefined.INSTANCE;
      Function f = ArrayLikeAbstractOperations.getCallbackArg(cx, callbackArg);
      Scriptable parent = ScriptableObject.getTopLevelScope(f);
      Scriptable thisArg;
      if (args.length >= 2 && args[1] != null && args[1] != Undefined.INSTANCE) {
         thisArg = ScriptRuntime.toObject(cx, scope, args[1]);
      } else {
         thisArg = parent;
      }

      long length = getLengthProperty(cx, o);
      Scriptable result = ArrayLikeAbstractOperations.arraySpeciesCreate(cx, scope, o, 0);
      long j = 0L;

      for (long i = 0L; i < length; i++) {
         Object elem = ArrayLikeAbstractOperations.getRawElem(o, i, cx);
         if (elem != Scriptable.NOT_FOUND) {
            Object[] innerArgs = new Object[]{elem, i, o};
            Object mapCall = f.call(cx, parent, thisArg, innerArgs);
            if (js_isArray(mapCall)) {
               Scriptable arr = (Scriptable)mapCall;
               long arrLength = getLengthProperty(cx, arr);

               for (long k = 0L; k < arrLength; k++) {
                  Object temp = ArrayLikeAbstractOperations.getRawElem(arr, k, cx);
                  defineElemOrThrow(cx, result, j++, temp);
               }
            } else {
               defineElemOrThrow(cx, result, j++, mapCall);
            }
         }
      }

      setLengthProperty(cx, result, j);
      return result;
   }

   private static Object js_every(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ArrayLikeAbstractOperations.iterativeMethod(cx, ARRAY_TAG, "every", ArrayLikeAbstractOperations.IterativeOperation.EVERY, scope, thisObj, args);
   }

   private static Object js_filter(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ArrayLikeAbstractOperations.iterativeMethod(cx, ARRAY_TAG, "filter", ArrayLikeAbstractOperations.IterativeOperation.FILTER, scope, thisObj, args);
   }

   private static Object js_forEach(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ArrayLikeAbstractOperations.iterativeMethod(
         cx, ARRAY_TAG, "forEach", ArrayLikeAbstractOperations.IterativeOperation.FOR_EACH, scope, thisObj, args
      );
   }

   private static Object js_map(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ArrayLikeAbstractOperations.iterativeMethod(cx, ARRAY_TAG, "map", ArrayLikeAbstractOperations.IterativeOperation.MAP, scope, thisObj, args);
   }

   private static Object js_some(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ArrayLikeAbstractOperations.iterativeMethod(cx, ARRAY_TAG, "some", ArrayLikeAbstractOperations.IterativeOperation.SOME, scope, thisObj, args);
   }

   private static Object js_find(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ArrayLikeAbstractOperations.iterativeMethod(cx, ARRAY_TAG, "find", ArrayLikeAbstractOperations.IterativeOperation.FIND, scope, thisObj, args);
   }

   private static Object js_findIndex(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ArrayLikeAbstractOperations.iterativeMethod(
         cx, ARRAY_TAG, "findIndex", ArrayLikeAbstractOperations.IterativeOperation.FIND_INDEX, scope, thisObj, args
      );
   }

   private static Object js_findLast(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ArrayLikeAbstractOperations.iterativeMethod(
         cx, ARRAY_TAG, "findLast", ArrayLikeAbstractOperations.IterativeOperation.FIND_LAST, scope, thisObj, args
      );
   }

   private static Object js_findLastIndex(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ArrayLikeAbstractOperations.iterativeMethod(
         cx, ARRAY_TAG, "findLastIndex", ArrayLikeAbstractOperations.IterativeOperation.FIND_LAST_INDEX, scope, thisObj, args
      );
   }

   private static Object js_reduce(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ArrayLikeAbstractOperations.reduceMethod(cx, ArrayLikeAbstractOperations.ReduceOperation.REDUCE, scope, thisObj, args);
   }

   private static Object js_reduceRight(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return ArrayLikeAbstractOperations.reduceMethod(cx, ArrayLikeAbstractOperations.ReduceOperation.REDUCE_RIGHT, scope, thisObj, args);
   }

   private static Object js_keys(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      thisObj = ScriptRuntime.toObject(cx, scope, thisObj);
      return new NativeArrayIterator(cx, scope, thisObj, NativeArrayIterator.ArrayIteratorType.KEYS);
   }

   private static Object js_entries(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      thisObj = ScriptRuntime.toObject(cx, scope, thisObj);
      return new NativeArrayIterator(cx, scope, thisObj, NativeArrayIterator.ArrayIteratorType.ENTRIES);
   }

   private static Object js_values(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      thisObj = ScriptRuntime.toObject(cx, scope, thisObj);
      return new NativeArrayIterator(cx, scope, thisObj, NativeArrayIterator.ArrayIteratorType.VALUES);
   }

   private static Object js_isArrayMethod(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return args.length > 0 && js_isArray(args[0]);
   }

   private static boolean js_isArray(Object o) {
      return !(o instanceof Scriptable) ? false : "Array".equals(((Scriptable)o).getClassName());
   }

   private static Object js_toSorted(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Comparator<Object> comparator = ArrayLikeAbstractOperations.getSortComparator(cx, scope, args);
      Scriptable source = ScriptRuntime.toObject(cx, scope, thisObj);
      long len = getLengthProperty(cx, source);
      if (len > 2147483647L) {
         String msg = ScriptRuntime.getMessage0("msg.arraylength.bad");
         throw ScriptRuntime.rangeError(cx, msg);
      } else {
         Scriptable result = cx.newArray(scope, (int)len);

         for (int k = 0; k < len; k++) {
            Object fromValue = getElem(cx, source, k);
            setElem(cx, result, k, fromValue);
         }

         sort(cx, result, comparator);
         return result;
      }
   }

   private static Object js_toReversed(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable source = ScriptRuntime.toObject(cx, scope, thisObj);
      long len = getLengthProperty(cx, source);
      if (len > 2147483647L) {
         String msg = ScriptRuntime.getMessage0("msg.arraylength.bad");
         throw ScriptRuntime.rangeError(cx, msg);
      } else {
         Scriptable result = cx.newArray(scope, (int)len);

         for (int k = 0; k < len; k++) {
            int from = (int)len - k - 1;
            Object fromValue = getElem(cx, source, from);
            setElem(cx, result, k, fromValue);
         }

         return result;
      }
   }

   private static Object js_toSpliced(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable source = ScriptRuntime.toObject(cx, scope, thisObj);
      long len = getLengthProperty(cx, source);
      long actualStart = 0L;
      if (args.length > 0) {
         actualStart = ArrayLikeAbstractOperations.toSliceIndex(ScriptRuntime.toInteger(cx, args[0]), len);
      }

      long insertCount = args.length > 2 ? args.length - 2 : 0L;
      long actualSkipCount;
      if (args.length == 0) {
         actualSkipCount = 0L;
      } else if (args.length == 1) {
         actualSkipCount = len - actualStart;
      } else {
         long sc = ScriptRuntime.toLength(cx, args, 1);
         actualSkipCount = Math.max(0L, Math.min(sc, len - actualStart));
      }

      long newLen = len + insertCount - actualSkipCount;
      if (newLen > 9.007199254740991E15) {
         throw ScriptRuntime.typeError1(cx, "msg.arraylength.too.big", newLen);
      } else if (newLen > 2147483647L) {
         String msg = ScriptRuntime.getMessage0("msg.arraylength.bad");
         throw ScriptRuntime.rangeError(cx, msg);
      } else {
         Scriptable result = cx.newArray(scope, (int)newLen);
         long i = 0L;

         long r;
         for (r = actualStart + actualSkipCount; i < actualStart; i++) {
            Object e = getElem(cx, source, i);
            setElem(cx, result, i, e);
         }

         for (int j = 2; j < args.length; j++) {
            setElem(cx, result, i, args[j]);
            i++;
         }

         while (i < newLen) {
            Object e = getElem(cx, source, r);
            setElem(cx, result, i, e);
            i++;
            r++;
         }

         return result;
      }
   }

   private static Object js_with(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable source = ScriptRuntime.toObject(cx, scope, thisObj);
      long len = getLengthProperty(cx, source);
      long relativeIndex = args.length > 0 ? (int)ScriptRuntime.toInteger(cx, args[0]) : 0L;
      long actualIndex = relativeIndex >= 0L ? relativeIndex : len + relativeIndex;
      if (actualIndex >= 0L && actualIndex < len) {
         if (len > 2147483647L) {
            String msg = ScriptRuntime.getMessage0("msg.arraylength.bad");
            throw ScriptRuntime.rangeError(cx, msg);
         } else {
            Scriptable result = cx.newArray(scope, (int)len);

            for (long k = 0L; k < len; k++) {
               Object value;
               if (k == actualIndex) {
                  value = args.length > 1 ? args[1] : Undefined.INSTANCE;
               } else {
                  value = getElem(cx, source, k);
               }

               setElem(cx, result, k, value);
            }

            return result;
         }
      } else {
         throw ScriptRuntime.rangeError(cx, "index out of range");
      }
   }

   @Override
   public boolean contains(Object o) {
      return this.indexOf(o) > -1;
   }

   @Override
   public Object[] toArray() {
      return this.toArray(ScriptRuntime.EMPTY_OBJECTS);
   }

   @Override
   public Object[] toArray(Object[] a) {
      int len = this.size();
      Object[] array = a.length >= len ? a : (Object[])Array.newInstance(a.getClass().getComponentType(), len);

      for (int i = 0; i < len; i++) {
         array[i] = this.get(i);
      }

      return array;
   }

   @Override
   public boolean containsAll(Collection c) {
      for (Object aC : c) {
         if (!this.contains(aC)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public int size() {
      long longLen = this.length;
      if (longLen > 2147483647L) {
         throw new IllegalStateException("list.length (" + this.length + ") exceeds Integer.MAX_VALUE");
      } else {
         return (int)longLen;
      }
   }

   @Override
   public boolean isEmpty() {
      return this.length == 0L;
   }

   public Object get(long index) {
      if (index >= 0L && index < this.length) {
         Object value = ArrayLikeAbstractOperations.getRawElem(this, index, this.localContext);
         if (value == Scriptable.NOT_FOUND || value == Undefined.INSTANCE) {
            return null;
         } else {
            return value instanceof Wrapper ? ((Wrapper)value).unwrap() : value;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public Object get(int index) {
      return this.get((long)index);
   }

   @Override
   public int indexOf(Object o) {
      int len = this.size();
      if (o == null) {
         for (int i = 0; i < len; i++) {
            if (this.get(i) == null) {
               return i;
            }
         }
      } else {
         for (int ix = 0; ix < len; ix++) {
            if (o.equals(this.get(ix))) {
               return ix;
            }
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(Object o) {
      int len = this.size();
      if (o == null) {
         for (int i = len - 1; i >= 0; i--) {
            if (this.get(i) == null) {
               return i;
            }
         }
      } else {
         for (int ix = len - 1; ix >= 0; ix--) {
            if (o.equals(this.get(ix))) {
               return ix;
            }
         }
      }

      return -1;
   }

   @Override
   public Iterator iterator() {
      return this.listIterator(0);
   }

   @Override
   public ListIterator listIterator() {
      return this.listIterator(0);
   }

   @Override
   public ListIterator listIterator(final int start) {
      final int len = this.size();
      if (start >= 0 && start <= len) {
         return new ListIterator() {
            int cursor = start;
            int modCount;

            {
               this.modCount = NativeArray.this.modCount;
            }

            @Override
            public boolean hasNext() {
               return this.cursor < len;
            }

            @Override
            public Object next() {
               NativeArray.this.checkModCount(this.modCount);
               if (this.cursor == len) {
                  throw new NoSuchElementException();
               } else {
                  return NativeArray.this.get(this.cursor++);
               }
            }

            @Override
            public boolean hasPrevious() {
               return this.cursor > 0;
            }

            @Override
            public Object previous() {
               NativeArray.this.checkModCount(this.modCount);
               if (this.cursor == 0) {
                  throw new NoSuchElementException();
               } else {
                  return NativeArray.this.get(--this.cursor);
               }
            }

            @Override
            public int nextIndex() {
               return this.cursor;
            }

            @Override
            public int previousIndex() {
               return this.cursor - 1;
            }

            @Override
            public void remove() {
               throw new UnsupportedOperationException();
            }

            @Override
            public void add(Object o) {
               throw new UnsupportedOperationException();
            }

            @Override
            public void set(Object o) {
               throw new UnsupportedOperationException();
            }
         };
      } else {
         throw new IndexOutOfBoundsException("Index: " + start);
      }
   }

   @Override
   public boolean add(Object o) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(Object o) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection c) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection c) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection c) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void add(int index, Object element) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(int index, Collection c) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object set(int index, Object element) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object remove(int index) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List subList(final int fromIndex, final int toIndex) {
      if (fromIndex < 0) {
         throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
      } else if (toIndex > this.size()) {
         throw new IndexOutOfBoundsException("toIndex = " + toIndex);
      } else if (fromIndex > toIndex) {
         throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
      } else {
         return new AbstractList() {
            private int mc = NativeArray.this.modCount;

            @Override
            public Object get(int index) {
               NativeArray.this.checkModCount(this.mc);
               return NativeArray.this.get(index + fromIndex);
            }

            @Override
            public int size() {
               NativeArray.this.checkModCount(this.mc);
               return toIndex - fromIndex;
            }
         };
      }
   }

   private void checkModCount(int modCount) {
      if (this.modCount != modCount) {
         throw new ConcurrentModificationException();
      }
   }

   @Override
   public <T> T createDataObject(Supplier<T> instanceFactory, Context cx) {
      List<T> list = this.createDataObjectList(instanceFactory, cx);
      if (list.isEmpty()) {
         throw new ArrayIndexOutOfBoundsException("Array doesn't contain any objects");
      } else {
         return (T)list.getFirst();
      }
   }

   @Override
   public <T> List<T> createDataObjectList(Supplier<T> instanceFactory, Context cx) {
      List<T> list = new ArrayList<>();

      for (Object o : this) {
         if (o instanceof DataObject) {
            list.add(((DataObject)o).createDataObject(instanceFactory, cx));
         } else {
            list.add((T)o);
         }
      }

      return list;
   }

   @Override
   public boolean isDataObjectList() {
      return true;
   }
}
