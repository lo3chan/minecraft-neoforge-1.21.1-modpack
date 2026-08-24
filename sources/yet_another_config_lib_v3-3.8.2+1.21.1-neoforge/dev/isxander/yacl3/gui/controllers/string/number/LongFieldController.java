package dev.isxander.yacl3.gui.controllers.string.number;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.LongSliderController;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;

public class LongFieldController extends NumberFieldController<Long> {
   private final long min;
   private final long max;

   public LongFieldController(Option<Long> option, long min, long max, Function<Long, Component> formatter) {
      super(option, formatter);
      this.min = min;
      this.max = max;
   }

   public LongFieldController(Option<Long> option, long min, long max) {
      this(option, min, max, LongSliderController.DEFAULT_FORMATTER);
   }

   public LongFieldController(Option<Long> option, Function<Long, Component> formatter) {
      this(option, -9223372036854775807L, 9223372036854775807L, formatter);
   }

   public LongFieldController(Option<Long> option) {
      this(option, -9223372036854775807L, 9223372036854775807L, LongSliderController.DEFAULT_FORMATTER);
   }

   @Internal
   public static LongFieldController createInternal(Option<Long> option, long min, long max, ValueFormatter<Long> formatter) {
      return new LongFieldController(option, min, max, formatter::format);
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
      this.option().requestSet((long)value);
   }

   @Override
   public double pendingValue() {
      return this.option().pendingValue().longValue();
   }
}
