package dev.latvian.mods.rhino;

public class BuiltInSlot<T extends ScriptableObject> extends Slot {
   private final BuiltInSlot.Getter<T> getter;
   private final BuiltInSlot.Setter<T> setter;
   private final BuiltInSlot.AttributeSetter<T> attrUpdater;
   private final BuiltInSlot.PropDescriptionSetter<T> propDescSetter;

   BuiltInSlot(
      Object name, int index, int attr, T builtIn, BuiltInSlot.Getter<T> getter, BuiltInSlot.Setter<T> setter, BuiltInSlot.AttributeSetter<T> attrUpdater
   ) {
      this(name, index, attr, builtIn, getter, setter, attrUpdater, BuiltInSlot::defaultPropDescSetter);
   }

   BuiltInSlot(
      Object name,
      int index,
      int attr,
      T builtIn,
      BuiltInSlot.Getter<T> getter,
      BuiltInSlot.Setter<T> setter,
      BuiltInSlot.AttributeSetter<T> attrUpdater,
      BuiltInSlot.PropDescriptionSetter<T> propDescSetter
   ) {
      super(name, index, attr);
      this.value = builtIn;
      this.getter = getter;
      this.setter = setter;
      this.attrUpdater = attrUpdater;
      this.propDescSetter = propDescSetter;
   }

   BuiltInSlot(BuiltInSlot<T> slot) {
      super(slot);
      this.getter = slot.getter;
      this.setter = slot.setter;
      this.attrUpdater = slot.attrUpdater;
      this.propDescSetter = slot.propDescSetter;
   }

   @Override
   Slot copySlot() {
      BuiltInSlot<T> res = new BuiltInSlot<>(this);
      res.next = null;
      res.orderedNext = null;
      return res;
   }

   @Override
   boolean isValueSlot() {
      return false;
   }

   @Override
   Object getValue(Scriptable start, Context cx) {
      return this.getter.apply((T)this.value, start, cx);
   }

   @Override
   boolean setValue(Object value, Scriptable owner, Scriptable start, Context cx, boolean isThrow) {
      return this.setter.apply((T)this.value, value, owner, start, isThrow, cx);
   }

   @Override
   void setAttributes(int value) {
      this.attrUpdater.apply((T)this.value, value);
      super.setAttributes(value);
   }

   @Override
   ScriptableObject getPropertyDescriptor(Context cx, Scriptable scope) {
      return ScriptableObject.buildDataDescriptor(scope, this.getValue((ScriptableObject)this.value, cx), this.getAttributes(), cx);
   }

   void applyNewDescriptor(Object id, ScriptableObject desc, boolean checkValid, Object key, int index, Context cx) {
      this.propDescSetter.apply((T)this.value, this, id, desc, checkValid, key, index, cx);
   }

   private static <T extends ScriptableObject> void defaultPropDescSetter(
      T builtIn, BuiltInSlot<T> current, Object id, ScriptableObject desc, boolean checkValid, Object key, int index, Context cx
   ) {
      builtIn.defineOrdinaryProperty(cx, id, desc, checkValid, key, index);
   }

   public interface AttributeSetter<U extends ScriptableObject> {
      void apply(U var1, int var2);
   }

   public interface Getter<U extends ScriptableObject> {
      Object apply(U var1, Scriptable var2, Context var3);
   }

   public interface PropDescriptionSetter<U extends ScriptableObject> {
      void apply(U var1, BuiltInSlot<U> var2, Object var3, ScriptableObject var4, boolean var5, Object var6, int var7, Context var8);
   }

   public interface Setter<U extends ScriptableObject> {
      boolean apply(U var1, Object var2, Scriptable var3, Scriptable var4, boolean var5, Context var6);
   }
}
