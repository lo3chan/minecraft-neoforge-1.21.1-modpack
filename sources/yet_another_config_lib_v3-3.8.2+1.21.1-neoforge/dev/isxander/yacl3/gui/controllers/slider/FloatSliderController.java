package dev.isxander.yacl3.gui.controllers.slider;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;

public class FloatSliderController implements ISliderController<Float> {
   public static final Function<Float, Component> DEFAULT_FORMATTER = value -> Component.literal(String.format("%,.1f", value).replaceAll("[  ]", " "));
   private final Option<Float> option;
   private final float min;
   private final float max;
   private final float interval;
   private final ValueFormatter<Float> valueFormatter;

   public FloatSliderController(Option<Float> option, float min, float max, float interval) {
      this(option, min, max, interval, DEFAULT_FORMATTER);
   }

   public FloatSliderController(Option<Float> option, float min, float max, float interval, Function<Float, Component> valueFormatter) {
      Validate.isTrue(max > min, "`max` cannot be smaller than `min`", new Object[0]);
      Validate.isTrue(interval > 0.0F, "`interval` must be more than 0", new Object[0]);
      Validate.notNull(valueFormatter, "`valueFormatter` must not be null", new Object[0]);
      this.option = option;
      this.min = min;
      this.max = max;
      this.interval = interval;
      this.valueFormatter = valueFormatter::apply;
   }

   public static FloatSliderController createInternal(Option<Float> option, float min, float max, float interval, ValueFormatter<Float> formatter) {
      return new FloatSliderController(option, min, max, interval, formatter::format);
   }

   @Override
   public Option<Float> option() {
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
      this.option().requestSet((float)value);
   }

   @Override
   public double pendingValue() {
      return this.option().pendingValue().floatValue();
   }
}
