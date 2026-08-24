package dev.isxander.yacl3.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.impl.OptionGroupImpl;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public interface OptionGroup {
   Component name();

   OptionDescription description();

   @Deprecated
   Component tooltip();

   @NotNull
   ImmutableList<? extends Option<?>> options();

   boolean collapsed();

   boolean isRoot();

   static OptionGroup.Builder createBuilder() {
      return new OptionGroupImpl.BuilderImpl();
   }

   public interface Builder extends OptionAddable {
      OptionGroup.Builder name(@NotNull Component var1);

      OptionGroup.Builder description(@NotNull OptionDescription var1);

      OptionGroup.Builder option(@NotNull Option<?> var1);

      default OptionGroup.Builder option(@NotNull Supplier<Option<?>> optionSupplier) {
         OptionAddable.super.option(optionSupplier);
         return this;
      }

      default OptionGroup.Builder optionIf(boolean condition, @NotNull Option<?> option) {
         OptionAddable.super.optionIf(condition, option);
         return this;
      }

      default OptionGroup.Builder optionIf(boolean condition, @NotNull Supplier<Option<?>> optionSupplier) {
         OptionAddable.super.optionIf(condition, optionSupplier);
         return this;
      }

      OptionGroup.Builder options(@NotNull Collection<? extends Option<?>> var1);

      default OptionGroup.Builder optionsIf(boolean condition, @NotNull Collection<? extends Option<?>> options) {
         OptionAddable.super.optionsIf(condition, options);
         return this;
      }

      OptionGroup.Builder collapsed(boolean var1);

      OptionGroup build();
   }
}
