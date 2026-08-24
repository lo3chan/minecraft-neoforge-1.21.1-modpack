package io.wispforest.owo.config.ui.component;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Sizing;
import java.util.Locale;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class ConfigEnumButton extends ButtonComponent implements OptionValueProvider {
   @Nullable
   protected Option<? extends Enum<?>> backingOption = null;
   @Nullable
   protected Enum<?>[] backingValues = null;
   protected int selectedIndex = 0;
   protected boolean wasRightClicked = false;

   public ConfigEnumButton() {
      super(Component.empty(), button -> {});
      this.verticalSizing(Sizing.fixed(20));
      this.updateMessage();
   }

   public boolean onMouseDown(double mouseX, double mouseY, int button) {
      this.wasRightClicked = button == 1;
      return super.onMouseDown(mouseX, mouseY, button);
   }

   public void onPress() {
      if (!this.wasRightClicked && !Screen.hasShiftDown()) {
         this.selectedIndex++;
         if (this.selectedIndex > this.backingValues.length - 1) {
            this.selectedIndex = this.selectedIndex - this.backingValues.length;
         }
      } else {
         this.selectedIndex--;
         if (this.selectedIndex < 0) {
            this.selectedIndex = this.selectedIndex + this.backingValues.length;
         }
      }

      this.updateMessage();
      super.onPress();
   }

   protected boolean isValidClickButton(int button) {
      return button == 1 || super.isValidClickButton(button);
   }

   protected void updateMessage() {
      if (this.backingOption != null) {
         String enumName = StringUtils.uncapitalize(this.backingValues.getClass().componentType().getSimpleName());
         String valueName = this.backingValues[this.selectedIndex].name().toLowerCase(Locale.ROOT);
         String optionValueKey = this.backingOption.translationKey() + ".value." + valueName;
         this.setMessage(
            I18n.exists(optionValueKey)
               ? Component.translatable(optionValueKey)
               : Component.translatable("text.config." + this.backingOption.configName() + ".enum." + enumName + "." + valueName)
         );
      }
   }

   public ConfigEnumButton init(Option<? extends Enum<?>> option, int selectedIndex) {
      this.backingOption = option;
      this.backingValues = (Enum<?>[])option.backingField().field().getType().getEnumConstants();
      this.selectedIndex = selectedIndex;
      this.updateMessage();
      return this;
   }

   public ConfigEnumButton select(int index) {
      this.selectedIndex = index;
      this.updateMessage();
      return this;
   }

   @Override
   public boolean isValid() {
      return true;
   }

   @Override
   public Object parsedValue() {
      return this.backingValues[this.selectedIndex];
   }
}
