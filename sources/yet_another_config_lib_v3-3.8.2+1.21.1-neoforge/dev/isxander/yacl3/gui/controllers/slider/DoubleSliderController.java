package dev.isxander.yacl3.gui.controllers.slider;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;

public class DoubleSliderController implements ISliderController<Double> {
   public static final Function<Double, Component> DEFAULT_FORMATTER = value -> Component.literal(String.format("%,.2f", value).replaceAll("[  ]", " "));
   private final Option<Double> option;
   private final double min;
   private final double max;
   private final double interval;
   private final ValueFormatter<Double> valueFormatter;

   public DoubleSliderController(Option<Double> option, double min, double max, double interval) {
      this(option, min, max, interval, DEFAULT_FORMATTER);
   }

   public DoubleSliderController(Option<Double> option, double min, double max, double interval, Function<Double, Component> valueFormatter) {
      Validate.isTrue(max > min, "`max` cannot be smaller than `min`", new Object[0]);
      Validate.isTrue(interval > 0.0, "`interval` must be more than 0", new Object[0]);
      Validate.notNull(valueFormatter, "`valueFormatter` must not be null", new Object[0]);
      this.option = option;
      this.min = min;
      this.max = max;
      this.interval = interval;
      this.valueFormatter = valueFormatter::apply;
   }

   public static DoubleSliderController createInternal(Option<Double> option, double min, double max, double interval, ValueFormatter<Double> formatter) {
      return new DoubleSliderController(option, min, max, interval, formatter::format);
   }

   @Override
   public Option<Double> option() {
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
      this.option().requestSet(value);
   }

   @Override
   public double pendingValue() {
      return this.option().pendingValue();
   }
}
