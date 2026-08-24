package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class OptionGroupImpl implements OptionGroup {
   @NotNull
   private final Component name;
   @NotNull
   private final OptionDescription description;
   private final ImmutableList<? extends Option<?>> options;
   private final boolean collapsed;
   private final boolean isRoot;

   public OptionGroupImpl(
      @NotNull Component name, @NotNull OptionDescription description, ImmutableList<? extends Option<?>> options, boolean collapsed, boolean isRoot
   ) {
      this.name = name;
      this.description = description;
      this.options = options;
      this.collapsed = collapsed;
      this.isRoot = isRoot;
   }

   @NotNull
   @Override
   public Component name() {
      return this.name;
   }

   @Override
   public OptionDescription description() {
      return this.description;
   }

   @NotNull
   @Override
   public Component tooltip() {
      return this.description.text();
   }

   @NotNull
   @Override
   public ImmutableList<? extends Option<?>> options() {
      return this.options;
   }

   @Override
   public boolean collapsed() {
      return this.collapsed;
   }

   @Override
   public boolean isRoot() {
      return this.isRoot;
   }

   @Internal
   public static final class BuilderImpl implements OptionGroup.Builder {
      private Component name = Component.empty();
      private OptionDescription description = OptionDescription.EMPTY;
      private final List<Option<?>> options = new ArrayList<>();
      private boolean collapsed = false;

      @Override
      public OptionGroup.Builder name(@NotNull Component name) {
         Validate.notNull(name, "`name` must not be null", new Object[0]);
         this.name = name;
         return this;
      }

      @Override
      public OptionGroup.Builder description(@NotNull OptionDescription description) {
         Validate.notNull(description, "`description` must not be null", new Object[0]);
         this.description = description;
         return this;
      }

      @Override
      public OptionGroup.Builder option(@NotNull Option<?> option) {
         Validate.notNull(option, "`option` must not be null", new Object[0]);
         if (option instanceof ListOption) {
            throw new UnsupportedOperationException("List options must not be added as an option but a group!");
         } else {
            this.options.add(option);
            return this;
         }
      }

      @Override
      public OptionGroup.Builder options(@NotNull Collection<? extends Option<?>> options) {
         Validate.notEmpty(options, "`options` must not be empty", new Object[0]);
         if (options.stream().anyMatch(ListOption.class::isInstance)) {
            throw new UnsupportedOperationException("List options must not be added as an option but a group!");
         } else {
            this.options.addAll(options);
            return this;
         }
      }

      @Override
      public OptionGroup.Builder collapsed(boolean collapsible) {
         this.collapsed = collapsible;
         return this;
      }

      @Override
      public OptionGroup build() {
         Validate.notEmpty(this.options, "`options` must not be empty to build `OptionGroup`", new Object[0]);
         return new OptionGroupImpl(this.name, this.description, ImmutableList.copyOf(this.options), this.collapsed, false);
      }
   }
}
