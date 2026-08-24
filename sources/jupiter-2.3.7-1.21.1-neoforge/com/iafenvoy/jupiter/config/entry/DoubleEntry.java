package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.RangeConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;

public class DoubleEntry extends BaseEntry<Double> implements RangeConfigEntry<Double> {
   private final double minValue;
   private final double maxValue;
   private final boolean useSlider = false;

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public DoubleEntry(String nameKey, double defaultValue) {
      this(nameKey, defaultValue, -2.147483648E9, 2.147483647E9);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public DoubleEntry(String nameKey, double defaultValue, double minValue, double maxValue) {
      super(nameKey, defaultValue);
      this.minValue = minValue;
      this.maxValue = maxValue;
   }

   protected DoubleEntry(DoubleEntry.Builder builder) {
      super(builder);
      this.minValue = builder.minValue;
      this.maxValue = builder.maxValue;
   }

   @Override
   public ConfigType<Double> getType() {
      return ConfigTypes.DOUBLE;
   }

   @Override
   public ConfigEntry<Double> newInstance() {
      return new DoubleEntry.Builder(this).build();
   }

   @Override
   public Codec<Double> getCodec() {
      return Codec.doubleRange(this.minValue, this.maxValue);
   }

   public Double getMinValue() {
      return this.minValue;
   }

   public Double getMaxValue() {
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
      double d = Double.parseDouble(s);
      if (!(d < this.minValue) && !(d > this.maxValue)) {
         this.setValue(d);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static DoubleEntry.Builder builder(Component name, double defaultValue) {
      return new DoubleEntry.Builder(name, defaultValue);
   }

   public static DoubleEntry.Builder builder(String nameKey, double defaultValue) {
      return new DoubleEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<Double, DoubleEntry, DoubleEntry.Builder> {
      protected double minValue = 5.0E-324;
      protected double maxValue = 1.7976931348623157E308;

      public Builder(Component name, double defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, double defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(DoubleEntry parent) {
         super(parent);
         this.minValue = parent.minValue;
         this.maxValue = parent.maxValue;
      }

      public DoubleEntry.Builder min(double minValue) {
         this.minValue = minValue;
         return this;
      }

      public DoubleEntry.Builder max(double maxValue) {
         this.maxValue = maxValue;
         return this;
      }

      public DoubleEntry.Builder range(double min, double max) {
         this.min(min);
         this.max(max);
         return this;
      }

      public DoubleEntry.Builder self() {
         return this;
      }

      protected DoubleEntry buildInternal() {
         return new DoubleEntry(this);
      }
   }
}
