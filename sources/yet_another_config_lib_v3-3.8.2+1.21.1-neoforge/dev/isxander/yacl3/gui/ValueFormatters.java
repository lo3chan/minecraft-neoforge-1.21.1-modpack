package dev.isxander.yacl3.gui;

import dev.isxander.yacl3.api.controller.ValueFormatter;
import net.minecraft.network.chat.Component;

public final class ValueFormatters {
   public static ValueFormatter<Float> percent(int decimalPlaces) {
      return new ValueFormatters.PercentFormatter(decimalPlaces);
   }

   public record PercentFormatter(int decimalPlaces) implements ValueFormatter<Float> {
      public PercentFormatter() {
         this(1);
      }

      public Component format(Float value) {
         return Component.literal(String.format("%." + this.decimalPlaces + "f%%", value * 100.0F));
      }
   }
}
