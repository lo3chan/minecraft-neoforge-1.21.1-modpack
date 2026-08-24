package io.wispforest.owo.config.ui.component;

import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.NumberReflection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.w3c.dom.Element;

@Internal
public class ConfigTextBox extends TextBoxComponent implements OptionValueProvider {
   protected int invalidColor = 15408438;
   protected int validColor = 2686911;
   protected Function<String, Object> valueParser = s -> s;
   protected Predicate<String> inputPredicate = s -> true;
   protected Predicate<String> applyPredicate = s -> true;

   public ConfigTextBox() {
      super(Sizing.fixed(0));
      this.setMaxLength(2147483647);
      this.textValue.observe(s -> this.setTextColor(this.applyPredicate.test(s) ? this.validColor : this.invalidColor));
   }

   public ConfigTextBox configureForNumber(Class<? extends Number> fieldType) {
      boolean floatingPoint = NumberReflection.isFloatingPointType(fieldType);
      double min = NumberReflection.minValue(fieldType).doubleValue();
      double max = NumberReflection.maxValue(fieldType).doubleValue();
      this.valueParser = s -> {
         try {
            return NumberReflection.convert(floatingPoint ? Double.parseDouble(s) : Long.parseLong(s), fieldType);
         } catch (NumberFormatException var4) {
            return NumberReflection.convert(0L, fieldType);
         }
      };
      this.inputPredicate(floatingPoint ? s -> s.matches("-?\\d*\\.?\\d*") : s -> s.matches("-?\\d*"));
      this.applyPredicate(s -> {
         try {
            double value = Double.parseDouble(s);
            return value >= min && value <= max;
         } catch (NumberFormatException var7) {
            return false;
         }
      });
      return this;
   }

   @Override
   public boolean isValid() {
      return this.applyPredicate.test(this.getValue());
   }

   @Override
   public Object parsedValue() {
      return this.valueParser.apply(this.getValue());
   }

   public ConfigTextBox inputPredicate(Predicate<String> inputPredicate) {
      this.inputPredicate = inputPredicate;
      this.setFilter(this.inputPredicate);
      return this;
   }

   public Predicate<String> inputPredicate() {
      return this.inputPredicate;
   }

   public ConfigTextBox applyPredicate(Predicate<String> applyPredicate) {
      this.applyPredicate = applyPredicate;
      return this;
   }

   public Predicate<String> applyPredicate() {
      return this.applyPredicate;
   }

   public ConfigTextBox invalidColor(int invalidColor) {
      this.invalidColor = invalidColor;
      return this;
   }

   public int invalidColor() {
      return this.invalidColor;
   }

   public ConfigTextBox validColor(int validColor) {
      this.validColor = validColor;
      return this;
   }

   public int validColor() {
      return this.validColor;
   }

   public Function<String, Object> valueParser() {
      return this.valueParser;
   }

   public ConfigTextBox valueParser(Function<String, Object> valueParser) {
      this.valueParser = valueParser;
      return this;
   }

   @Override
   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);
      UIParsing.apply(children, "invalid-color", Color::parseAndPack, this::invalidColor);
      UIParsing.apply(children, "valid-color", Color::parseAndPack, this::validColor);
   }
}
