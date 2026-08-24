package dev.isxander.yacl3.gui.controllers.string.number;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;

public class DoubleFieldController extends NumberFieldController<Double> {
   private final double min;
   private final double max;

   public DoubleFieldController(Option<Double> option, double min, double max, Function<Double, Component> formatter) {
      super(option, formatter);
      this.min = min;
      this.max = max;
   }

   public DoubleFieldController(Option<Double> option, double min, double max) {
      this(option, min, max, DoubleSliderController.DEFAULT_FORMATTER);
   }

   public DoubleFieldController(Option<Double> option, Function<Double, Component> formatter) {
      this(option, -1.7976931348623157E308, 1.7976931348623157E308, formatter);
   }

   public DoubleFieldController(Option<Double> option) {
      this(option, -1.7976931348623157E308, 1.7976931348623157E308, DoubleSliderController.DEFAULT_FORMATTER);
   }

   @Internal
   public static DoubleFieldController createInternal(Option<Double> option, double min, double max, ValueFormatter<Double> formatter) {
      return new DoubleFieldController(option, min, max, formatter::format);
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
   public String getString() {
      return NUMBER_FORMAT.format(this.option().pendingValue());
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
