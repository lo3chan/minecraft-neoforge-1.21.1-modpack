package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.PlaceholderCategory;
import dev.isxander.yacl3.gui.YACLScreen;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class PlaceholderCategoryImpl implements PlaceholderCategory {
   private final Component name;
   private final BiFunction<Minecraft, YACLScreen, Screen> screen;
   private final Component tooltip;

   public PlaceholderCategoryImpl(Component name, BiFunction<Minecraft, YACLScreen, Screen> screen, Component tooltip) {
      this.name = name;
      this.screen = screen;
      this.tooltip = tooltip;
   }

   @NotNull
   @Override
   public ImmutableList<OptionGroup> groups() {
      return ImmutableList.of();
   }

   @NotNull
   @Override
   public Component name() {
      return this.name;
   }

   @Override
   public BiFunction<Minecraft, YACLScreen, Screen> screen() {
      return this.screen;
   }

   @NotNull
   @Override
   public Component tooltip() {
      return this.tooltip;
   }

   @Internal
   public static final class BuilderImpl implements PlaceholderCategory.Builder {
      private Component name;
      private final List<Component> tooltipLines = new ArrayList<>();
      private BiFunction<Minecraft, YACLScreen, Screen> screenFunction;

      @Override
      public PlaceholderCategory.Builder name(@NotNull Component name) {
         Validate.notNull(name, "`name` cannot be null", new Object[0]);
         this.name = name;
         return this;
      }

      @Override
      public PlaceholderCategory.Builder tooltip(@NotNull Component... tooltips) {
         Validate.notEmpty(tooltips, "`tooltips` cannot be empty", new Object[0]);
         this.tooltipLines.addAll(List.of(tooltips));
         return this;
      }

      @Override
      public PlaceholderCategory.Builder screen(@NotNull BiFunction<Minecraft, YACLScreen, Screen> screenFunction) {
         Validate.notNull(screenFunction, "`screenFunction` cannot be null", new Object[0]);
         this.screenFunction = screenFunction;
         return this;
      }

      @Override
      public PlaceholderCategory build() {
         Validate.notNull(this.name, "`name` must not be null to build `ConfigCategory`", new Object[0]);
         MutableComponent concatenatedTooltip = Component.empty();
         boolean first = true;

         for (Component line : this.tooltipLines) {
            if (!first) {
               concatenatedTooltip.append("\n");
            }

            first = false;
            concatenatedTooltip.append(line);
         }

         return new PlaceholderCategoryImpl(this.name, this.screenFunction, concatenatedTooltip);
      }
   }
}
