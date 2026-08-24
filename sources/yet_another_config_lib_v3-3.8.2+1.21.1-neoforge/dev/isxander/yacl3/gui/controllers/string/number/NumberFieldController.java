package dev.isxander.yacl3.gui.controllers.string.number;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.slider.ISliderController;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class NumberFieldController<T extends Number> implements ISliderController<T>, IStringController<T> {
   protected static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
   private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = DecimalFormatSymbols.getInstance();
   private final Option<T> option;
   private final ValueFormatter<T> displayFormatter;

   public NumberFieldController(Option<T> option, Function<T, Component> displayFormatter) {
      this.option = option;
      this.displayFormatter = displayFormatter::apply;
   }

   @Override
   public Option<T> option() {
      return this.option;
   }

   @Override
   public void setFromString(String value) {
      try {
         String transformed = this.transformInput(value);
         this.setPendingValue(Mth.clamp(NUMBER_FORMAT.parse(transformed).doubleValue(), this.min(), this.max()));
      } catch (ParseException var3) {
         YACLConstants.LOGGER.warn("Failed to parse number: {}", value);
      }
   }

   @Override
   public double pendingValue() {
      return this.option().pendingValue().doubleValue();
   }

   @Override
   public boolean isInputValid(String input) {
      input = this.transformInput(input);
      ParsePosition parsePosition = new ParsePosition(0);
      NUMBER_FORMAT.parse(input, parsePosition);
      return parsePosition.getIndex() == input.length();
   }

   @Override
   public Component formatValue() {
      return this.displayFormatter.format(this.option().pendingValue());
   }

   @Override
   public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
      return new StringControllerElement(this, screen, widgetDimension, false);
   }

   @Override
   public double interval() {
      return -1.0;
   }

   protected String transformInput(String input) {
      if (input.isEmpty()) {
         input = "0";
      }

      if (input.equals("-")) {
         input = "-0";
      }

      return input.replace(DECIMAL_FORMAT_SYMBOLS.getGroupingSeparator() + "", "");
   }
}
