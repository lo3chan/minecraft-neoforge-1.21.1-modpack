package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigBuilder;
import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.ValueChangeCallback;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.iafenvoy.jupiter.util.TextUtil;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SeparatorEntry implements ConfigEntry<Unit> {
   private Component text = null;
   private Component tooltip;

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public SeparatorEntry() {
   }

   protected SeparatorEntry(SeparatorEntry.Builder builder) {
      this.text = builder.text;
      this.tooltip = builder.tooltip;
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public SeparatorEntry text(@NotNull String textKey) {
      return this.text(TextUtil.translatable(textKey));
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public SeparatorEntry text(@NotNull Component textKey) {
      this.text = textKey;
      return this;
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public SeparatorEntry tooltip(String tooltipKey) {
      return this.tooltip(TextUtil.translatable(tooltipKey));
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public SeparatorEntry tooltip(Component tooltipKey) {
      this.tooltip = tooltipKey;
      return this;
   }

   @Override
   public ConfigType<Unit> getType() {
      return ConfigTypes.SEPARATOR;
   }

   @Nullable
   @Override
   public String getKey() {
      return null;
   }

   @Override
   public Component getName() {
      return this.text;
   }

   @Override
   public Component getTooltip() {
      return this.tooltip;
   }

   @Override
   public ConfigEntry<Unit> newInstance() {
      return new SeparatorEntry.Builder().build();
   }

   @Override
   public void registerCallback(ValueChangeCallback<Unit> callback) {
   }

   public Unit getValue() {
      return Unit.INSTANCE;
   }

   public Unit getDefaultValue() {
      return Unit.INSTANCE;
   }

   public void setValue(Unit value) {
   }

   @Override
   public Codec<Unit> getCodec() {
      return Codec.EMPTY.codec();
   }

   @Override
   public void reset() {
   }

   public static SeparatorEntry.Builder builder() {
      return new SeparatorEntry.Builder();
   }

   public static class Builder implements ConfigBuilder<Unit, SeparatorEntry, SeparatorEntry.Builder> {
      protected Component text;
      @Nullable
      protected Component tooltip;
      protected boolean visible = true;

      public SeparatorEntry.Builder text(String textKey) {
         return this.text(TextUtil.translatable(textKey));
      }

      public SeparatorEntry.Builder text(Component text) {
         this.text = text;
         return this;
      }

      public SeparatorEntry.Builder tooltip(String tooltipKey) {
         return this.tooltip(TextUtil.translatable(tooltipKey));
      }

      public SeparatorEntry.Builder tooltip(Component tooltip) {
         this.tooltip = tooltip;
         return this;
      }

      public SeparatorEntry.Builder callback(ValueChangeCallback<Unit> callback) {
         return this;
      }

      public SeparatorEntry.Builder value(Unit value) {
         return this;
      }

      public SeparatorEntry build() {
         return new SeparatorEntry(this);
      }
   }
}
