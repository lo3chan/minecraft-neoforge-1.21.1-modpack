package com.teamresourceful.resourcefulconfig.client.components.options.range;

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigValueEntry;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryData;
import com.teamresourceful.resourcefulconfig.api.types.options.Option;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public record DecimalOptionRange(DoubleConsumer setter, DoubleSupplier getter, double min, double max, double step) implements OptionRange {
   public static DecimalOptionRange of(ResourcefulConfigValueEntry entry) {
      return switch (entry.type()) {
         case FLOAT -> ofFloat(entry);
         case DOUBLE -> ofDouble(entry);
         default -> throw new IllegalStateException("Unexpected value: " + entry.type());
      };
   }

   private static DecimalOptionRange ofFloat(ResourcefulConfigValueEntry entry) {
      DoubleConsumer setter = value -> entry.setFloat((float)value);
      DoubleSupplier getter = entry::getFloat;
      EntryData options = entry.options();
      if (options.hasOption(Option.RANGE)) {
         ConfigOption.Range range = options.getOption(Option.RANGE);
         return new DecimalOptionRange(setter, getter, range.min(), range.max(), 1.0);
      } else {
         return new DecimalOptionRange(setter, getter, 1.401298464324817E-45, 3.4028234663852886E38, 0.0);
      }
   }

   private static DecimalOptionRange ofDouble(ResourcefulConfigValueEntry entry) {
      DoubleConsumer setter = entry::setDouble;
      DoubleSupplier getter = entry::getDouble;
      EntryData options = entry.options();
      if (options.hasOption(Option.RANGE)) {
         ConfigOption.Range range = options.getOption(Option.RANGE);
         return new DecimalOptionRange(setter, getter, range.min(), range.max(), 1.0);
      } else {
         return new DecimalOptionRange(setter, getter, 5.0E-324, 1.7976931348623157E308, 0.0);
      }
   }

   @Override
   public Component toComponent() {
      return Component.literal(String.format("%.2f", this.getter.getAsDouble()));
   }

   @Override
   public Component minComponent() {
      return Component.literal(String.format("%.2f", this.min));
   }

   @Override
   public Component maxComponent() {
      return Component.literal(String.format("%.2f", this.max));
   }

   @Override
   public void setPercent(double value) {
      this.setter.accept(Mth.clampedLerp(this.min, this.max, value));
   }

   @Override
   public double getPercent() {
      return (this.getter.getAsDouble() - this.min) / (this.max - this.min);
   }

   @Override
   public double getStepPercent() {
      return this.step / (this.max - this.min);
   }

   @Override
   public boolean hasRange() {
      return this.step != 0.0;
   }
}
