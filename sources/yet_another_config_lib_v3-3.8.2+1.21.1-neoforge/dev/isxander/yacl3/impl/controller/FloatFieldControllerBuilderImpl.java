package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl3.gui.controllers.string.number.FloatFieldController;

public class FloatFieldControllerBuilderImpl extends AbstractControllerBuilderImpl<Float> implements FloatFieldControllerBuilder {
   private float min = -3.4028235E38F;
   private float max = 3.4028235E38F;
   private ValueFormatter<Float> formatter = FloatSliderController.DEFAULT_FORMATTER::apply;

   public FloatFieldControllerBuilderImpl(Option<Float> option) {
      super(option);
   }

   public FloatFieldControllerBuilder min(Float min) {
      this.min = min;
      return this;
   }

   public FloatFieldControllerBuilder max(Float max) {
      this.max = max;
      return this;
   }

   public FloatFieldControllerBuilder range(Float min, Float max) {
      this.min = min;
      this.max = max;
      return this;
   }

   public FloatFieldControllerBuilder formatValue(ValueFormatter<Float> formatter) {
      this.formatter = formatter;
      return this;
   }

   @Override
   public Controller<Float> build() {
      return FloatFieldController.createInternal(this.option, this.min, this.max, this.formatter);
   }
}
