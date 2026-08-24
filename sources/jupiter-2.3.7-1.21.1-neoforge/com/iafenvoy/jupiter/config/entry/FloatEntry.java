package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.RangeConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;

public class FloatEntry extends BaseEntry<Float> implements RangeConfigEntry<Float> {
   private final float minValue;
   private final float maxValue;
   private final boolean useSlider = false;

   protected FloatEntry(FloatEntry.Builder builder) {
      super(builder);
      this.minValue = builder.minValue;
      this.maxValue = builder.maxValue;
   }

   @Override
   public ConfigType<Float> getType() {
      return ConfigTypes.FLOAT;
   }

   @Override
   public ConfigEntry<Float> newInstance() {
      return new FloatEntry.Builder(this).build();
   }

   @Override
   public Codec<Float> getCodec() {
      return Codec.floatRange(this.minValue, this.maxValue);
   }

   public Float getMinValue() {
      return this.minValue;
   }

   public Float getMaxValue() {
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
      float d = Float.parseFloat(s);
      if (!(d < this.minValue) && !(d > this.maxValue)) {
         this.setValue(d);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static FloatEntry.Builder builder(Component name, float defaultValue) {
      return new FloatEntry.Builder(name, defaultValue);
   }

   public static FloatEntry.Builder builder(String nameKey, float defaultValue) {
      return new FloatEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<Float, FloatEntry, FloatEntry.Builder> {
      protected float minValue = 1.0E-45F;
      protected float maxValue = 3.4028235E38F;

      public Builder(Component name, float defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, float defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(FloatEntry parent) {
         super(parent);
         this.minValue = parent.minValue;
         this.maxValue = parent.maxValue;
      }

      public FloatEntry.Builder min(float minValue) {
         this.minValue = minValue;
         return this;
      }

      public FloatEntry.Builder max(float maxValue) {
         this.maxValue = maxValue;
         return this;
      }

      public FloatEntry.Builder range(float min, float max) {
         this.min(min);
         this.max(max);
         return this;
      }

      public FloatEntry.Builder self() {
         return this;
      }

      protected FloatEntry buildInternal() {
         return new FloatEntry(this);
      }
   }
}
