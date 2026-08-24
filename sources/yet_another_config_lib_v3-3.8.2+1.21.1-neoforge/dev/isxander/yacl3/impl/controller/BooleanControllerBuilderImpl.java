package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import org.apache.commons.lang3.Validate;

public class BooleanControllerBuilderImpl extends AbstractControllerBuilderImpl<Boolean> implements BooleanControllerBuilder {
   private boolean coloured = false;
   private ValueFormatter<Boolean> formatter = BooleanController.ON_OFF_FORMATTER::apply;

   public BooleanControllerBuilderImpl(Option<Boolean> option) {
      super(option);
   }

   @Override
   public BooleanControllerBuilder coloured(boolean coloured) {
      this.coloured = coloured;
      return this;
   }

   public BooleanControllerBuilder formatValue(ValueFormatter<Boolean> formatter) {
      Validate.notNull(formatter, "formatter cannot be null", new Object[0]);
      this.formatter = formatter;
      return this;
   }

   @Override
   public BooleanControllerBuilder onOffFormatter() {
      this.formatter = BooleanController.ON_OFF_FORMATTER::apply;
      return this;
   }

   @Override
   public BooleanControllerBuilder yesNoFormatter() {
      this.formatter = BooleanController.YES_NO_FORMATTER::apply;
      return this;
   }

   @Override
   public BooleanControllerBuilder trueFalseFormatter() {
      this.formatter = BooleanController.TRUE_FALSE_FORMATTER::apply;
      return this;
   }

   @Override
   public Controller<Boolean> build() {
      return BooleanController.createInternal(this.option, this.formatter, this.coloured);
   }
}
