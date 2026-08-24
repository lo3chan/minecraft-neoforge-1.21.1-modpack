package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.List;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuide;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import net.caffeinemc.mods.sodium.client.config.structure.EnumOption;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

final class EnumOptionRow<E extends Enum<E>> extends AbstractOptionRow {
   private static final int MAX_CONTENT_WIDTH = 70;
   private final EnumOption<E> option;

   EnumOptionRow(LayoutBounds dim, GuiTheme theme, OptionStateStore optionStateStore, EnumOption<E> option) {
      super(dim, theme, optionStateStore, option);
      this.option = option;
   }

   public EnumOption<E> getOption() {
      return this.option;
   }

   @Override
   protected int controlContentWidth() {
      return Math.min(70, this.font.width(this.displayValue()));
   }

   @Override
   public List<ControlGuide> controlGuides() {
      return this.canShowControlGuide() ? List.of(ControlGuide.press(Component.translatable("rso.controller.guide.next_value"))) : List.of();
   }

   @Override
   protected void renderControl(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      if (this.option.showControl()) {
         Component value = this.displayValue();
         int valueWidth = this.font.width(value);
         int x = this.rightAlignedControlX(valueWidth);
         int y = this.centeredTextY();
         this.drawString(guiGraphics, value, x, y, -1);
         if (this.option.isEnabled()) {
            this.requestPointerCursorIfHovered(guiGraphics);
         }
      }
   }

   @Override
   protected boolean controlMouseClicked(double mouseX, double mouseY, int button) {
      boolean reverse = Screen.hasShiftDown();
      if (button == 1) {
         if (!ReeseSodiumOptionsConfig.config().isReverseCyclingControls()) {
            return false;
         }

         reverse = true;
      } else if (button != 0) {
         return false;
      }

      if (this.option.isEnabled() && this.option.showControl() && this.isMouseOverRow(mouseX, mouseY)) {
         this.cycleControl(reverse);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean controlKeyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.isRowFocused() && isSelectionKey(keyCode)) {
         this.cycleControl(Screen.hasShiftDown());
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean activateControl() {
      this.cycleControl(Screen.hasShiftDown());
      return true;
   }

   private Component displayValue() {
      Component value = this.option.getElementName((Enum)this.option.getValidatedValue());
      return (Component)(this.option.isEnabled() ? value : this.formatDisabledControlValue(value));
   }

   @Override
   protected Component narrationValue() {
      return this.option.showControl() ? this.option.getElementName((Enum)this.option.getValidatedValue()) : null;
   }

   @Override
   protected void updateControlNarration(NarrationElementOutput builder) {
      if (!this.option.isEnabled()) {
         builder.add(NarratedElementType.HINT, Component.translatable("rso.narration.option_unavailable"));
      } else if (this.option.showControl()) {
         Component nextValue = this.option.getElementName(this.nextValue(false));
         if (this.isFocused()) {
            builder.add(NarratedElementType.USAGE, Component.translatable("narration.cycle_button.usage.focused", new Object[]{nextValue}));
         } else if (this.isHovered()) {
            builder.add(NarratedElementType.USAGE, Component.translatable("narration.cycle_button.usage.hovered", new Object[]{nextValue}));
         }
      }
   }

   private void cycleControl(boolean reverse) {
      E nextValue = this.nextValue(reverse);
      if (nextValue != this.option.getValidatedValue()) {
         this.option.modifyValue(nextValue);
         this.playClickSound();
      }
   }

   private E nextValue(boolean reverse) {
      E[] values = (E[])this.option.getEnumClass().getEnumConstants();
      E currentValue = (E)this.option.getValidatedValue();
      int valueIndex = 0;

      for (int i = 0; i < values.length; i++) {
         if (values[i] == currentValue) {
            valueIndex = i;
            break;
         }
      }

      for (int ix = 0; ix < values.length; ix++) {
         valueIndex = reverse ? (valueIndex + values.length - 1) % values.length : (valueIndex + 1) % values.length;
         E nextValue = values[valueIndex];
         if (this.option.isValueAllowed(nextValue)) {
            return nextValue;
         }
      }

      return currentValue;
   }
}
