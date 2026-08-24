package dev.isxander.yacl3.gui.controllers.string.number;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;

public class FloatFieldController extends NumberFieldController<Float> {
   private final float min;
   private final float max;

   public FloatFieldController(Option<Float> option, float min, float max, Function<Float, Component> formatter) {
      super(option, formatter);
      this.min = min;
      this.max = max;
   }

   public FloatFieldController(Option<Float> option, float min, float max) {
      this(option, min, max, FloatSliderController.DEFAULT_FORMATTER);
   }

   public FloatFieldController(Option<Float> option, Function<Float, Component> formatter) {
      this(option, -3.4028235E38F, 3.4028235E38F, formatter);
   }

   public FloatFieldController(Option<Float> option) {
      this(option, -3.4028235E38F, 3.4028235E38F, FloatSliderController.DEFAULT_FORMATTER);
   }

   @Internal
   public static FloatFieldController createInternal(Option<Float> option, float min, float max, ValueFormatter<Float> formatter) {
      return new FloatFieldController(option, min, max, formatter::format);
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
      this.option().requestSet((float)value);
   }

   @Override
   public double pendingValue() {
      return this.option().pendingValue().floatValue();
   }
}
