package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ConfigCategoryImpl implements ConfigCategory {
   private final Component name;
   private final ImmutableList<OptionGroup> groups;
   private final Component tooltip;

   public ConfigCategoryImpl(Component name, ImmutableList<OptionGroup> groups, Component tooltip) {
      this.name = name;
      this.groups = groups;
      this.tooltip = tooltip;
   }

   @NotNull
   @Override
   public Component name() {
      return this.name;
   }

   @NotNull
   @Override
   public ImmutableList<OptionGroup> groups() {
      return this.groups;
   }

   @NotNull
   @Override
   public Component tooltip() {
      return this.tooltip;
   }

   @Internal
   public static final class BuilderImpl implements ConfigCategory.Builder {
      private Component name;
      private final List<Option<?>> rootOptions = new ArrayList<>();
      private final ConfigCategoryImpl.BuilderImpl.RootGroupBuilder rootGroupBuilder = new ConfigCategoryImpl.BuilderImpl.RootGroupBuilder();
      private final List<OptionGroup> groups = new ArrayList<>();
      private final List<Component> tooltipLines = new ArrayList<>();

      @Override
      public ConfigCategory.Builder name(@NotNull Component name) {
         Validate.notNull(name, "`name` cannot be null", new Object[0]);
         this.name = name;
         return this;
      }

      @Override
      public ConfigCategory.Builder option(@NotNull Option<?> option) {
         Validate.notNull(option, "`option` must not be null", new Object[0]);
         if (option instanceof ListOption<?> listOption) {
            YACLConstants.LOGGER.warn("Adding list option as an option is not supported! Rerouting to group!");
            return this.group(listOption);
         } else {
            this.rootOptions.add(option);
            return this;
         }
      }

      @Override
      public ConfigCategory.Builder options(@NotNull Collection<? extends Option<?>> options) {
         Validate.notNull(options, "`options` must not be null", new Object[0]);
         if (options.stream().anyMatch(ListOption.class::isInstance)) {
            throw new UnsupportedOperationException("List options must not be added as an option but a group!");
         } else {
            this.rootOptions.addAll(options);
            return this;
         }
      }

      @Override
      public ConfigCategory.Builder group(@NotNull OptionGroup group) {
         Validate.notNull(group, "`group` must not be null", new Object[0]);
         this.groups.add(group);
         return this;
      }

      @Override
      public ConfigCategory.Builder groups(@NotNull Collection<OptionGroup> groups) {
         Validate.notEmpty(groups, "`groups` must not be empty", new Object[0]);
         this.groups.addAll(groups);
         return this;
      }

      @Override
      public ConfigCategory.Builder tooltip(@NotNull Component... tooltips) {
         Validate.notEmpty(tooltips, "`tooltips` cannot be empty", new Object[0]);
         this.tooltipLines.addAll(List.of(tooltips));
         return this;
      }

      @Override
      public OptionGroup.Builder rootGroupBuilder() {
         return this.rootGroupBuilder;
      }

      @Override
      public ConfigCategory build() {
         Validate.notNull(this.name, "`name` must not be null to build `ConfigCategory`", new Object[0]);
         List<OptionGroup> combinedGroups = new ArrayList<>();
         combinedGroups.add(new OptionGroupImpl(CommonComponents.EMPTY, OptionDescription.EMPTY, ImmutableList.copyOf(this.rootOptions), false, true));
         combinedGroups.addAll(this.groups);
         Validate.notEmpty(combinedGroups, "at least one option must be added to build `ConfigCategory`", new Object[0]);
         MutableComponent concatenatedTooltip = Component.empty();
         boolean first = true;

         for (Component line : this.tooltipLines) {
            if (line.getContents() != CommonComponents.EMPTY.getContents()) {
               if (!first) {
                  concatenatedTooltip.append("\n");
               }

               first = false;
               concatenatedTooltip.append(line);
            }
         }

         return new ConfigCategoryImpl(this.name, ImmutableList.copyOf(combinedGroups), concatenatedTooltip);
      }

      private class RootGroupBuilder implements OptionGroup.Builder {
         @Override
         public OptionGroup.Builder name(@NotNull Component name) {
            throw new UnsupportedOperationException("Cannot set name of root group!");
         }

         @Override
         public OptionGroup.Builder description(@NotNull OptionDescription description) {
            throw new UnsupportedOperationException("Cannot set name of root group!");
         }

         @Override
         public OptionGroup.Builder option(@NotNull Option<?> option) {
            BuilderImpl.this.option(option);
            return this;
         }

         @Override
         public OptionGroup.Builder options(@NotNull Collection<? extends Option<?>> options) {
            BuilderImpl.this.options(options);
            return this;
         }

         @Override
         public OptionGroup.Builder collapsed(boolean collapsible) {
            throw new UnsupportedOperationException("Cannot set collapsible of root group!");
         }

         @Override
         public OptionGroup build() {
            throw new UnsupportedOperationException("Cannot build root group!");
         }
      }
   }
}
