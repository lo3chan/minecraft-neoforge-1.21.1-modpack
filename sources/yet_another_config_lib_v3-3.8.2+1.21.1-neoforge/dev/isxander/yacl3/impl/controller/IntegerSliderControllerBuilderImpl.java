package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;

public class IntegerSliderControllerBuilderImpl extends AbstractControllerBuilderImpl<Integer> implements IntegerSliderControllerBuilder {
   private int min;
   private int max;
   private int step;
   private ValueFormatter<Integer> formatter = IntegerSliderController.DEFAULT_FORMATTER::apply;

   public IntegerSliderControllerBuilderImpl(Option<Integer> option) {
      super(option);
   }

   public IntegerSliderControllerBuilder range(Integer min, Integer max) {
      this.min = min;
      this.max = max;
      return this;
   }

   public IntegerSliderControllerBuilder step(Integer step) {
      this.step = step;
      return this;
   }

   public IntegerSliderControllerBuilder formatValue(ValueFormatter<Integer> formatter) {
      this.formatter = formatter;
      return this;
   }

   @Override
   public Controller<Integer> build() {
      return IntegerSliderController.createInternal(this.option, this.min, this.max, this.step, this.formatter);
   }
}
