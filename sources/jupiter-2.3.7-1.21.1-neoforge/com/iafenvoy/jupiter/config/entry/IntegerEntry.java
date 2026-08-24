package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.RangeConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;

public class IntegerEntry extends BaseEntry<Integer> implements RangeConfigEntry<Integer> {
   private final int minValue;
   private final int maxValue;
   private final boolean useSlider = false;

   protected IntegerEntry(IntegerEntry.Builder builder) {
      super(builder);
      this.minValue = builder.minValue;
      this.maxValue = builder.maxValue;
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public IntegerEntry(String nameKey, int defaultValue) {
      this(nameKey, defaultValue, -2147483648, 2147483647);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public IntegerEntry(String nameKey, int defaultValue, int minValue, int maxValue) {
      super(nameKey, defaultValue);
      this.minValue = minValue;
      this.maxValue = maxValue;
   }

   @Override
   public ConfigType<Integer> getType() {
      return ConfigTypes.INTEGER;
   }

   @Override
   public ConfigEntry<Integer> newInstance() {
      return new IntegerEntry.Builder(this).build();
   }

   @Override
   public Codec<Integer> getCodec() {
      return Codec.intRange(this.minValue, this.maxValue);
   }

   public Integer getMinValue() {
      return this.minValue;
   }

   public Integer getMaxValue() {
      return this.maxValue;
   }

   @Override
   public boolean useSlider() {
      return false;
   }

   @Override
   public String valueAsString() {
      return String.valueOf(this.getValue());
   }

   @Override
   public void setValueFromString(String s) {
      int d = Integer.parseInt(s);
      if (d >= this.minValue && d <= this.maxValue) {
         this.setValue(d);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static IntegerEntry.Builder builder(Component name, int defaultValue) {
      return new IntegerEntry.Builder(name, defaultValue);
   }

   public static IntegerEntry.Builder builder(String nameKey, int defaultValue) {
      return new IntegerEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<Integer, IntegerEntry, IntegerEntry.Builder> {
      protected int minValue = -2147483648;
      protected int maxValue = 2147483647;

      public Builder(Component name, int defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, int defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(IntegerEntry parent) {
         super(parent);
         this.minValue = parent.minValue;
         this.maxValue = parent.maxValue;
      }

      public IntegerEntry.Builder min(int minValue) {
         this.minValue = minValue;
         return this;
      }

      public IntegerEntry.Builder max(int maxValue) {
         this.maxValue = maxValue;
         return this;
      }

      public IntegerEntry.Builder range(int min, int max) {
         this.min(min);
         this.max(max);
         return this;
      }

      public IntegerEntry.Builder self() {
         return this;
      }

      protected IntegerEntry buildInternal() {
         return new IntegerEntry(this);
      }
   }
}
