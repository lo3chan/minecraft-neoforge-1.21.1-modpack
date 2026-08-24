package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.DefaultValueTypeHint;
import dev.latvian.mods.rhino.util.Deletable;
import dev.latvian.mods.rhino.util.WrappedReflectionMethod;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ScriptableObject implements Scriptable, SymbolScriptable, ConstProperties {
   public static final int EMPTY = 0;
   public static final int READONLY = 1;
   public static final int DONTENUM = 2;
   public static final int PERMANENT = 4;
   public static final int UNINITIALIZED_CONST = 8;
   public static final int CONST = 13;
   private static final WrappedExecutable GET_ARRAY_LENGTH = (cx, scope, self, args) -> ((ScriptableObject)self).getExternalArrayLength();
   private static final Comparator<Object> KEY_COMPARATOR = new ScriptableObject.KeyComparator();
   private final transient SlotMapContainer slotMap;
   private Scriptable prototypeObject;
   private Scriptable parentScopeObject;
   private transient ExternalArrayData externalData;
   private volatile Map<Object, Object> associatedValues;
   private boolean isExtensible = true;
   private boolean isSealed = false;

   protected static ScriptableObject buildDataDescriptor(Scriptable scope, Object value, int attributes, Context cx) {
      ScriptableObject desc = new NativeObject(cx.factory);
      ScriptRuntime.setBuiltinProtoAndParent(cx, scope, desc, TopLevel.Builtins.Object);
      desc.defineProperty(cx, "value", value, 0);
      desc.setCommonDescriptorProperties(cx, attributes, true);
      return desc;
   }

   protected void setCommonDescriptorProperties(Context cx, int attributes, boolean defineWritable) {
      if (defineWritable) {
         this.defineProperty(cx, "writable", (attributes & 1) == 0, 0);
      }

      this.defineProperty(cx, "enumerable", (attributes & 2) == 0, 0);
      this.defineProperty(cx, "configurable", (attributes & 4) == 0, 0);
   }

   static void checkValidAttributes(int attributes) {
      int mask = 15;
      if ((attributes & -16) != 0) {
         throw new IllegalArgumentException(String.valueOf(attributes));
      }
   }

   private static SlotMapContainer createSlotMap(int initialSize) {
      return new SlotMapContainer(initialSize);
   }

   public static Object getDefaultValue(Scriptable object, DefaultValueTypeHint typeHint, Context cx) {
      for (int i = 0; i < 2; i++) {
         boolean tryToString;
         if (typeHint == DefaultValueTypeHint.STRING) {
            tryToString = i == 0;
         } else {
            tryToString = i == 1;
         }

         String methodName;
         if (tryToString) {
            methodName = "toString";
         } else {
            methodName = "valueOf";
         }

         if (getProperty(object, methodName, cx) instanceof Function fun) {
            Object var9 = fun.call(cx, fun.getParentScope(), object, ScriptRuntime.EMPTY_OBJECTS);
            if (var9 != null) {
               if (!(var9 instanceof Scriptable)) {
                  return var9;
               }

               if (typeHint == DefaultValueTypeHint.CLASS || typeHint == DefaultValueTypeHint.FUNCTION) {
                  return var9;
               }

               if (tryToString && var9 instanceof Wrapper) {
                  Object u = ((Wrapper)var9).unwrap();
                  if (u instanceof String) {
                     return u;
                  }
               }
            }
         }
      }

      throw ScriptRuntime.typeError1(cx, "msg.default.value", typeHint == null ? "undefined" : typeHint.name);
   }

   private static <T extends Scriptable> Class<T> extendsScriptable(Class<?> c) {
      return (Class<T>)(ScriptRuntime.ScriptableClass.isAssignableFrom(c) ? c : null);
   }

   public static void defineProperty(Scriptable destination, String propertyName, Object value, int attributes, Context cx) {
      if (destination instanceof ScriptableObject so) {
         so.defineProperty(cx, propertyName, value, attributes);
      } else {
         destination.put(cx, propertyName, destination, value);
      }
   }

   public static void defineConstProperty(Scriptable destination, String propertyName, Context cx) {
      if (destination instanceof ConstProperties cp) {
         cp.defineConst(cx, propertyName, destination);
      } else {
         defineProperty(destination, propertyName, Undefined.INSTANCE, 13, cx);
      }
   }

   protected static boolean isTrue(Object value, Context cx) {
      return value != NOT_FOUND && ScriptRuntime.toBoolean(cx, value);
   }

   protected static boolean isFalse(Object value, Context cx) {
      return !isTrue(value, cx);
   }

   protected static Scriptable ensureScriptable(Object arg, Context cx) {
      if (!(arg instanceof Scriptable)) {
         throw ScriptRuntime.typeError1(cx, "msg.arg.not.object", ScriptRuntime.typeof(cx, arg));
      } else {
         return (Scriptable)arg;
      }
   }

   protected static SymbolScriptable ensureSymbolScriptable(Object arg, Context cx) {
      if (!(arg instanceof SymbolScriptable)) {
         throw ScriptRuntime.typeError1(cx, "msg.object.not.symbolscriptable", ScriptRuntime.typeof(cx, arg));
      } else {
         return (SymbolScriptable)arg;
      }
   }

   protected static ScriptableObject ensureScriptableObject(Object arg, Context cx) {
      if (!(arg instanceof ScriptableObject)) {
         throw ScriptRuntime.typeError1(cx, "msg.arg.not.object", ScriptRuntime.typeof(cx, arg));
      } else {
         return (ScriptableObject)arg;
      }
   }

   public static Scriptable getObjectPrototype(Scriptable scope, Context cx) {
      return TopLevel.getBuiltinPrototype(getTopLevelScope(scope), TopLevel.Builtins.Object, cx);
   }

   public static Scriptable getFunctionPrototype(Scriptable scope, Context cx) {
      return TopLevel.getBuiltinPrototype(getTopLevelScope(scope), TopLevel.Builtins.Function, cx);
   }

   public static Scriptable getGeneratorFunctionPrototype(Scriptable scope, Context cx) {
      return TopLevel.getBuiltinPrototype(getTopLevelScope(scope), TopLevel.Builtins.GeneratorFunction, cx);
   }

   public static Scriptable getArrayPrototype(Scriptable scope, Context cx) {
      return TopLevel.getBuiltinPrototype(getTopLevelScope(scope), TopLevel.Builtins.Array, cx);
   }

   public static Scriptable getClassPrototype(Scriptable scope, String className, Context cx) {
      scope = getTopLevelScope(scope);
      Object ctor = getProperty(scope, className, cx);
      Object proto;
      if (ctor instanceof BaseFunction) {
         proto = ((BaseFunction)ctor).getPrototypeProperty(cx);
      } else {
         if (!(ctor instanceof Scriptable ctorObj)) {
            return null;
         }

         proto = ctorObj.get(cx, "prototype", ctorObj);
      }

      return proto instanceof Scriptable ? (Scriptable)proto : null;
   }

   public static Scriptable getTopLevelScope(Scriptable obj) {
      while (true) {
         Scriptable parent = obj.getParentScope();
         if (parent == null) {
            return obj;
         }

         obj = parent;
      }
   }

   public static Object getProperty(Scriptable obj, String name, Context cx) {
      Scriptable start = obj;

      Object result;
      do {
         result = obj.get(cx, name, start);
         if (result != NOT_FOUND) {
            break;
         }

         obj = obj.getPrototype(cx);
      } while (obj != null);

      return result;
   }

   public static Object getProperty(Scriptable obj, Symbol key, Context cx) {
      Scriptable start = obj;

      Object result;
      do {
         result = ensureSymbolScriptable(obj, cx).get(cx, key, start);
         if (result != NOT_FOUND) {
            break;
         }

         obj = obj.getPrototype(cx);
      } while (obj != null);

      return result;
   }

   public static Object getProperty(Scriptable obj, int index, Context cx) {
      Scriptable start = obj;

      Object result;
      do {
         result = obj.get(cx, index, start);
         if (result != NOT_FOUND) {
            break;
         }

         obj = obj.getPrototype(cx);
      } while (obj != null);

      return result;
   }

   public static boolean hasProperty(Scriptable obj, String name, Context cx) {
      return null != getBase(obj, name, cx);
   }

   public static void redefineProperty(Scriptable obj, String name, boolean isConst, Context cx) {
      Scriptable base = getBase(obj, name, cx);
      if (base != null) {
         if (base instanceof ConstProperties cp && cp.isConst(name)) {
            throw ScriptRuntime.typeError1(cx, "msg.const.redecl", name);
         } else if (isConst) {
            throw ScriptRuntime.typeError1(cx, "msg.var.redecl", name);
         }
      }
   }

   public static boolean hasProperty(Scriptable obj, int index, Context cx) {
      return null != getBase(cx, obj, index);
   }

   public static boolean hasProperty(Scriptable obj, Symbol key, Context cx) {
      return null != getBase(cx, obj, key);
   }

   public static void putProperty(Scriptable obj, String name, Object value, Context cx) {
      Scriptable base = getBase(obj, name, cx);
      if (base == null) {
         base = obj;
      }

      base.put(cx, name, obj, value);
   }

   public static void putProperty(Scriptable obj, Symbol key, Object value, Context cx) {
      Scriptable base = getBase(cx, obj, key);
      if (base == null) {
         base = obj;
      }

      ensureSymbolScriptable(base, cx).put(cx, key, obj, value);
   }

   public static void putConstProperty(Scriptable obj, String name, Object value, Context cx) {
      Scriptable base = getBase(obj, name, cx);
      if (base == null) {
         base = obj;
      }

      if (base instanceof ConstProperties) {
         ((ConstProperties)base).putConst(cx, name, obj, value);
      }
   }

   public static void putProperty(Scriptable obj, int index, Object value, Context cx) {
      Scriptable base = getBase(cx, obj, index);
      if (base == null) {
         base = obj;
      }

      base.put(cx, index, obj, value);
   }

   public static boolean deleteProperty(Scriptable obj, String name, Context cx) {
      Scriptable base = getBase(obj, name, cx);
      if (base == null) {
         return true;
      } else {
         base.delete(cx, name);
         return !base.has(cx, name, obj);
      }
   }

   public static boolean deleteProperty(Scriptable obj, int index, Context cx) {
      Scriptable base = getBase(cx, obj, index);
      if (base == null) {
         return true;
      } else {
         base.delete(cx, index);
         return !base.has(cx, index, obj);
      }
   }

   public static Object[] getPropertyIds(Context cx, Scriptable obj) {
      if (obj == null) {
         return ScriptRuntime.EMPTY_OBJECTS;
      } else {
         Object[] result = obj.getIds(cx);
         ObjToIntMap map = null;

         while (true) {
            obj = obj.getPrototype(cx);
            if (obj == null) {
               if (map != null) {
                  result = map.getKeys();
               }

               return result;
            }

            Object[] ids = obj.getIds(cx);
            if (ids.length != 0) {
               if (map == null) {
                  if (result.length == 0) {
                     result = ids;
                     continue;
                  }

                  map = new ObjToIntMap(result.length + ids.length);

                  for (int i = 0; i != result.length; i++) {
                     map.intern(result[i]);
                  }

                  result = null;
               }

               for (int i = 0; i != ids.length; i++) {
                  map.intern(ids[i]);
               }
            }
         }
      }
   }

   static Scriptable getBase(Scriptable start, String name, Context cx) {
      Scriptable obj = start;

      while (!obj.has(cx, name, start)) {
         obj = obj.getPrototype(cx);
         if (obj == null) {
            break;
         }
      }

      return obj;
   }

   static Scriptable getBase(Context cx, Scriptable start, int index) {
      Scriptable obj = start;

      while (!obj.has(cx, index, start)) {
         obj = obj.getPrototype(cx);
         if (obj == null) {
            break;
         }
      }

      return obj;
   }

   static Scriptable getBase(Context cx, Scriptable start, Symbol key) {
      Scriptable obj = start;

      while (!ensureSymbolScriptable(obj, cx).has(cx, key, start)) {
         obj = obj.getPrototype(cx);
         if (obj == null) {
            break;
         }
      }

      return obj;
   }

   public static Object getTopScopeValue(Scriptable scope, Object key, Context cx) {
      scope = getTopLevelScope(scope);

      do {
         if (scope instanceof ScriptableObject so) {
            Object value = so.getAssociatedValue(key);
            if (value != null) {
               return value;
            }
         }

         scope = scope.getPrototype(cx);
      } while (scope != null);

      return null;
   }

   public ScriptableObject() {
      this.slotMap = createSlotMap(0);
   }

   public ScriptableObject(Scriptable scope, Scriptable prototype) {
      if (scope == null) {
         throw new IllegalArgumentException();
      } else {
         this.parentScopeObject = scope;
         this.prototypeObject = prototype;
         this.slotMap = createSlotMap(0);
      }
   }

   @Override
   public MemberType getTypeOf() {
      return this.avoidObjectDetection() ? MemberType.UNDEFINED : MemberType.OBJECT;
   }

   @Override
   public abstract String getClassName();

   @Override
   public boolean has(Context cx, String name, Scriptable start) {
      return null != this.slotMap.query(name, 0);
   }

   @Override
   public boolean has(Context cx, int index, Scriptable start) {
      return this.externalData != null ? index < this.externalData.getArrayLength() : null != this.slotMap.query(null, index);
   }

   @Override
   public boolean has(Context cx, Symbol key, Scriptable start) {
      return null != this.slotMap.query(key, 0);
   }

   @Override
   public Object get(Context cx, String name, Scriptable start) {
      Slot slot = this.slotMap.query(name, 0);
      return slot == null ? NOT_FOUND : slot.getValue(start, cx);
   }

   @Override
   public Object get(Context cx, int index, Scriptable start) {
      if (this.externalData != null) {
         return index < this.externalData.getArrayLength() ? this.externalData.getArrayElement(index) : NOT_FOUND;
      } else {
         Slot slot = this.slotMap.query(null, index);
         return slot == null ? NOT_FOUND : slot.getValue(start, cx);
      }
   }

   @Override
   public Object get(Context cx, Symbol key, Scriptable start) {
      Slot slot = this.slotMap.query(key, 0);
      return slot == null ? NOT_FOUND : slot.getValue(start, cx);
   }

   @Override
   public void put(Context cx, String name, Scriptable start, Object value) {
      if (!this.putImpl(cx, name, 0, start, value)) {
         if (start == this) {
            throw Kit.codeBug();
         } else {
            start.put(cx, name, start, value);
         }
      }
   }

   @Override
   public void put(Context cx, int index, Scriptable start, Object value) {
      if (this.externalData != null) {
         if (index < this.externalData.getArrayLength()) {
            this.externalData.setArrayElement(index, value);
         } else {
            throw new JavaScriptException(
               cx, ScriptRuntime.newNativeError(cx, this, TopLevel.NativeErrors.RangeError, new Object[]{"External array index out of bounds "}), null, 0
            );
         }
      } else if (!this.putImpl(cx, null, index, start, value)) {
         if (start == this) {
            throw Kit.codeBug();
         } else {
            start.put(cx, index, start, value);
         }
      }
   }

   @Override
   public void put(Context cx, Symbol key, Scriptable start, Object value) {
      if (!this.putImpl(cx, key, 0, start, value)) {
         if (start == this) {
            throw Kit.codeBug();
         } else {
            ensureSymbolScriptable(start, cx).put(cx, key, start, value);
         }
      }
   }

   @Override
   public void delete(Context cx, String name) {
      this.checkNotSealed(cx, name, 0);
      Slot s = this.slotMap.query(name, 0);
      this.slotMap.compute(name, 0, (k, ix, existing) -> checkSlotRemoval(cx, k, existing));
      Deletable.deleteObject(s == null ? null : s.value);
   }

   @Override
   public void delete(Context cx, int index) {
      this.checkNotSealed(cx, null, index);
      Slot s = this.slotMap.query(null, index);
      this.slotMap.compute(null, index, (k, ix, existing) -> checkSlotRemoval(cx, k, existing));
      Deletable.deleteObject(s == null ? null : s.value);
   }

   @Override
   public void delete(Context cx, Symbol key) {
      this.checkNotSealed(cx, key, 0);
      this.slotMap.compute(key, 0, (k, ix, existing) -> checkSlotRemoval(cx, k, existing));
   }

   private static Slot checkSlotRemoval(Context cx, Object key, Slot slot) {
      if (slot == null || (slot.getAttributes() & 4) == 0) {
         return null;
      } else if (cx.isStrictMode()) {
         throw ScriptRuntime.typeError1(cx, "msg.delete.property.with.configurable.false", key);
      } else {
         return slot;
      }
   }

   @Override
   public void putConst(Context cx, String name, Scriptable start, Object value) {
      if (!this.putConstImpl(cx, name, 0, start, value, 1)) {
         if (start == this) {
            throw Kit.codeBug();
         } else {
            if (start instanceof ConstProperties) {
               ((ConstProperties)start).putConst(cx, name, start, value);
            } else {
               start.put(cx, name, start, value);
            }
         }
      }
   }

   @Override
   public void defineConst(Context cx, String name, Scriptable start) {
      if (!this.putConstImpl(cx, name, 0, start, Undefined.INSTANCE, 8)) {
         if (start == this) {
            throw Kit.codeBug();
         } else {
            if (start instanceof ConstProperties) {
               ((ConstProperties)start).defineConst(cx, name, start);
            }
         }
      }
   }

   @Override
   public boolean isConst(String name) {
      Slot slot = this.slotMap.query(name, 0);
      return slot == null ? false : (slot.getAttributes() & 5) == 5;
   }

   public int getAttributes(Context cx, String name) {
      return this.getAttributeSlot(cx, name, 0).getAttributes();
   }

   public int getAttributes(Context cx, int index) {
      return this.getAttributeSlot(cx, null, index).getAttributes();
   }

   public int getAttributes(Context cx, Symbol sym) {
      return this.getAttributeSlot(cx, sym).getAttributes();
   }

   public void setAttributes(Context cx, String name, int attributes) {
      this.checkNotSealed(cx, name, 0);
      Slot attrSlot = this.slotMap.modify(name, 0, 0);
      attrSlot.setAttributes(attributes);
   }

   public void setAttributes(Context cx, int index, int attributes) {
      this.checkNotSealed(cx, null, index);
      Slot attrSlot = this.slotMap.modify(null, index, 0);
      attrSlot.setAttributes(attributes);
   }

   public void setAttributes(Context cx, Symbol key, int attributes) {
      this.checkNotSealed(cx, key, 0);
      Slot attrSlot = this.slotMap.modify(key, 0, 0);
      attrSlot.setAttributes(attributes);
   }

   public void setGetterOrSetter(Context cx, Object name, int index, Callable getterOrSetter, boolean isSetter) {
      this.setGetterOrSetter(cx, name, index, getterOrSetter, isSetter, false);
   }

   private void setGetterOrSetter(Context cx, Object name, int index, Callable getterOrSetter, boolean isSetter, boolean force) {
      if (name != null && index != 0) {
         throw new IllegalArgumentException(name.toString());
      } else {
         if (!force) {
            this.checkNotSealed(cx, name, index);
         }

         AccessorSlot fslot;
         if (this.isExtensible()) {
            fslot = this.slotMap.compute(name, index, ScriptableObject::ensureAccessorSlot);
         } else {
            if (!(this.slotMap.query(name, index) instanceof AccessorSlot gs)) {
               return;
            }

            fslot = gs;
         }

         if (!force) {
            int attributes = fslot.getAttributes();
            if ((attributes & 1) != 0) {
               throw Context.reportRuntimeError1("msg.modify.readonly", name, cx);
            }
         }

         if (isSetter) {
            fslot.setter = getterOrSetter instanceof Function f ? new AccessorSlot.FunctionSetter(f) : null;
         } else {
            fslot.getter = getterOrSetter instanceof Function f ? new AccessorSlot.FunctionGetter(f) : null;
         }

         fslot.value = Undefined.INSTANCE;
      }
   }

   public Function getGetterOrSetter(Context cx, String name, int index, Scriptable scope, boolean isSetter) {
      if (name != null && index != 0) {
         throw new IllegalArgumentException(name);
      } else {
         Slot slot = this.slotMap.query(name, index);
         if (slot == null) {
            return null;
         } else {
            return isSetter ? slot.getSetterFunction(cx, name, scope) : slot.getGetterFunction(cx, name, scope);
         }
      }
   }

   protected boolean isGetterOrSetter(String name, int index, boolean setter) {
      Slot slot = this.slotMap.query(name, index);
      return slot != null && slot.isSetterSlot();
   }

   public ExternalArrayData getExternalArrayData() {
      return this.externalData;
   }

   public void setExternalArrayData(Context cx, ExternalArrayData array) {
      this.externalData = array;
      if (array == null) {
         this.delete(cx, "length");
      } else {
         this.defineProperty(cx, "length", null, GET_ARRAY_LENGTH, null, 3);
      }
   }

   public Object getExternalArrayLength() {
      return this.externalData == null ? 0 : this.externalData.getArrayLength();
   }

   @Override
   public Scriptable getPrototype(Context cx) {
      return this.prototypeObject;
   }

   @Override
   public void setPrototype(Scriptable m) {
      this.prototypeObject = m;
   }

   @Override
   public Scriptable getParentScope() {
      return this.parentScopeObject;
   }

   @Override
   public void setParentScope(Scriptable m) {
      this.parentScopeObject = m;
   }

   @Override
   public Object[] getIds(Context cx) {
      return this.getIds(cx, false, false);
   }

   @Override
   public Object[] getAllIds(Context cx) {
      return this.getIds(cx, true, false);
   }

   @Override
   public Object getDefaultValue(Context cx, DefaultValueTypeHint typeHint) {
      return getDefaultValue(this, typeHint, cx);
   }

   @Override
   public boolean hasInstance(Context cx, Scriptable instance) {
      return ScriptRuntime.jsDelegatesTo(cx, instance, this);
   }

   public boolean avoidObjectDetection() {
      return false;
   }

   protected Object equivalentValues(Object value) {
      return this == value ? Boolean.TRUE : NOT_FOUND;
   }

   public void defineProperty(Context cx, String propertyName, Object value, int attributes) {
      this.checkNotSealed(cx, propertyName, 0);
      this.put(cx, propertyName, this, value);
      this.setAttributes(cx, propertyName, attributes);
   }

   public void defineProperty(Context cx, Scriptable scope, String name, int length, Callable target, int attributes, int propertyAttributes) {
      LambdaFunction f = new LambdaFunction(cx, scope, name, length, target, false);
      f.setStandardPropertyAttributes(propertyAttributes);
      this.defineProperty(cx, name, f, attributes);
   }

   public void defineProperty(Context cx, Symbol key, Object value, int attributes) {
      this.checkNotSealed(cx, key, 0);
      this.put(cx, key, this, value);
      this.setAttributes(cx, key, attributes);
   }

   public void defineProperty(Context cx, String propertyName, Class<?> clazz, int attributes) {
      int length = propertyName.length();
      if (length == 0) {
         throw new IllegalArgumentException();
      } else {
         char[] buf = new char[3 + length];
         propertyName.getChars(0, length, buf, 3);
         buf[3] = Character.toUpperCase(buf[3]);
         buf[0] = 'g';
         buf[1] = 'e';
         buf[2] = 't';
         String getterName = new String(buf);
         buf[0] = 's';
         String setterName = new String(buf);
         List<CachedMethodInfo> methods = FunctionObject.getMethodList(cx, clazz);
         WrappedExecutable getter = WrappedReflectionMethod.of(FunctionObject.findSingleMethod(methods, getterName, cx));
         WrappedExecutable setter = WrappedReflectionMethod.of(FunctionObject.findSingleMethod(methods, setterName, cx));
         if (setter == null) {
            attributes |= 1;
         }

         this.defineProperty(cx, propertyName, null, getter, setter, attributes);
      }
   }

   public void defineProperty(Context cx, String propertyName, Object delegateTo, WrappedExecutable getter, WrappedExecutable setter, int attributes) {
      MemberBox getterBox = null;
      if (getter != null) {
         getterBox = new MemberBox(getter);
         if (!getter.isStatic()) {
            getterBox.delegateTo = delegateTo;
         } else {
            getterBox.delegateTo = void.class;
         }
      }

      MemberBox setterBox = null;
      if (setter != null) {
         if (setter.getReturnType() != TypeInfo.PRIMITIVE_VOID) {
            throw Context.reportRuntimeError1("msg.setter.return", setter.toString(), cx);
         }

         setterBox = new MemberBox(setter);
         if (!setter.isStatic()) {
            setterBox.delegateTo = delegateTo;
         } else {
            setterBox.delegateTo = void.class;
         }
      }

      AccessorSlot getterSlot = this.slotMap.compute(propertyName, 0, ScriptableObject::ensureAccessorSlot);
      getterSlot.setAttributes(attributes);
      if (getterBox != null) {
         getterSlot.getter = new AccessorSlot.MemberBoxGetter(getterBox);
      }

      if (setterBox != null) {
         getterSlot.setter = new AccessorSlot.MemberBoxSetter(setterBox);
      }
   }

   public void defineOwnProperties(Context cx, ScriptableObject props) {
      Object[] ids = props.getIds(cx, false, true);
      ScriptableObject[] descs = new ScriptableObject[ids.length];
      int i = 0;

      for (int len = ids.length; i < len; i++) {
         Object descObj = ScriptRuntime.getObjectElem(cx, props, ids[i]);
         ScriptableObject desc = ensureScriptableObject(descObj, cx);
         this.checkPropertyDefinition(cx, desc);
         descs[i] = desc;
      }

      i = 0;

      for (int len = ids.length; i < len; i++) {
         this.defineOwnProperty(cx, ids[i], descs[i]);
      }
   }

   public void defineOwnProperty(Context cx, Object id, ScriptableObject desc) {
      this.checkPropertyDefinition(cx, desc);
      this.defineOwnProperty(cx, id, desc, true);
   }

   protected void defineOwnProperty(Context cx, Object id, ScriptableObject desc, boolean checkValid) {
      Object key;
      int index;
      if (id instanceof Symbol) {
         key = id;
         index = 0;
      } else {
         ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, id);
         if (s.stringId == null) {
            key = null;
            index = s.index;
         } else {
            key = s.stringId;
            index = 0;
         }
      }

      Slot aSlot = this.slotMap.query(key, index);
      if (aSlot instanceof BuiltInSlot) {
         ((BuiltInSlot)aSlot).applyNewDescriptor(id, desc, checkValid, key, index, cx);
      } else {
         this.defineOrdinaryProperty(cx, id, desc, checkValid, key, index);
      }
   }

   void defineOrdinaryProperty(Context cx, Object id, ScriptableObject desc, boolean checkValid, Object key, int index) {
      Object enumerable = getProperty(desc, "enumerable", cx);
      Object writable = getProperty(desc, "writable", cx);
      Object configurable = getProperty(desc, "configurable", cx);
      Object getter = getProperty(desc, "get", cx);
      Object setter = getProperty(desc, "set", cx);
      Object value = getProperty(desc, "value", cx);
      boolean accessorDescriptor = isAccessorDescriptor(cx, desc);
      this.slotMap.compute(key, index, (k, ix, existing) -> {
         if (checkValid) {
            this.checkPropertyChangeForSlot(cx, id, existing, desc);
         }

         Slot slot;
         int attributes;
         if (existing == null) {
            slot = new Slot(k, ix, 0);
            attributes = this.applyDescriptorToAttributeBitset(cx, 7, enumerable, writable, configurable);
         } else {
            slot = existing;
            attributes = this.applyDescriptorToAttributeBitset(cx, existing.getAttributes(), enumerable, writable, configurable);
         }

         if (accessorDescriptor) {
            AccessorSlot fslot;
            if (slot instanceof AccessorSlot gs) {
               fslot = gs;
            } else {
               fslot = new AccessorSlot(slot);
               slot = fslot;
            }

            if (getter != NOT_FOUND) {
               fslot.getter = new AccessorSlot.FunctionGetter(getter);
            }

            if (setter != NOT_FOUND) {
               fslot.setter = new AccessorSlot.FunctionSetter(setter);
            }

            fslot.value = Undefined.INSTANCE;
         } else if (slot instanceof BuiltInSlot) {
            if (value != NOT_FOUND) {
               slot.setValue(value, this, this, cx, true);
            }
         } else {
            if (!slot.isValueSlot() && isDataDescriptor(desc, cx)) {
               slot = new Slot(slot);
            }

            if (value != NOT_FOUND) {
               slot.value = value;
            } else if (existing == null) {
               slot.value = Undefined.INSTANCE;
            }
         }

         slot.setAttributes(attributes);
         return slot;
      });
   }

   public void defineProperty(String name, Supplier<Object> getter, Consumer<Object> setter, int attributes) {
      LambdaSlot lSlot = this.slotMap.compute(name, 0, ScriptableObject::ensureLambdaSlot);
      lSlot.setAttributes(attributes);
      lSlot.getter = getter;
      lSlot.setter = setter;
   }

   public static <T extends ScriptableObject> void defineBuiltInProperty(
      T owner, String name, int attributes, BuiltInSlot.Getter<T> getter, BuiltInSlot.Setter<T> setter, BuiltInSlot.AttributeSetter<T> attrSetter
   ) {
      owner.addSlot(new BuiltInSlot<>(name, 0, attributes, owner, getter, setter, attrSetter));
   }

   public static <T extends ScriptableObject> void defineBuiltInProperty(
      T owner,
      String name,
      int attributes,
      BuiltInSlot.Getter<T> getter,
      BuiltInSlot.Setter<T> setter,
      BuiltInSlot.AttributeSetter<T> attrSetter,
      BuiltInSlot.PropDescriptionSetter<T> propDescSetter
   ) {
      owner.addSlot(new BuiltInSlot<>(name, 0, attributes, owner, getter, setter, attrSetter, propDescSetter));
   }

   void addSlot(Slot slot) {
      this.slotMap.add(slot);
   }

   public void defineProperty(
      Context cx, String name, java.util.function.Function<Scriptable, Object> getter, BiConsumer<Scriptable, Object> setter, int attributes
   ) {
      if (getter == null && setter == null) {
         throw ScriptRuntime.typeError0(cx, "msg.getter.setter.required");
      } else {
         LambdaAccessorSlot newSlot = this.createLambdaAccessorSlot(cx, name, 0, getter, setter, attributes);
         ScriptableObject newDesc = newSlot.buildPropertyDescriptor(cx);
         this.checkPropertyDefinition(cx, newDesc);
         this.slotMap.compute(name, 0, (id, index, existing) -> {
            if (existing != null) {
               return this.replaceExistingLambdaSlot(cx, name, existing, newSlot);
            } else {
               this.checkPropertyChangeForSlot(cx, name, null, newDesc);
               return newSlot;
            }
         });
      }
   }

   private LambdaAccessorSlot replaceExistingLambdaSlot(Context cx, String name, Slot existing, LambdaAccessorSlot newSlot) {
      LambdaAccessorSlot replacedSlot;
      if (existing instanceof LambdaAccessorSlot lSlot) {
         replacedSlot = lSlot;
      } else {
         replacedSlot = new LambdaAccessorSlot(existing);
      }

      replacedSlot.replaceWith(newSlot);
      ScriptableObject replacedDesc = replacedSlot.buildPropertyDescriptor(cx);
      this.checkPropertyChangeForSlot(cx, name, existing, replacedDesc);
      return replacedSlot;
   }

   private LambdaAccessorSlot createLambdaAccessorSlot(
      Context cx, Object name, int index, java.util.function.Function<Scriptable, Object> getter, BiConsumer<Scriptable, Object> setter, int attributes
   ) {
      LambdaAccessorSlot slot = new LambdaAccessorSlot(name, index);
      slot.setGetter(cx, this, getter);
      slot.setSetter(cx, this, setter);
      slot.setAttributes(attributes);
      return slot;
   }

   private static AccessorSlot ensureAccessorSlot(Object name, int index, Slot existing) {
      if (existing == null) {
         return new AccessorSlot(name, index);
      } else {
         return existing instanceof AccessorSlot aSlot ? aSlot : new AccessorSlot(existing);
      }
   }

   private static LambdaSlot ensureLambdaSlot(Object name, int index, Slot existing) {
      if (existing == null) {
         return new LambdaSlot(name, index);
      } else {
         return existing instanceof LambdaSlot lSlot ? lSlot : new LambdaSlot(existing);
      }
   }

   protected void checkPropertyDefinition(Context cx, ScriptableObject desc) {
      Object getter = getProperty(desc, "get", cx);
      if (getter != NOT_FOUND && getter != Undefined.INSTANCE && !(getter instanceof Callable)) {
         throw ScriptRuntime.notFunctionError(cx, getter);
      } else {
         Object setter = getProperty(desc, "set", cx);
         if (setter != NOT_FOUND && setter != Undefined.INSTANCE && !(setter instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(cx, setter);
         } else if (isDataDescriptor(desc, cx) && isAccessorDescriptor(cx, desc)) {
            throw ScriptRuntime.typeError0(cx, "msg.both.data.and.accessor.desc");
         }
      }
   }

   protected void checkPropertyChangeForSlot(Context cx, Object id, Slot current, ScriptableObject desc) {
      if (current == null) {
         if (!this.isExtensible()) {
            throw ScriptRuntime.typeError0(cx, "msg.not.extensible");
         }
      } else if ((current.getAttributes() & 4) != 0) {
         if (isTrue(getProperty(desc, "configurable", cx), cx)) {
            throw ScriptRuntime.typeError1(cx, "msg.change.configurable.false.to.true", id);
         }

         if ((current.getAttributes() & 2) == 0 != isTrue(getProperty(desc, "enumerable", cx), cx)) {
            throw ScriptRuntime.typeError1(cx, "msg.change.enumerable.with.configurable.false", id);
         }

         boolean isData = isDataDescriptor(desc, cx);
         boolean isAccessor = isAccessorDescriptor(cx, desc);
         if (isData || isAccessor) {
            if (isData) {
               if ((current.getAttributes() & 1) != 0) {
                  if (isTrue(getProperty(desc, "writable", cx), cx)) {
                     throw ScriptRuntime.typeError1(cx, "msg.change.writable.false.to.true.with.configurable.false", id);
                  }

                  Object currentValue = current instanceof BuiltInSlot ? current.getValue(null, cx) : current.value;
                  if (!this.sameValue(cx, getProperty(desc, "value", cx), currentValue)) {
                     throw ScriptRuntime.typeError1(cx, "msg.change.value.with.writable.false", id);
                  }
               }
            } else {
               if (!isAccessor || !(current instanceof AccessorSlot accessor)) {
                  throw ScriptRuntime.typeError1(cx, "msg.change.property.data.to.accessor.with.configurable.false", id);
               }

               if (!accessor.isSameSetterFunction(cx, getProperty(desc, "set", cx))) {
                  throw ScriptRuntime.typeError1(cx, "msg.change.setter.with.configurable.false", id);
               }

               if (!accessor.isSameGetterFunction(cx, getProperty(desc, "get", cx))) {
                  throw ScriptRuntime.typeError1(cx, "msg.change.getter.with.configurable.false", id);
               }
            }
         }
      }
   }

   protected boolean sameValue(Context cx, Object newValue, Object currentValue) {
      if (newValue == NOT_FOUND) {
         return true;
      } else {
         if (currentValue == NOT_FOUND) {
            currentValue = Undefined.INSTANCE;
         }

         if (currentValue instanceof Number && newValue instanceof Number) {
            double d1 = ((Number)currentValue).doubleValue();
            double d2 = ((Number)newValue).doubleValue();
            if (Double.isNaN(d1) && Double.isNaN(d2)) {
               return true;
            }

            if (d1 == 0.0 && Double.doubleToLongBits(d1) != Double.doubleToLongBits(d2)) {
               return false;
            }
         }

         return ScriptRuntime.shallowEq(cx, currentValue, newValue);
      }
   }

   protected int applyDescriptorToAttributeBitset(Context cx, int attributes, Object enumerable, Object writable, Object configurable) {
      if (enumerable != NOT_FOUND) {
         attributes = ScriptRuntime.toBoolean(cx, enumerable) ? attributes & -3 : attributes | 2;
      }

      if (writable != NOT_FOUND) {
         attributes = ScriptRuntime.toBoolean(cx, writable) ? attributes & -2 : attributes | 1;
      }

      if (configurable != NOT_FOUND) {
         attributes = ScriptRuntime.toBoolean(cx, configurable) ? attributes & -5 : attributes | 4;
      }

      return attributes;
   }

   protected static boolean isDataDescriptor(ScriptableObject desc, Context cx) {
      return hasProperty(desc, "value", cx) || hasProperty(desc, "writable", cx);
   }

   protected static boolean isAccessorDescriptor(Context cx, ScriptableObject desc) {
      return hasProperty(desc, "get", cx) || hasProperty(desc, "set", cx);
   }

   protected boolean isGenericDescriptor(Context cx, ScriptableObject desc) {
      return !isDataDescriptor(desc, cx) && !isAccessorDescriptor(cx, desc);
   }

   public void defineFunctionProperties(Context cx, String[] names, Class<?> clazz, int attributes) {
      List<CachedMethodInfo> methods = FunctionObject.getMethodList(cx, clazz);

      for (int i = 0; i < names.length; i++) {
         String name = names[i];
         CachedMethodInfo m = FunctionObject.findSingleMethod(methods, name, cx);
         if (m == null) {
            throw Context.reportRuntimeError2("msg.method.not.found", name, clazz.getName(), cx);
         }

         FunctionObject f = new FunctionObject(name, m, this, cx);
         this.defineProperty(cx, name, f, attributes);
      }
   }

   public boolean isExtensible() {
      return this.isExtensible;
   }

   public void preventExtensions() {
      this.isExtensible = false;
   }

   public void sealObject(Context cx) {
      if (!this.isSealed) {
         long stamp = this.slotMap.readLock();

         try {
            this.isSealed = true;
         } finally {
            this.slotMap.unlockRead(stamp);
         }
      }
   }

   public final boolean isSealed(Context cx) {
      return this.isSealed;
   }

   private void checkNotSealed(Context cx, Object key, int index) {
      if (this.isSealed(cx)) {
         String str = key != null ? key.toString() : Integer.toString(index);
         throw Context.reportRuntimeError1("msg.modify.sealed", str, cx);
      }
   }

   public final Object getAssociatedValue(Object key) {
      Map<Object, Object> h = this.associatedValues;
      return h == null ? null : h.get(key);
   }

   public final synchronized Object associateValue(Object key, Object value) {
      if (value == null) {
         throw new IllegalArgumentException();
      } else {
         Map<Object, Object> h = this.associatedValues;
         if (h == null) {
            h = new HashMap<>();
            this.associatedValues = h;
         }

         return Kit.initHash(h, key, value);
      }
   }

   private boolean putImpl(Context cx, Object key, int index, Scriptable start, Object value) {
      return this.putImpl(cx, key, index, start, value, cx.isStrictMode());
   }

   boolean putImpl(Context cx, Object key, int index, Scriptable start, Object value, boolean isThrow) {
      Slot slot;
      if (this != start) {
         slot = this.slotMap.query(key, index);
         if (!this.isExtensible && (slot == null || !slot.isSetterSlot() && (slot.getAttributes() & 1) != 0) && isThrow) {
            throw ScriptRuntime.typeError0(cx, "msg.not.extensible");
         }

         if (slot == null) {
            return false;
         }
      } else if (!this.isExtensible) {
         slot = this.slotMap.query(key, index);
         if ((slot == null || !slot.isSetterSlot() && (slot.getAttributes() & 1) != 0) && isThrow) {
            throw ScriptRuntime.typeError0(cx, "msg.not.extensible");
         }

         if (slot == null) {
            return true;
         }
      } else {
         if (this.isSealed) {
            this.checkNotSealed(cx, key, index);
         }

         slot = this.slotMap.modify(key, index, 0);
      }

      return slot.setValue(value, this, start, cx, isThrow);
   }

   private boolean putConstImpl(Context cx, String name, int index, Scriptable start, Object value, int constFlag) {
      assert constFlag != 0;

      if (!this.isExtensible && cx.isStrictMode()) {
         throw ScriptRuntime.typeError0(cx, "msg.not.extensible");
      } else {
         Slot slot;
         if (this != start) {
            slot = this.slotMap.query(name, index);
            if (slot == null) {
               return false;
            }
         } else {
            if (this.isExtensible()) {
               this.checkNotSealed(cx, name, index);
               slot = this.slotMap.modify(name, index, 13);
               int attr = slot.getAttributes();
               if ((attr & 1) == 0) {
                  throw Context.reportRuntimeError1("msg.var.redecl", name, cx);
               }

               if ((attr & 8) != 0) {
                  slot.value = value;
                  if (constFlag != 8) {
                     slot.setAttributes(attr & -9);
                  }
               }

               return true;
            }

            slot = this.slotMap.query(name, index);
            if (slot == null) {
               return true;
            }
         }

         return slot.setValue(value, this, start, cx);
      }
   }

   private Slot getAttributeSlot(Context cx, String name, int index) {
      Slot slot = this.slotMap.query(name, index);
      if (slot == null) {
         String str = name != null ? name : Integer.toString(index);
         throw Context.reportRuntimeError1("msg.prop.not.found", str, cx);
      } else {
         return slot;
      }
   }

   private Slot getAttributeSlot(Context cx, Symbol key) {
      Slot slot = this.slotMap.query(key, 0);
      if (slot == null) {
         throw Context.reportRuntimeError1("msg.prop.not.found", key, cx);
      } else {
         return slot;
      }
   }

   Object[] getIds(Context cx, boolean getNonEnumerable, boolean getSymbols) {
      int externalLen = this.externalData == null ? 0 : this.externalData.getArrayLength();
      Object[] a;
      if (externalLen == 0) {
         a = ScriptRuntime.EMPTY_OBJECTS;
      } else {
         a = new Object[externalLen];

         for (int i = 0; i < externalLen; i++) {
            a[i] = i;
         }
      }

      if (this.slotMap.isEmpty()) {
         return a;
      } else {
         int c = externalLen;
         long stamp = this.slotMap.readLock();

         try {
            for (Slot slot : this.slotMap) {
               if ((getNonEnumerable || (slot.getAttributes() & 2) == 0) && (getSymbols || !(slot.name instanceof Symbol))) {
                  if (c == externalLen) {
                     Object[] oldA = a;
                     a = new Object[this.slotMap.dirtySize() + externalLen];
                     if (oldA != null) {
                        System.arraycopy(oldA, 0, a, 0, externalLen);
                     }
                  }

                  a[c++] = slot.name != null ? slot.name : slot.indexOrHash;
               }
            }
         } finally {
            this.slotMap.unlockRead(stamp);
         }

         Object[] result;
         if (c == a.length + externalLen) {
            result = a;
         } else {
            result = new Object[c];
            System.arraycopy(a, 0, result, 0, c);
         }

         if (cx != null) {
            Arrays.sort(result, KEY_COMPARATOR);
         }

         return result;
      }
   }

   protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
      Slot slot = this.querySlot(cx, id);
      if (slot == null) {
         return null;
      } else {
         Scriptable scope = this.getParentScope();
         return slot.getPropertyDescriptor(cx, (Scriptable)(scope == null ? this : scope));
      }
   }

   protected Slot querySlot(Context cx, Object id) {
      if (id instanceof Symbol) {
         return this.slotMap.query(id, 0);
      } else {
         ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, id);
         return s.stringId == null ? this.slotMap.query(null, s.index) : this.slotMap.query(s.stringId, 0);
      }
   }

   public int size() {
      return this.slotMap.size();
   }

   public boolean isEmpty() {
      return this.slotMap.isEmpty();
   }

   public Object get(Context cx, Object key) {
      Object value = null;
      if (key instanceof String) {
         value = this.get(cx, (String)key, this);
      } else if (key instanceof Symbol) {
         value = this.get(cx, (Symbol)key, this);
      } else if (key instanceof Number) {
         value = this.get(cx, ((Number)key).intValue(), this);
      }

      if (value == NOT_FOUND || value == Undefined.INSTANCE) {
         return null;
      } else {
         return value instanceof Wrapper ? ((Wrapper)value).unwrap() : value;
      }
   }

   public static final class KeyComparator implements Comparator<Object>, Serializable {
      private static final long serialVersionUID = 6411335891523988149L;

      @Override
      public int compare(Object o1, Object o2) {
         if (o1 instanceof Integer) {
            if (o2 instanceof Integer) {
               int i1 = (Integer)o1;
               int i2 = (Integer)o2;
               if (i1 < i2) {
                  return -1;
               } else {
                  return i1 > i2 ? 1 : 0;
               }
            } else {
               return -1;
            }
         } else {
            return o2 instanceof Integer ? 1 : 0;
         }
      }
   }
}
