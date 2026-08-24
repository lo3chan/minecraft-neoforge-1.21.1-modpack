package dev.isxander.yacl3.gui.controllers.string.number;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;

public class IntegerFieldController extends NumberFieldController<Integer> {
   private final int min;
   private final int max;

   public IntegerFieldController(Option<Integer> option, int min, int max, Function<Integer, Component> formatter) {
      super(option, formatter);
      this.min = min;
      this.max = max;
   }

   public IntegerFieldController(Option<Integer> option, int min, int max) {
      this(option, min, max, IntegerSliderController.DEFAULT_FORMATTER);
   }

   public IntegerFieldController(Option<Integer> option, Function<Integer, Component> formatter) {
      this(option, -2147483647, 2147483647, formatter);
   }

   public IntegerFieldController(Option<Integer> option) {
      this(option, -2147483647, 2147483647, IntegerSliderController.DEFAULT_FORMATTER);
   }

   @Internal
   public static IntegerFieldController createInternal(Option<Integer> option, int min, int max, ValueFormatter<Integer> formatter) {
      return new IntegerFieldController(option, min, max, formatter::format);
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
      this.option().requestSet((int)value);
   }

   @Override
   public double pendingValue() {
      return this.option().pendingValue().intValue();
   }
}
