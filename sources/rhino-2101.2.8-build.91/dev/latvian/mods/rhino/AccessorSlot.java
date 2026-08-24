package dev.latvian.mods.rhino;

import java.util.List;

public class AccessorSlot extends Slot {
   transient AccessorSlot.Getter getter;
   transient AccessorSlot.Setter setter;

   AccessorSlot(Object name, int index) {
      super(name, index, 0);
   }

   AccessorSlot(Slot oldSlot) {
      super(oldSlot);
   }

   AccessorSlot copySlot() {
      AccessorSlot newSlot = new AccessorSlot(this);
      newSlot.value = this.value;
      newSlot.getter = this.getter;
      newSlot.setter = this.setter;
      newSlot.next = null;
      newSlot.orderedNext = null;
      return newSlot;
   }

   @Override
   boolean isValueSlot() {
      return false;
   }

   @Override
   boolean isSetterSlot() {
      return true;
   }

   @Override
   ScriptableObject getPropertyDescriptor(Context cx, Scriptable scope) {
      ScriptableObject desc = new NativeObject(cx.factory);
      ScriptRuntime.setBuiltinProtoAndParent(cx, scope, desc, TopLevel.Builtins.Object);
      desc.setCommonDescriptorProperties(cx, this.getAttributes(), this.getter == null && this.setter == null);
      String fName = this.name == null ? "f" : this.name.toString();
      if (this.getter != null) {
         Function f = this.getter.asGetterFunction(cx, fName, scope);
         desc.defineProperty(cx, "get", f == null ? Undefined.INSTANCE : f, 0);
      }

      if (this.setter != null) {
         Function f = this.setter.asSetterFunction(cx, fName, scope);
         desc.defineProperty(cx, "set", f == null ? Undefined.INSTANCE : f, 0);
      }

      return desc;
   }

   @Override
   boolean setValue(Object value, Scriptable owner, Scriptable start, Context cx, boolean isThrow) {
      if (this.setter == null) {
         if (this.getter != null) {
            if (isThrow) {
               this.throwNoSetterException(cx, start, value);
            }

            return true;
         } else {
            return super.setValue(value, owner, start, cx, isThrow);
         }
      } else {
         return this.setter.setValue(value, owner, start, cx);
      }
   }

   @Override
   Object getValue(Scriptable start, Context cx) {
      return this.getter != null ? this.getter.getValue(start, cx) : super.getValue(start, cx);
   }

   @Override
   Function getGetterFunction(Context cx, String name, Scriptable scope) {
      return this.getter == null ? null : this.getter.asGetterFunction(cx, name, scope);
   }

   @Override
   Function getSetterFunction(Context cx, String name, Scriptable scope) {
      return this.setter == null ? null : this.setter.asSetterFunction(cx, name, scope);
   }

   @Override
   boolean isSameGetterFunction(Context cx, Object function) {
      if (function == Scriptable.NOT_FOUND) {
         return true;
      } else {
         return this.getter == null ? ScriptRuntime.shallowEq(cx, Undefined.INSTANCE, function) : this.getter.isSameGetterFunction(cx, function);
      }
   }

   @Override
   boolean isSameSetterFunction(Context cx, Object function) {
      if (function == Scriptable.NOT_FOUND) {
         return true;
      } else {
         return this.setter == null ? ScriptRuntime.shallowEq(cx, Undefined.INSTANCE, function) : this.setter.isSameSetterFunction(cx, function);
      }
   }

   static final class FunctionGetter extends AccessorSlot.Getter {
      final Object target;

      FunctionGetter(Object target) {
         this.target = target;
      }

      @Override
      Object getValue(Scriptable start, Context cx) {
         return this.target instanceof Function t ? t.call(cx, t.getParentScope(), start, ScriptRuntime.EMPTY_OBJECTS) : Undefined.INSTANCE;
      }

      @Override
      Function asGetterFunction(Context cx, String name, Scriptable scope) {
         return this.target instanceof Function ? (Function)this.target : null;
      }

      @Override
      boolean isSameGetterFunction(Context cx, Object function) {
         return ScriptRuntime.shallowEq(cx, this.target instanceof Function ? (Function)this.target : Undefined.INSTANCE, function);
      }
   }

   static final class FunctionSetter extends AccessorSlot.Setter {
      final Object target;

      FunctionSetter(Object target) {
         this.target = target;
      }

      @Override
      boolean setValue(Object value, Scriptable owner, Scriptable start, Context cx) {
         if (this.target instanceof Function t) {
            t.call(cx, t.getParentScope(), start, new Object[]{value});
         }

         return true;
      }

      @Override
      Function asSetterFunction(Context cx, String name, Scriptable scope) {
         return this.target instanceof Function ? (Function)this.target : null;
      }

      @Override
      boolean isSameSetterFunction(Context cx, Object function) {
         return ScriptRuntime.shallowEq(cx, this.target instanceof Function ? (Function)this.target : Undefined.INSTANCE, function);
      }
   }

   abstract static class Getter {
      abstract Object getValue(Scriptable var1, Context var2);

      abstract Function asGetterFunction(Context var1, String var2, Scriptable var3);

      abstract boolean isSameGetterFunction(Context var1, Object var2);
   }

   static final class MemberBoxGetter extends AccessorSlot.Getter {
      final MemberBox member;

      MemberBoxGetter(MemberBox member) {
         this.member = member;
      }

      @Override
      Object getValue(Scriptable start, Context cx) {
         Object getterThis;
         Object[] args;
         if (this.member.delegateTo == null) {
            getterThis = start;
            args = ScriptRuntime.EMPTY_OBJECTS;
         } else {
            getterThis = this.member.delegateTo;
            args = new Object[]{cx, start};
         }

         return this.member.invoke(getterThis, args, cx, start);
      }

      @Override
      Function asGetterFunction(Context cx, String name, Scriptable scope) {
         return this.member.asGetterFunction(cx, name, scope);
      }

      @Override
      boolean isSameGetterFunction(Context cx, Object function) {
         return this.member.isSameGetterFunction(cx, function);
      }
   }

   static final class MemberBoxSetter extends AccessorSlot.Setter {
      final MemberBox member;

      MemberBoxSetter(MemberBox member) {
         this.member = member;
      }

      @Override
      boolean setValue(Object value, Scriptable owner, Scriptable start, Context cx) {
         List<Class<?>> pTypes = this.member.parameters().types();
         Class<?> valueType = (Class<?>)pTypes.getLast();
         int tag = FunctionObject.getTypeTag(valueType);
         Object actualArg = FunctionObject.convertArg(cx, start, value, tag);
         if (this.member.delegateTo == null) {
            this.member.invoke(start, new Object[]{actualArg}, cx, start);
         } else {
            this.member.invoke(this.member.delegateTo, new Object[]{cx, start, actualArg}, cx, start);
         }

         return true;
      }

      @Override
      Function asSetterFunction(Context cx, String name, Scriptable scope) {
         return this.member.asSetterFunction(cx, name, scope);
      }

      @Override
      boolean isSameSetterFunction(Context cx, Object function) {
         return this.member.isSameSetterFunction(cx, function);
      }
   }

   abstract static class Setter {
      abstract boolean setValue(Object var1, Scriptable var2, Scriptable var3, Context var4);

      abstract Function asSetterFunction(Context var1, String var2, Scriptable var3);

      abstract boolean isSameSetterFunction(Context var1, Object var2);
   }
}
