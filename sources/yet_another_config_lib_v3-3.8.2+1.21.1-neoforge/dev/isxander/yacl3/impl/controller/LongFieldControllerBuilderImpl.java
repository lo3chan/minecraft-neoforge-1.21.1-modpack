package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.LongFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.LongSliderController;
import dev.isxander.yacl3.gui.controllers.string.number.LongFieldController;

public class LongFieldControllerBuilderImpl extends AbstractControllerBuilderImpl<Long> implements LongFieldControllerBuilder {
   private long min = -9223372036854775808L;
   private long max = 9223372036854775807L;
   private ValueFormatter<Long> formatter = LongSliderController.DEFAULT_FORMATTER::apply;

   public LongFieldControllerBuilderImpl(Option<Long> option) {
      super(option);
   }

   public LongFieldControllerBuilder min(Long min) {
      this.min = min;
      return this;
   }

   public LongFieldControllerBuilder max(Long max) {
      this.max = max;
      return this;
   }

   public LongFieldControllerBuilder range(Long min, Long max) {
      this.min = min;
      this.max = max;
      return this;
   }

   public LongFieldControllerBuilder formatValue(ValueFormatter<Long> formatter) {
      this.formatter = formatter;
      return this;
   }

   @Override
   public Controller<Long> build() {
      return LongFieldController.createInternal(this.option, this.min, this.max, this.formatter);
   }
}
