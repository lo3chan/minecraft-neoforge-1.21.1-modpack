package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.DefaultValueTypeHint;
import java.lang.reflect.Array;
import java.util.Objects;

public class NativeJavaArray extends NativeJavaObject implements SymbolScriptable {
   Object array;
   int length;
   TypeInfo componentType;

   public NativeJavaArray(Scriptable scope, Object array, TypeInfo type, Context cx) {
      super(scope, null, type, cx);
      this.array = array;
      this.length = Array.getLength(array);
      this.componentType = type.componentType();
   }

   @Override
   public String getClassName() {
      return "JavaArray";
   }

   @Override
   public Object unwrap() {
      return this.array;
   }

   @Override
   public boolean has(Context cx, String id, Scriptable start) {
      return id.equals("length") || super.has(cx, id, start);
   }

   @Override
   public boolean has(Context cx, int index, Scriptable start) {
      return 0 <= index && index < this.length;
   }

   @Override
   public boolean has(Context cx, Symbol key, Scriptable start) {
      return SymbolKey.IS_CONCAT_SPREADABLE.equals(key) || super.has(cx, key, start);
   }

   @Override
   public Object get(Context cx, String id, Scriptable start) {
      if (id.equals("length")) {
         return this.length;
      } else {
         Object result = super.get(cx, id, start);
         if (result == NOT_FOUND && !ScriptableObject.hasProperty(this.getPrototype(cx), id, cx)) {
            throw Context.reportRuntimeError2("msg.java.member.not.found", this.array.getClass().getName(), id, cx);
         } else {
            return result;
         }
      }
   }

   @Override
   public Object get(Context cx, int index, Scriptable start) {
      if (0 <= index && index < this.length) {
         Object obj = Array.get(this.array, index);
         return cx.wrap(this, obj, this.componentType);
      } else {
         return Undefined.INSTANCE;
      }
   }

   @Override
   public Object get(Context cx, Symbol key, Scriptable start) {
      return SymbolKey.IS_CONCAT_SPREADABLE.equals(key) ? Boolean.TRUE : super.get(cx, key, start);
   }

   @Override
   public void put(Context cx, String id, Scriptable start, Object value) {
      if (!id.equals("length")) {
         throw Context.reportRuntimeError1("msg.java.array.member.not.found", id, cx);
      }
   }

   @Override
   public void put(Context cx, int index, Scriptable start, Object value) {
      if (0 <= index && index < this.length) {
         Array.set(this.array, index, cx.jsToJava(value, this.componentType));
      } else {
         throw Context.reportRuntimeError2("msg.java.array.index.out.of.bounds", String.valueOf(index), String.valueOf(this.length - 1), cx);
      }
   }

   @Override
   public void delete(Context cx, Symbol key) {
   }

   @Override
   public Object getDefaultValue(Context cx, DefaultValueTypeHint hint) {
      if (hint == null || hint == DefaultValueTypeHint.STRING) {
         return this.array.toString();
      } else if (hint == DefaultValueTypeHint.BOOLEAN) {
         return Boolean.TRUE;
      } else {
         return hint == DefaultValueTypeHint.NUMBER ? ScriptRuntime.NaNobj : this;
      }
   }

   @Override
   public Object[] getIds(Context cx) {
      Object[] result = new Object[this.length];
      int i = this.length;

      while (--i >= 0) {
         result[i] = i;
      }

      return result;
   }

   @Override
   public boolean hasInstance(Context cx, Scriptable value) {
      if (!(value instanceof Wrapper)) {
         return false;
      } else {
         Object instance = ((Wrapper)value).unwrap();
         return this.componentType.asClass().isInstance(instance);
      }
   }

   @Override
   public Scriptable getPrototype(Context cx) {
      if (this.prototype == null) {
         this.prototype = ScriptableObject.getArrayPrototype(this.getParentScope(), cx);
      }

      return this.prototype;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof NativeJavaArray other && Objects.equals(other.array, this.array);
   }

   @Override
   public int hashCode() {
      return this.array == null ? 0 : this.array.hashCode();
   }
}
