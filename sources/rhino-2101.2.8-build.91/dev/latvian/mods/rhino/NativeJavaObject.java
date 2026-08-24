package dev.latvian.mods.rhino;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import dev.latvian.mods.rhino.type.ParameterizedTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.type.VariableTypeInfo;
import dev.latvian.mods.rhino.util.DefaultValueTypeHint;
import dev.latvian.mods.rhino.util.Deletable;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class NativeJavaObject implements Scriptable, SymbolScriptable, Wrapper {
   protected Scriptable prototype;
   protected Scriptable parent;
   protected transient Object javaObject;
   protected transient TypeInfo typeInfo;
   private transient Map<VariableTypeInfo, TypeInfo> typeMapping;
   protected transient JavaMembers members;
   protected transient Map<String, FieldAndMethods> fieldAndMethods;
   protected transient Map<String, CustomMember> customMembers;
   protected transient boolean isAdapter;
   private static final Callable symbol_iterator = (cx, scope, thisObj, args) -> {
      if (!(thisObj instanceof NativeJavaObject)) {
         throw ScriptRuntime.typeError1(cx, "msg.incompat.call", SymbolKey.ITERATOR);
      } else {
         Object javaObject = ((NativeJavaObject)thisObj).javaObject;
         if (!(javaObject instanceof Iterable)) {
            throw ScriptRuntime.typeError1(cx, "msg.incompat.call", SymbolKey.ITERATOR);
         } else {
            return new NativeJavaObject.JavaIterableIterator(cx, scope, (Iterable<?>)javaObject);
         }
      }
   };

   static void init(ScriptableObject scope, boolean sealed, Context cx) {
      NativeJavaObject.JavaIterableIterator.init(scope, sealed, cx);
   }

   public NativeJavaObject(Scriptable scope, Object javaObject, TypeInfo typeInfo, Context cx) {
      this(scope, javaObject, typeInfo, false, cx);
   }

   public NativeJavaObject(Scriptable scope, Object javaObject, TypeInfo typeInfo, boolean isAdapter, Context cx) {
      this.parent = scope;
      this.javaObject = javaObject;
      this.typeInfo = typeInfo;
      this.isAdapter = isAdapter;
      this.initMembers(cx, scope);
   }

   protected void initMembers(Context cx, Scriptable scope) {
      Class<?> dynamicType;
      if (this.javaObject != null) {
         dynamicType = this.javaObject.getClass();
      } else {
         dynamicType = this.typeInfo.asClass();
      }

      this.members = JavaMembers.lookupClass(cx, scope, dynamicType, this.typeInfo.asClass(), this.isAdapter);
      this.fieldAndMethods = this.members.getFieldAndMethodsObjects(this, this.javaObject, false, cx);
      this.customMembers = null;
   }

   public Map<VariableTypeInfo, TypeInfo> getTypeMapping() {
      if (this.typeMapping == null) {
         if (this.typeInfo instanceof ParameterizedTypeInfo parameterized) {
            TypeVariable<? extends Class<?>>[] parameters = parameterized.asClass().getTypeParameters();
            if (parameters.length == 1) {
               this.typeMapping = Collections.singletonMap(TypeInfo.of(parameters[0]), parameterized.param(0));
            } else {
               Builder<VariableTypeInfo, TypeInfo> mapping = ImmutableMap.builder();
               int i = 0;

               for (int size = parameters.length; i < size; i++) {
                  mapping.put(TypeInfo.of(parameters[i]), parameterized.param(i));
               }

               this.typeMapping = mapping.build();
            }
         } else {
            this.typeMapping = Collections.emptyMap();
         }
      }

      return this.typeMapping;
   }

   public void addCustomMember(CustomMember member) {
      if (this.customMembers == null) {
         this.customMembers = new HashMap<>();
      }

      this.customMembers.put(member.name(), member);
   }

   protected void addCustomFunction(String name, TypeInfo returnType, CustomFunction.Func func, TypeInfo... argTypes) {
      this.addCustomMember(new CustomMember(name, returnType, new CustomFunction(name, func, argTypes)));
   }

   protected void addCustomFunction(String name, TypeInfo returnType, CustomFunction.NoArgFunc func) {
      this.addCustomFunction(name, returnType, func, TypeInfo.EMPTY_ARRAY);
   }

   public void addCustomProperty(String name, TypeInfo type, CustomProperty getter) {
      this.addCustomMember(new CustomMember(name, type, getter));
   }

   @Override
   public boolean has(Context cx, String name, Scriptable start) {
      return this.members.has(cx, name, false) || this.customMembers != null && this.customMembers.containsKey(name);
   }

   @Override
   public boolean has(Context cx, int index, Scriptable start) {
      return false;
   }

   @Override
   public boolean has(Context cx, Symbol key, Scriptable start) {
      return this.javaObject instanceof Iterable && SymbolKey.ITERATOR.equals(key);
   }

   @Override
   public Object get(Context cx, String name, Scriptable start) {
      if (this.fieldAndMethods != null) {
         Object result = this.fieldAndMethods.get(name);
         if (result != null) {
            return result;
         }
      }

      if (this.customMembers != null) {
         CustomMember member = this.customMembers.get(name);
         if (member != null) {
            Object value = member.value();
            if (value instanceof CustomProperty p) {
               value = p.get(cx);
            }

            return cx.javaToJS(value, start, member.type());
         }
      }

      return this.members.get(this, name, this.javaObject, false, cx);
   }

   @Override
   public Object get(Context cx, Symbol key, Scriptable start) {
      return this.javaObject instanceof Iterable && SymbolKey.ITERATOR.equals(key) ? symbol_iterator : Scriptable.NOT_FOUND;
   }

   @Override
   public Object get(Context cx, int index, Scriptable start) {
      throw this.members.reportMemberNotFound(Integer.toString(index), cx);
   }

   @Override
   public void put(Context cx, String name, Scriptable start, Object value) {
      if (this.prototype != null && !this.members.has(cx, name, false)) {
         this.prototype.put(cx, name, this.prototype, value);
      } else {
         this.members.put(this, name, this.javaObject, value, false, cx);
      }
   }

   @Override
   public void put(Context cx, Symbol symbol, Scriptable start, Object value) {
      String name = symbol.toString();
      if (this.prototype == null || this.members.has(cx, name, false)) {
         this.members.put(this, name, this.javaObject, value, false, cx);
      } else if (this.prototype instanceof SymbolScriptable) {
         ((SymbolScriptable)this.prototype).put(cx, symbol, this.prototype, value);
      }
   }

   @Override
   public void put(Context cx, int index, Scriptable start, Object value) {
      throw this.members.reportMemberNotFound(Integer.toString(index), cx);
   }

   @Override
   public boolean hasInstance(Context cx, Scriptable value) {
      return false;
   }

   @Override
   public void delete(Context cx, String name) {
      if (this.fieldAndMethods != null) {
         Object result = this.fieldAndMethods.get(name);
         if (result != null) {
            Deletable.deleteObject(result);
            return;
         }
      }

      if (this.customMembers != null) {
         Object result = this.customMembers.get(name);
         if (result != null) {
            Deletable.deleteObject(result);
            return;
         }
      }

      Deletable.deleteObject(this.members.get(this, name, this.javaObject, false, cx));
   }

   @Override
   public void delete(Context cx, Symbol key) {
   }

   @Override
   public void delete(Context cx, int index) {
   }

   @Override
   public Scriptable getPrototype(Context cx) {
      return this.prototype == null && this.javaObject instanceof String
         ? TopLevel.getBuiltinPrototype(ScriptableObject.getTopLevelScope(this.parent), TopLevel.Builtins.String, cx)
         : this.prototype;
   }

   @Override
   public void setPrototype(Scriptable m) {
      this.prototype = m;
   }

   @Override
   public Scriptable getParentScope() {
      return this.parent;
   }

   @Override
   public void setParentScope(Scriptable m) {
      this.parent = m;
   }

   @Override
   public Object[] getIds(Context cx) {
      if (this.customMembers != null) {
         Object[] c = this.customMembers.keySet().toArray(ScriptRuntime.EMPTY_OBJECTS);
         Object[] m = this.members.getIds(false);
         Object[] result = new Object[c.length + m.length];
         System.arraycopy(c, 0, result, 0, c.length);
         System.arraycopy(m, 0, result, c.length, m.length);
         return result;
      } else {
         return this.members.getIds(false);
      }
   }

   @Override
   public Object unwrap() {
      return this.javaObject;
   }

   @Override
   public String getClassName() {
      return "JavaObject";
   }

   @Override
   public Object getDefaultValue(Context cx, DefaultValueTypeHint hint) {
      if (hint == null) {
         if (this.javaObject instanceof Boolean) {
            hint = DefaultValueTypeHint.BOOLEAN;
         }

         if (this.javaObject instanceof Number) {
            hint = DefaultValueTypeHint.NUMBER;
         }
      }

      Object value;
      if (hint != null && hint != DefaultValueTypeHint.STRING) {
         String converterName;
         if (hint == DefaultValueTypeHint.BOOLEAN) {
            converterName = "booleanValue";
         } else {
            if (hint != DefaultValueTypeHint.NUMBER) {
               throw Context.reportRuntimeError0("msg.default.value", cx);
            }

            converterName = "doubleValue";
         }

         if (this.get(cx, converterName, this) instanceof Function f) {
            value = f.call(cx, f.getParentScope(), this, ScriptRuntime.EMPTY_OBJECTS);
         } else if (hint == DefaultValueTypeHint.NUMBER && this.javaObject instanceof Boolean) {
            boolean b = (Boolean)this.javaObject;
            value = b ? ScriptRuntime.wrapNumber(1.0) : ScriptRuntime.zeroObj;
         } else {
            value = this.javaObject.toString();
         }
      } else {
         value = this.javaObject.toString();
      }

      return value;
   }

   @Override
   public boolean equals(Object obj) {
      return obj != null && obj.getClass().equals(this.getClass()) && Objects.equals(((NativeJavaObject)obj).javaObject, this.javaObject);
   }

   @Override
   public int hashCode() {
      return this.javaObject == null ? 0 : this.javaObject.hashCode();
   }

   private static final class JavaIterableIterator extends ES6Iterator {
      private static final String ITERATOR_TAG = "JavaIterableIterator";
      private final Iterator<?> iterator;

      static void init(ScriptableObject scope, boolean sealed, Context cx) {
         init(scope, sealed, new NativeJavaObject.JavaIterableIterator(), "JavaIterableIterator", cx);
      }

      private JavaIterableIterator() {
         this.iterator = Collections.emptyIterator();
      }

      JavaIterableIterator(Context cx, Scriptable scope, Iterable<?> iterable) {
         super(scope, "JavaIterableIterator", cx);
         this.iterator = iterable.iterator();
      }

      @Override
      public String getClassName() {
         return "Java Iterable Iterator";
      }

      @Override
      protected boolean isDone(Context cx, Scriptable scope) {
         return !this.iterator.hasNext();
      }

      @Override
      protected Object nextValue(Context cx, Scriptable scope) {
         return !this.iterator.hasNext() ? Undefined.INSTANCE : cx.javaToJS(this.iterator.next(), scope);
      }

      @Override
      protected String getTag() {
         return "JavaIterableIterator";
      }
   }
}
