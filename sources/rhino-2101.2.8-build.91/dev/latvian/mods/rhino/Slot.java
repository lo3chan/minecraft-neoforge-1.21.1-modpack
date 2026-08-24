package dev.latvian.mods.rhino;

public class Slot {
   Object name;
   int indexOrHash;
   Object value;
   transient Slot next;
   transient Slot orderedNext;
   private short attributes;

   Slot(Object name, int index, int attributes) {
      this.name = name;
      this.indexOrHash = name == null ? index : name.hashCode();
      this.attributes = (short)attributes;
   }

   protected Slot(Slot oldSlot) {
      this.name = oldSlot.name;
      this.indexOrHash = oldSlot.indexOrHash;
      this.attributes = oldSlot.attributes;
      this.value = oldSlot.value;
      this.next = oldSlot.next;
      this.orderedNext = oldSlot.orderedNext;
   }

   Slot copySlot() {
      Slot newSlot = new Slot(this);
      newSlot.next = null;
      newSlot.orderedNext = null;
      return newSlot;
   }

   boolean isValueSlot() {
      return true;
   }

   boolean isSetterSlot() {
      return false;
   }

   boolean setValue(Object value, Scriptable owner, Scriptable start, Context cx) {
      return this.setValue(value, owner, start, cx, cx.isStrictMode());
   }

   boolean setValue(Object value, Scriptable owner, Scriptable start, Context cx, boolean isThrow) {
      if ((this.attributes & 1) != 0) {
         if (isThrow) {
            throw ScriptRuntime.typeError1(cx, "msg.modify.readonly", this.name);
         } else {
            return true;
         }
      } else if (owner == start) {
         this.value = value;
         return true;
      } else {
         return false;
      }
   }

   Object getValue(Scriptable start, Context cx) {
      return this.value;
   }

   int getAttributes() {
      return this.attributes;
   }

   void setAttributes(int value) {
      ScriptableObject.checkValidAttributes(value);
      this.attributes = (short)value;
   }

   ScriptableObject getPropertyDescriptor(Context cx, Scriptable scope) {
      return ScriptableObject.buildDataDescriptor(scope, this.value, this.attributes, cx);
   }

   protected void throwNoSetterException(Context cx, Scriptable start, Object newValue) {
      String prop = "";
      if (this.name != null) {
         prop = "[" + start.getClassName() + "]." + this.name;
      }

      throw ScriptRuntime.typeError2(cx, "msg.set.prop.no.setter", prop, ScriptRuntime.toString(cx, newValue));
   }

   Function getGetterFunction(Context cx, String name, Scriptable scope) {
      return null;
   }

   Function getSetterFunction(Context cx, String name, Scriptable scope) {
      return null;
   }

   boolean isSameSetterFunction(Context cx, Object function) {
      return false;
   }

   boolean isSameGetterFunction(Context cx, Object function) {
      return false;
   }
}
