package io.wispforest.owo.config.ui;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.ui.component.ConfigEnumButton;
import io.wispforest.owo.config.ui.component.ConfigSlider;
import io.wispforest.owo.config.ui.component.ConfigTextBox;
import io.wispforest.owo.config.ui.component.ConfigToggleButton;
import io.wispforest.owo.config.ui.component.OptionValueProvider;
import io.wispforest.owo.config.ui.component.SearchAnchorComponent;
import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.parsing.UIModel;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class OptionComponents {
   public static OptionComponentFactory.Result<FlowLayout, ConfigTextBox> createTextBox(UIModel model, Option<?> option, Consumer<ConfigTextBox> processor) {
      return createTextBox(model, option, Object::toString, processor);
   }

   public static <T> OptionComponentFactory.Result<FlowLayout, ConfigTextBox> createTextBox(
      UIModel model, Option<T> option, Function<T, String> toStringFunction, Consumer<ConfigTextBox> processor
   ) {
      FlowLayout optionComponent = model.expandTemplate(
         FlowLayout.class, "text-box-config-option", packParameters(option.translationKey(), toStringFunction.apply(option.value()))
      );
      ConfigTextBox valueBox = optionComponent.childById(ConfigTextBox.class, "value-box");
      ButtonComponent resetButton = optionComponent.childById(ButtonComponent.class, "reset-button");
      if (option.detached()) {
         resetButton.active = false;
         valueBox.setEditable(false);
      } else {
         resetButton.active = !valueBox.getValue().equals(toStringFunction.apply(option.defaultValue()));
         resetButton.onPress(button -> {
            valueBox.setValue(toStringFunction.apply(option.defaultValue()));
            button.active = false;
         });
         valueBox.onChanged().subscribe(s -> resetButton.active = !s.equals(toStringFunction.apply(option.defaultValue())));
      }

      processor.accept(valueBox);
      optionComponent.child(
         new SearchAnchorComponent(
            optionComponent, option.key(), () -> optionComponent.childById(LabelComponent.class, "option-name").text().getString(), valueBox::getValue
         )
      );
      return new OptionComponentFactory.Result<>(optionComponent, valueBox);
   }

   public static OptionComponentFactory.Result<FlowLayout, OptionValueProvider> createRangeControls(
      UIModel model, Option<? extends Number> option, int decimalPlaces
   ) {
      boolean withDecimals = decimalPlaces > 0;
      Number value = option.value();
      FlowLayout optionComponent = model.expandTemplate(FlowLayout.class, "range-config-option", packParameters(option.translationKey(), value.toString()));
      RangeConstraint constraint = option.backingField().field().getAnnotation(RangeConstraint.class);
      double min = constraint.min();
      double max = constraint.max();
      final ConfigSlider sliderInput = optionComponent.childById(ConfigSlider.class, "value-slider");
      sliderInput.min(min).max(max).decimalPlaces(decimalPlaces).snap(!withDecimals).setFromDiscreteValue(value.doubleValue());
      sliderInput.valueType(option.clazz());
      ButtonComponent resetButton = optionComponent.childById(ButtonComponent.class, "reset-button");
      if (option.detached()) {
         resetButton.active = false;
         sliderInput.active = false;
      } else {
         resetButton.active = (withDecimals ? value.doubleValue() : Math.round(value.doubleValue())) != option.defaultValue().doubleValue();
         resetButton.onPress(button -> {
            sliderInput.setFromDiscreteValue(option.defaultValue().doubleValue());
            button.active = false;
         });
         sliderInput.onChanged()
            .subscribe(newValue -> resetButton.active = (withDecimals ? newValue : Math.round(newValue)) != option.defaultValue().doubleValue());
      }

      FlowLayout sliderControls = optionComponent.childById(FlowLayout.class, "slider-controls");
      BaseParentComponent textControls = createTextBox(model, option, configTextBox -> {
         configTextBox.configureForNumber(option.clazz());
         Predicate<String> predicate = configTextBox.applyPredicate();
         configTextBox.applyPredicate(predicate.and(s -> {
            double parsed = Double.parseDouble(s);
            return parsed >= min && parsed <= max;
         }));
      }).baseComponent().childById(FlowLayout.class, "controls-flow").positioning(Positioning.layout());
      final ConfigTextBox textInput = textControls.childById(ConfigTextBox.class, "value-box");
      FlowLayout controlsLayout = optionComponent.childById(FlowLayout.class, "controls-flow");
      ButtonComponent toggleButton = optionComponent.childById(ButtonComponent.class, "toggle-button");
      final MutableBoolean textMode = new MutableBoolean(false);
      toggleButton.onPress(
         button -> {
            textMode.setValue(textMode.isFalse());
            if (textMode.isTrue()) {
               sliderControls.remove();
               textInput.text(sliderInput.decimalPlaces() == 0 ? String.valueOf((int)sliderInput.discreteValue()) : String.valueOf(sliderInput.discreteValue()));
               controlsLayout.child(textControls);
            } else {
               textControls.remove();
               sliderInput.setFromDiscreteValue(((Number)textInput.parsedValue()).doubleValue());
               controlsLayout.child(sliderControls);
            }

            button.tooltip(
               textMode.isTrue()
                  ? Component.translatable("text.owo.config.button.range.edit_with_slider")
                  : Component.translatable("text.owo.config.button.range.edit_as_text")
            );
         }
      );
      optionComponent.child(
         new SearchAnchorComponent(
            optionComponent,
            option.key(),
            () -> optionComponent.childById(LabelComponent.class, "option-name").text().getString(),
            () -> textMode.isTrue() ? textInput.getValue() : sliderInput.getMessage().getString()
         )
      );
      return new OptionComponentFactory.Result<>(optionComponent, new OptionValueProvider() {
         @Override
         public boolean isValid() {
            return textMode.isTrue() ? textInput.isValid() : sliderInput.isValid();
         }

         @Override
         public Object parsedValue() {
            return textMode.isTrue() ? textInput.parsedValue() : sliderInput.parsedValue();
         }
      });
   }

   public static OptionComponentFactory.Result<FlowLayout, ConfigToggleButton> createToggleButton(UIModel model, Option<Boolean> option) {
      FlowLayout optionComponent = model.expandTemplate(
         FlowLayout.class, "boolean-toggle-config-option", packParameters(option.translationKey(), option.value().toString())
      );
      ConfigToggleButton toggleButton = optionComponent.childById(ConfigToggleButton.class, "toggle-button");
      ButtonComponent resetButton = optionComponent.childById(ButtonComponent.class, "reset-button");
      toggleButton.enabled(option.value());
      if (option.detached()) {
         resetButton.active = false;
         toggleButton.active = false;
      } else {
         resetButton.active = option.value() != option.defaultValue();
         resetButton.onPress(button -> {
            toggleButton.enabled(option.defaultValue());
            button.active = false;
         });
         toggleButton.onPress(button -> resetButton.active = toggleButton.parsedValue() != option.defaultValue());
      }

      optionComponent.child(
         new SearchAnchorComponent(
            optionComponent,
            option.key(),
            () -> optionComponent.childById(LabelComponent.class, "option-name").text().getString(),
            () -> toggleButton.getMessage().getString()
         )
      );
      return new OptionComponentFactory.Result<>(optionComponent, toggleButton);
   }

   public static OptionComponentFactory.Result<FlowLayout, ConfigEnumButton> createEnumButton(UIModel model, Option<? extends Enum<?>> option) {
      FlowLayout optionComponent = model.expandTemplate(
         FlowLayout.class, "enum-config-option", packParameters(option.translationKey(), option.value().toString())
      );
      ConfigEnumButton enumButton = optionComponent.childById(ConfigEnumButton.class, "enum-button");
      ButtonComponent resetButton = optionComponent.childById(ButtonComponent.class, "reset-button");
      enumButton.init(option, option.value().ordinal());
      if (option.detached()) {
         resetButton.active = false;
         enumButton.active = false;
      } else {
         resetButton.active = option.value() != option.defaultValue();
         resetButton.onPress(button -> {
            enumButton.select(option.defaultValue().ordinal());
            button.active = false;
         });
         enumButton.onPress(button -> resetButton.active = enumButton.parsedValue() != option.defaultValue());
      }

      optionComponent.child(
         new SearchAnchorComponent(
            optionComponent,
            option.key(),
            () -> optionComponent.childById(LabelComponent.class, "option-name").text().getString(),
            () -> enumButton.getMessage().getString()
         )
      );
      return new OptionComponentFactory.Result<>(optionComponent, enumButton);
   }

   public static Map<String, String> packParameters(String name, String value) {
      return Map.of("config-option-name", name, "config-option-value", value);
   }
}
