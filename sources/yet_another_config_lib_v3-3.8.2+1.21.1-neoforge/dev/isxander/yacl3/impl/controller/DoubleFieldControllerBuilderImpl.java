package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl3.gui.controllers.string.number.DoubleFieldController;

public class DoubleFieldControllerBuilderImpl extends AbstractControllerBuilderImpl<Double> implements DoubleFieldControllerBuilder {
   private double min = -1.7976931348623157E308;
   private double max = 1.7976931348623157E308;
   private ValueFormatter<Double> formatter = DoubleSliderController.DEFAULT_FORMATTER::apply;

   public DoubleFieldControllerBuilderImpl(Option<Double> option) {
      super(option);
   }

   public DoubleFieldControllerBuilder min(Double min) {
      this.min = min;
      return this;
   }

   public DoubleFieldControllerBuilder max(Double max) {
      this.max = max;
      return this;
   }

   public DoubleFieldControllerBuilder range(Double min, Double max) {
      this.min = min;
      this.max = max;
      return this;
   }

   public DoubleFieldControllerBuilder formatValue(ValueFormatter<Double> formatter) {
      this.formatter = formatter;
      return this;
   }

   @Override
   public Controller<Double> build() {
      return DoubleFieldController.createInternal(this.option, this.min, this.max, this.formatter);
   }
}
