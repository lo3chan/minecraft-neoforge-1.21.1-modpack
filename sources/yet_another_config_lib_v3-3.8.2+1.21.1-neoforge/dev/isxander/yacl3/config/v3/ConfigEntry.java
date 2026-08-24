package dev.isxander.yacl3.config.v3;

import dev.isxander.yacl3.api.Binding;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface ConfigEntry<T> extends ReadonlyConfigEntry<T> {
   void set(T var1);

   T defaultValue();

   ConfigEntry<T> modifyGet(UnaryOperator<T> var1);

   default ConfigEntry<T> onGet(Consumer<T> consumer) {
      return this.modifyGet(v -> {
         consumer.accept((T)v);
         return v;
      });
   }

   ConfigEntry<T> modifySet(UnaryOperator<T> var1);

   default ConfigEntry<T> onSet(Consumer<T> consumer) {
      return this.modifySet(v -> {
         consumer.accept((T)v);
         return v;
      });
   }

   default Binding<T> asBinding() {
      return Binding.generic(this.defaultValue(), this::get, this::set);
   }
}
