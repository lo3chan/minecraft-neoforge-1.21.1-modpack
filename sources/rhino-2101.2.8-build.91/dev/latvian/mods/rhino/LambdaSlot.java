package dev.latvian.mods.rhino;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LambdaSlot extends Slot {
   transient Supplier<Object> getter;
   transient Consumer<Object> setter;

   LambdaSlot(Object name, int index) {
      super(name, index, 0);
   }

   LambdaSlot(Slot oldSlot) {
      super(oldSlot);
   }

   LambdaSlot copySlot() {
      LambdaSlot newSlot = new LambdaSlot(this);
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
      return false;
   }

   @Override
   ScriptableObject getPropertyDescriptor(Context cx, Scriptable scope) {
      ScriptableObject desc = new NativeObject(cx.factory);
      ScriptRuntime.setBuiltinProtoAndParent(cx, scope, desc, TopLevel.Builtins.Object);
      if (this.getter != null) {
         desc.defineProperty(cx, "value", this.getter.get(), 0);
      } else {
         desc.defineProperty(cx, "value", this.value, 0);
      }

      desc.setCommonDescriptorProperties(cx, this.getAttributes(), true);
      return desc;
   }

   @Override
   boolean setValue(Object value, Scriptable owner, Scriptable start, Context cx, boolean isThrow) {
      if (this.setter != null) {
         if (owner == start) {
            this.setter.accept(value);
            return true;
         } else {
            return false;
         }
      } else {
         return super.setValue(value, owner, start, cx, isThrow);
      }
   }

   @Override
   Object getValue(Scriptable start, Context cx) {
      return this.getter != null ? this.getter.get() : super.getValue(start, cx);
   }
}
