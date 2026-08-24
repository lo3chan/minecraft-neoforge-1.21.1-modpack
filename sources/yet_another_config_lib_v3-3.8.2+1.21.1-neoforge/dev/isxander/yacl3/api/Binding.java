package dev.isxander.yacl3.api;

import dev.isxander.yacl3.impl.GenericBindingImpl;
import dev.isxander.yacl3.mixin.OptionInstanceAccessor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.OptionInstance;
import org.apache.commons.lang3.Validate;

public interface Binding<T> {
   void setValue(T var1);

   T getValue();

   T defaultValue();

   default <U> Binding<U> xmap(Function<T, U> to, Function<U, T> from) {
      return generic(to.apply(this.defaultValue()), () -> to.apply(this.getValue()), v -> this.setValue(from.apply(v)));
   }

   static <T> Binding<T> generic(T def, Supplier<T> getter, Consumer<T> setter) {
      Validate.notNull(def, "`def` must not be null", new Object[0]);
      Validate.notNull(getter, "`getter` must not be null", new Object[0]);
      Validate.notNull(setter, "`setter` must not be null", new Object[0]);
      return new GenericBindingImpl<>(def, getter, setter);
   }

   static <T> Binding<T> minecraft(OptionInstance<T> minecraftOption) {
      Validate.notNull(minecraftOption, "`minecraftOption` must not be null", new Object[0]);
      return new GenericBindingImpl<>((T)((OptionInstanceAccessor)minecraftOption).getInitialValue(), minecraftOption::get, minecraftOption::set);
   }

   static <T> Binding<T> immutable(T value) {
      Validate.notNull(value, "`value` must not be null", new Object[0]);
      return new GenericBindingImpl<>(value, () -> value, changed -> {});
   }
}
