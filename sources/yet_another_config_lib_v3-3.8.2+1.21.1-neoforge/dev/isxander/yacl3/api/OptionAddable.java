package dev.isxander.yacl3.api;

import java.util.Collection;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public interface OptionAddable {
   OptionAddable option(@NotNull Option<?> var1);

   default OptionAddable option(@NotNull Supplier<Option<?>> optionSupplier) {
      return this.option(optionSupplier.get());
   }

   default OptionAddable optionIf(boolean condition, @NotNull Option<?> option) {
      return condition ? this.option(option) : this;
   }

   default OptionAddable optionIf(boolean condition, @NotNull Supplier<Option<?>> optionSupplier) {
      return condition ? this.option(optionSupplier) : this;
   }

   OptionAddable options(@NotNull Collection<? extends Option<?>> var1);

   default OptionAddable optionsIf(boolean condition, @NotNull Collection<? extends Option<?>> options) {
      return condition ? this.options(options) : this;
   }
}
