package dev.isxander.yacl3.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.impl.ConfigCategoryImpl;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public interface ConfigCategory {
   @NotNull
   Component name();

   @NotNull
   ImmutableList<OptionGroup> groups();

   @NotNull
   Component tooltip();

   static ConfigCategory.Builder createBuilder() {
      return new ConfigCategoryImpl.BuilderImpl();
   }

   public interface Builder extends OptionAddable {
      ConfigCategory.Builder name(@NotNull Component var1);

      ConfigCategory.Builder option(@NotNull Option<?> var1);

      default ConfigCategory.Builder option(@NotNull Supplier<Option<?>> optionSupplier) {
         OptionAddable.super.option(optionSupplier);
         return this;
      }

      default ConfigCategory.Builder optionIf(boolean condition, @NotNull Option<?> option) {
         OptionAddable.super.optionIf(condition, option);
         return this;
      }

      default ConfigCategory.Builder optionIf(boolean condition, @NotNull Supplier<Option<?>> optionSupplier) {
         OptionAddable.super.optionIf(condition, optionSupplier);
         return this;
      }

      ConfigCategory.Builder options(@NotNull Collection<? extends Option<?>> var1);

      default ConfigCategory.Builder optionsIf(boolean condition, @NotNull Collection<? extends Option<?>> options) {
         OptionAddable.super.optionsIf(condition, options);
         return this;
      }

      ConfigCategory.Builder group(@NotNull OptionGroup var1);

      default ConfigCategory.Builder group(@NotNull Supplier<OptionGroup> groupSupplier) {
         return this.group(groupSupplier.get());
      }

      default ConfigCategory.Builder groupIf(boolean condition, @NotNull OptionGroup group) {
         return condition ? this.group(group) : this;
      }

      default ConfigCategory.Builder groupIf(boolean condition, @NotNull Supplier<OptionGroup> groupSupplier) {
         return condition ? this.group(groupSupplier) : this;
      }

      ConfigCategory.Builder groups(@NotNull Collection<OptionGroup> var1);

      default ConfigCategory.Builder groupsIf(boolean condition, @NotNull Collection<OptionGroup> groups) {
         return condition ? this.groups(groups) : this;
      }

      OptionGroup.Builder rootGroupBuilder();

      ConfigCategory.Builder tooltip(@NotNull Component... var1);

      ConfigCategory build();
   }
}
