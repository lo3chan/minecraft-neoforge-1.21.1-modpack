package dev.isxander.yacl3.gui.controllers.slider;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;

public class LongSliderController implements ISliderController<Long> {
   public static final Function<Long, Component> DEFAULT_FORMATTER = value -> Component.literal(String.format("%,d", value).replaceAll("[  ]", " "));
   private final Option<Long> option;
   private final long min;
   private final long max;
   private final long interval;
   private final ValueFormatter<Long> valueFormatter;

   public LongSliderController(Option<Long> option, long min, long max, long interval) {
      this(option, min, max, interval, DEFAULT_FORMATTER);
   }

   public LongSliderController(Option<Long> option, long min, long max, long interval, Function<Long, Component> valueFormatter) {
      Validate.isTrue(max > min, "`max` cannot be smaller than `min`", new Object[0]);
      Validate.isTrue(interval > 0L, "`interval` must be more than 0", new Object[0]);
      Validate.notNull(valueFormatter, "`valueFormatter` must not be null", new Object[0]);
      this.option = option;
      this.min = min;
      this.max = max;
      this.interval = interval;
      this.valueFormatter = valueFormatter::apply;
   }

   public static LongSliderController createInternal(Option<Long> option, long min, long max, long interval, ValueFormatter<Long> formatter) {
      return new LongSliderController(option, min, max, interval, formatter::format);
   }

   @Override
   public Option<Long> option() {
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
      this.option().requestSet((long)value);
   }

   @Override
   public double pendingValue() {
      return this.option().pendingValue().longValue();
   }
}
