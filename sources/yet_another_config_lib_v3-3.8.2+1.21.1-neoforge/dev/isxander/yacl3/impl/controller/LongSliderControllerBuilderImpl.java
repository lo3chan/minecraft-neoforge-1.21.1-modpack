package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.LongSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.LongSliderController;

public class LongSliderControllerBuilderImpl extends AbstractControllerBuilderImpl<Long> implements LongSliderControllerBuilder {
   private long min;
   private long max;
   private long step;
   private ValueFormatter<Long> formatter = LongSliderController.DEFAULT_FORMATTER::apply;

   public LongSliderControllerBuilderImpl(Option<Long> option) {
      super(option);
   }

   public LongSliderControllerBuilder range(Long min, Long max) {
      this.min = min;
      this.max = max;
      return this;
   }

   public LongSliderControllerBuilder step(Long step) {
      this.step = step;
      return this;
   }

   public LongSliderControllerBuilder formatValue(ValueFormatter<Long> formatter) {
      this.formatter = formatter;
      return this;
   }

   @Override
   public Controller<Long> build() {
      return LongSliderController.createInternal(this.option, this.min, this.max, this.step, this.formatter);
   }
}
