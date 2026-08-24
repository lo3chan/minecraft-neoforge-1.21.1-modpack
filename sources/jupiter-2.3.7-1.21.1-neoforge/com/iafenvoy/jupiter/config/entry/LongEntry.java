package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.RangeConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import net.minecraft.network.chat.Component;

public class LongEntry extends BaseEntry<Long> implements RangeConfigEntry<Long> {
   private final long minValue;
   private final long maxValue;
   private final boolean useSlider = false;

   protected LongEntry(LongEntry.Builder builder) {
      super(builder);
      this.minValue = builder.minValue;
      this.maxValue = builder.maxValue;
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public LongEntry(String nameKey, Long defaultValue) {
      this(nameKey, defaultValue, -9223372036854775808L, 9223372036854775807L);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public LongEntry(String nameKey, long defaultValue, long minValue, long maxValue) {
      super(nameKey, defaultValue);
      this.minValue = minValue;
      this.maxValue = maxValue;
   }

   @Override
   public ConfigType<Long> getType() {
      return ConfigTypes.LONG;
   }

   @Override
   public ConfigEntry<Long> newInstance() {
      return new LongEntry.Builder(this).build();
   }

   @Override
   public Codec<Long> getCodec() {
      Function<Long, DataResult<Long>> checker = Codec.checkRange(this.minValue, this.maxValue);
      return Codec.LONG.flatXmap(checker, checker);
   }

   public Long getMinValue() {
      return this.minValue;
   }

   public Long getMaxValue() {
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
      long d = Long.parseLong(s);
      if (d >= this.minValue && d <= this.maxValue) {
         this.setValue(d);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static LongEntry.Builder builder(Component name, long defaultValue) {
      return new LongEntry.Builder(name, defaultValue);
   }

   public static LongEntry.Builder builder(String nameKey, long defaultValue) {
      return new LongEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<Long, LongEntry, LongEntry.Builder> {
      protected long minValue = -9223372036854775808L;
      protected long maxValue = 9223372036854775807L;

      public Builder(Component name, long defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, long defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(LongEntry parent) {
         super(parent);
         this.minValue = parent.minValue;
         this.maxValue = parent.maxValue;
      }

      public LongEntry.Builder min(long minValue) {
         this.minValue = minValue;
         return this;
      }

      public LongEntry.Builder max(long maxValue) {
         this.maxValue = maxValue;
         return this;
      }

      public LongEntry.Builder range(long min, long max) {
         this.min(min);
         this.max(max);
         return this;
      }

      public LongEntry.Builder self() {
         return this;
      }

      protected LongEntry buildInternal() {
         return new LongEntry(this);
      }
   }
}
