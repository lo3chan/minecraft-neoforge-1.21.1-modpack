package dev.isxander.yacl3.gui.controllers.slider;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;

public class IntegerSliderController implements ISliderController<Integer> {
   public static final Function<Integer, Component> DEFAULT_FORMATTER = value -> Component.literal(String.format("%,d", value).replaceAll("[  ]", " "));
   private final Option<Integer> option;
   private final int min;
   private final int max;
   private final int interval;
   private final ValueFormatter<Integer> valueFormatter;

   public IntegerSliderController(Option<Integer> option, int min, int max, int interval) {
      this(option, min, max, interval, DEFAULT_FORMATTER);
   }

   public IntegerSliderController(Option<Integer> option, int min, int max, int interval, Function<Integer, Component> valueFormatter) {
      Validate.isTrue(max > min, "`max` cannot be smaller than `min`", new Object[0]);
      Validate.isTrue(interval > 0, "`interval` must be more than 0", new Object[0]);
      Validate.notNull(valueFormatter, "`valueFormatter` must not be null", new Object[0]);
      this.option = option;
      this.min = min;
      this.max = max;
      this.interval = interval;
      this.valueFormatter = valueFormatter::apply;
   }

   public static IntegerSliderController createInternal(Option<Integer> option, int min, int max, int interval, ValueFormatter<Integer> formatter) {
      return new IntegerSliderController(option, min, max, interval, formatter::format);
   }

   @Override
   public Option<Integer> option() {
      return this.option;
   }

   @Override
   public Component formatValue() {
      return this.valueFormatter.format(this.option().pendingValue());
   }

   @Override
   public double min() {
      return this.min;
   }

   @Override
   public double max() {
      return this.max;
   }

   @Override
   public double interval() {
      return this.interval;
   }

   @Override
   public void setPendingValue(double value) {
      this.option().requestSet((int)value);
   }

   @Override
   public double pendingValue() {
      return this.option().pendingValue().intValue();
   }
}
