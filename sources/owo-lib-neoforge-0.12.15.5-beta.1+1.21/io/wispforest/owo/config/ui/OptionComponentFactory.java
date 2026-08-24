package io.wispforest.owo.config.ui;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.WithAlpha;
import io.wispforest.owo.config.ui.component.ConfigTextBox;
import io.wispforest.owo.config.ui.component.ListOptionContainer;
import io.wispforest.owo.config.ui.component.OptionValueProvider;
import io.wispforest.owo.ui.component.BoxComponent;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.ColorPickerComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.util.NumberReflection;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public interface OptionComponentFactory<T> {
   OptionComponentFactory<? extends Number> NUMBER = (model, option) -> {
      Field field = option.backingField().field();
      return field.isAnnotationPresent(RangeConstraint.class)
         ? OptionComponents.createRangeControls(
            model, option, NumberReflection.isFloatingPointType(field.getType()) ? field.getAnnotation(RangeConstraint.class).decimalPlaces() : 0
         )
         : OptionComponents.createTextBox(model, option, configTextBox -> configTextBox.configureForNumber(option.clazz()));
   };
   OptionComponentFactory<? extends CharSequence> STRING = (model, option) -> OptionComponents.createTextBox(model, option, configTextBox -> {
      if (option.constraint() != null) {
         configTextBox.applyPredicate(option.constraint()::test);
      }
   });
   OptionComponentFactory<ResourceLocation> IDENTIFIER = (model, option) -> OptionComponents.createTextBox(model, option, configTextBox -> {
      configTextBox.inputPredicate(s -> s.matches("[a-z0-9_.:\\-]*"));
      configTextBox.applyPredicate(s -> ResourceLocation.tryParse(s) != null);
      configTextBox.valueParser(ResourceLocation::parse);
   });
   OptionComponentFactory<Color> COLOR = (model, option) -> {
      boolean withAlpha = option.backingField().hasAnnotation(WithAlpha.class);
      OptionComponentFactory.Result<FlowLayout, ConfigTextBox> result = OptionComponents.createTextBox(
         model,
         option,
         color -> color.asHexString(withAlpha),
         configTextBox -> {
            configTextBox.inputPredicate(withAlpha ? s -> s.matches("#[a-zA-Z\\d]{0,8}") : s -> s.matches("#[a-zA-Z\\d]{0,6}"));
            configTextBox.applyPredicate(withAlpha ? s -> s.matches("#[a-zA-Z\\d]{8}") : s -> s.matches("#[a-zA-Z\\d]{6}"));
            configTextBox.valueParser(
               withAlpha ? s -> Color.ofArgb(Integer.parseUnsignedInt(s.substring(1), 16)) : s -> Color.ofRgb(Integer.parseUnsignedInt(s.substring(1), 16))
            );
         }
      );
      result.baseComponent
         .childById(FlowLayout.class, "controls-flow")
         .configure(
            controls -> {
               Supplier<Color> valueGetter = () -> result.optionProvider.isValid() ? (Color)result.optionProvider.parsedValue() : Color.BLACK;
               BoxComponent box = Components.box(Sizing.fixed(15), Sizing.fixed(15)).color(valueGetter.get()).fill(true);
               box.margins(Insets.right(5)).cursorStyle(CursorStyle.HAND);
               controls.child(0, box);
               result.optionProvider.onChanged().subscribe(value -> box.color(valueGetter.get()));
               box.mouseDown()
                  .subscribe(
                     (mouseX, mouseY, button) -> {
                        ((FlowLayout)box.root())
                           .child(
                              Containers.overlay(
                                    model.expandTemplate(
                                          FlowLayout.class,
                                          "color-picker-panel",
                                          Map.of("color", valueGetter.get().asHexString(withAlpha), "with-alpha", String.valueOf(withAlpha))
                                       )
                                       .configure(
                                          flowLayout -> {
                                             ColorPickerComponent picker = flowLayout.childById(ColorPickerComponent.class, "color-picker");
                                             BoxComponent previewBox = flowLayout.childById(BoxComponent.class, "current-color");
                                             picker.onChanged().subscribe(previewBox::color);
                                             ((ButtonComponent)flowLayout.childById((Class<T>)ButtonComponent.class, "confirm-button"))
                                                .onPress(confirmButton -> {
                                                   result.optionProvider.text(picker.selectedColor().asHexString(withAlpha));
                                                   flowLayout.parent().remove();
                                                });
                                             ((ButtonComponent)flowLayout.childById((Class<T>)ButtonComponent.class, "cancel-button"))
                                                .onPress(cancelButton -> flowLayout.parent().remove());
                                          }
                                       )
                                 )
                                 .zIndex(100)
                           );
                        return true;
                     }
                  );
            }
         );
      return result;
   };
   OptionComponentFactory<Boolean> BOOLEAN = OptionComponents::createToggleButton;
   OptionComponentFactory<? extends Enum<?>> ENUM = OptionComponents::createEnumButton;
   OptionComponentFactory<List<?>> LIST = (model, option) -> {
      ListOptionContainer layout = new ListOptionContainer<>(option);
      return new OptionComponentFactory.Result<>(layout, layout);
   };

   OptionComponentFactory.Result<?, ?> make(UIModel var1, Option<T> var2);

   public record Result<B extends Component, P extends OptionValueProvider>(B baseComponent, P optionProvider) {
   }
}
