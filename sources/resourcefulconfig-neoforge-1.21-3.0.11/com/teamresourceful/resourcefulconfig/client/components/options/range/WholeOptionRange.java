package com.teamresourceful.resourcefulconfig.client.components.options.range;

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigValueEntry;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryData;
import com.teamresourceful.resourcefulconfig.api.types.options.Option;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public record WholeOptionRange(LongConsumer setter, LongSupplier getter, long min, long max, long step) implements OptionRange {
   private WholeOptionRange(LongConsumer setter, LongSupplier getter, double min, double max, long step) {
      this(setter, getter, (long)min, (long)max, step);
   }

   public static WholeOptionRange of(ResourcefulConfigValueEntry entry) {
      return switch (entry.type()) {
         case BYTE -> ofByte(entry);
         case SHORT -> ofShort(entry);
         case INTEGER -> ofInteger(entry);
         case LONG -> ofLong(entry);
         default -> throw new IllegalStateException("Unexpected value: " + entry.type());
      };
   }

   private static WholeOptionRange ofByte(ResourcefulConfigValueEntry entry) {
      LongConsumer setter = value -> entry.setByte((byte)value);
      LongSupplier getter = entry::getByte;
      EntryData options = entry.options();
      if (options.hasOption(Option.RANGE)) {
         ConfigOption.Range range = options.getOption(Option.RANGE);
         return new WholeOptionRange(setter, getter, range.min(), range.max(), 1L);
      } else {
         return new WholeOptionRange(setter, getter, -128L, 127L, 0L);
      }
   }

   private static WholeOptionRange ofShort(ResourcefulConfigValueEntry entry) {
      LongConsumer setter = value -> entry.setShort((short)value);
      LongSupplier getter = entry::getShort;
      EntryData options = entry.options();
      if (options.hasOption(Option.RANGE)) {
         ConfigOption.Range range = options.getOption(Option.RANGE);
         return new WholeOptionRange(setter, getter, range.min(), range.max(), 1L);
      } else {
         return new WholeOptionRange(setter, getter, -32768L, 32767L, 0L);
      }
   }

   private static WholeOptionRange ofInteger(ResourcefulConfigValueEntry entry) {
      LongConsumer setter = value -> entry.setInt((int)value);
      LongSupplier getter = entry::getInt;
      EntryData options = entry.options();
      if (options.hasOption(Option.RANGE)) {
         ConfigOption.Range range = options.getOption(Option.RANGE);
         return new WholeOptionRange(setter, getter, range.min(), range.max(), 1L);
      } else {
         return new WholeOptionRange(setter, getter, -2147483648L, 2147483647L, 0L);
      }
   }

   private static WholeOptionRange ofLong(ResourcefulConfigValueEntry entry) {
      LongConsumer setter = entry::setLong;
      LongSupplier getter = entry::getLong;
      EntryData options = entry.options();
      if (options.hasOption(Option.RANGE)) {
         ConfigOption.Range range = options.getOption(Option.RANGE);
         return new WholeOptionRange(setter, getter, range.min(), range.max(), 1L);
      } else {
         return new WholeOptionRange(setter, getter, -9223372036854775808L, 9223372036854775807L, 0L);
      }
   }

   @Override
   public Component toComponent() {
      return Component.literal(String.valueOf(this.getter.getAsLong()));
   }

   @Override
   public Component minComponent() {
      return Component.literal(String.valueOf(this.min));
   }

   @Override
   public Component maxComponent() {
      return Component.literal(String.valueOf(this.max));
   }

   @Override
   public void setPercent(double value) {
      this.setter.accept((long)Mth.clampedLerp(this.min, this.max, value));
   }

   @Override
   public double getPercent() {
      return (double)(this.getter.getAsLong() - this.min) / (this.max - this.min);
   }

   @Override
   public double getStepPercent() {
      return (double)this.step / (this.max - this.min);
   }

   @Override
   public boolean hasRange() {
      return this.step != 0L;
   }
}
