package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.Binding;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

public class NotNullBinding<T> implements Binding<T> {
   private final Binding<T> binding;

   public NotNullBinding(Binding<T> binding) {
      this.binding = binding;
   }

   @NotNull
   @Override
   public T getValue() {
      return (T)Validate.notNull(this.binding.getValue(), "Binding's value must not be null, please use Optionals if you want null behaviour.", new Object[0]);
   }

   @Override
   public void setValue(@NotNull T value) {
      Validate.notNull(value, "Binding's value must not be set to null, please use Optionals if you want null behaviour.", new Object[0]);
      this.binding.setValue(value);
   }

   @NotNull
   @Override
   public T defaultValue() {
      return (T)Validate.notNull(
         this.binding.defaultValue(), "Binding's default value must not be null, please use Optionals if you want null behaviour.", new Object[0]
      );
   }
}
