package dev.latvian.mods.rhino;

import java.util.function.BiConsumer;

public class LambdaAccessorSlot extends Slot {
   transient java.util.function.Function<Scriptable, Object> getter;
   transient BiConsumer<Scriptable, Object> setter;
   private LambdaFunction getterFunction;
   private LambdaFunction setterFunction;

   LambdaAccessorSlot(Object name, int index) {
      super(name, index, 0);
   }

   LambdaAccessorSlot(Slot oldSlot) {
      super(oldSlot);
   }

   LambdaAccessorSlot copySlot() {
      LambdaAccessorSlot newSlot = new LambdaAccessorSlot(this);
      newSlot.value = this.value;
      newSlot.getter = this.getter;
      newSlot.setter = this.setter;
      newSlot.getterFunction = this.getterFunction;
      newSlot.setterFunction = this.setterFunction;
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
      return this.buildPropertyDescriptor(cx);
   }

   public ScriptableObject buildPropertyDescriptor(Context cx) {
      ScriptableObject desc = new NativeObject(cx.factory);
      desc.setCommonDescriptorProperties(cx, this.getAttributes(), this.getterFunction == null && this.setterFunction == null);
      if (this.getterFunction != null) {
         desc.defineProperty(cx, "get", this.getterFunction, 0);
      }

      if (this.setterFunction != null) {
         desc.defineProperty(cx, "set", this.setterFunction, 0);
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
         this.setter.accept(start, value);
         return true;
      }
   }

   @Override
   Object getValue(Scriptable start, Context cx) {
      return this.getter != null ? this.getter.apply(start) : super.getValue(start, cx);
   }

   public void setGetter(Context cx, Scriptable scope, java.util.function.Function<Scriptable, Object> getter) {
      this.getter = getter;
      if (getter != null) {
         this.getterFunction = new LambdaFunction(cx, scope, "get " + this.name, 0, (cx1, scope1, thisObj, args) -> getter.apply(thisObj), false);
      }
   }

   public void setSetter(Context cx, Scriptable scope, BiConsumer<Scriptable, Object> setter) {
      this.setter = setter;
      if (setter != null) {
         this.setterFunction = new LambdaFunction(cx, scope, "set " + this.name, 1, (cx1, scope1, thisObj, args) -> {
            setter.accept(thisObj, args[0]);
            return Undefined.INSTANCE;
         }, false);
      }
   }

   public void replaceWith(LambdaAccessorSlot slot) {
      this.getterFunction = slot.getterFunction;
      this.getter = slot.getter;
      this.setterFunction = slot.setterFunction;
      this.setter = slot.setter;
      this.setAttributes(slot.getAttributes());
   }
}
