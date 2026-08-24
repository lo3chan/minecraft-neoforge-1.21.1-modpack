package dev.isxander.yacl3.config.v2.impl;

import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.config.v2.api.FieldAccess;
import dev.isxander.yacl3.config.v2.api.ReadOnlyFieldAccess;

public record FieldBackedBinding<T>(FieldAccess<T> field, ReadOnlyFieldAccess<T> defaultField) implements Binding<T> {
   @Override
   public T getValue() {
      return this.field.get();
   }

   @Override
   public void setValue(T value) {
      this.field.set(value);
   }

   @Override
   public T defaultValue() {
      return this.defaultField.get();
   }
}
