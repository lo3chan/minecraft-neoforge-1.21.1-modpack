package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController;

public class DoubleSliderControllerBuilderImpl extends AbstractControllerBuilderImpl<Double> implements DoubleSliderControllerBuilder {
   private double min;
   private double max;
   private double step;
   private ValueFormatter<Double> formatter = DoubleSliderController.DEFAULT_FORMATTER::apply;

   public DoubleSliderControllerBuilderImpl(Option<Double> option) {
      super(option);
   }

   public DoubleSliderControllerBuilder range(Double min, Double max) {
      this.min = min;
      this.max = max;
      return this;
   }

   public DoubleSliderControllerBuilder step(Double step) {
      this.step = step;
      return this;
   }

   public DoubleSliderControllerBuilder formatValue(ValueFormatter<Double> formatter) {
      this.formatter = formatter;
      return this;
   }

   @Override
   public Controller<Double> build() {
      return DoubleSliderController.createInternal(this.option, this.min, this.max, this.step, this.formatter);
   }
}
