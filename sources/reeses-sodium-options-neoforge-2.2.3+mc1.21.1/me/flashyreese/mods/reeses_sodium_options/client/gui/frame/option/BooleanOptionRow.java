package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.List;
import me.flashyreese.mods.reeses_sodium_options.client.gui.control.ControlGuide;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import net.caffeinemc.mods.sodium.client.config.structure.BooleanOption;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

final class BooleanOptionRow extends AbstractOptionRow {
   private static final int CONTENT_WIDTH = 30;
   private static final int CHECKBOX_SIZE = 10;
   private static final int DISABLED_CORNER_SIZE = 3;
   private static final int DISABLED_COLOR = -5592406;
   private final BooleanOption option;

   BooleanOptionRow(LayoutBounds dim, GuiTheme theme, OptionStateStore optionStateStore, BooleanOption option) {
      super(dim, theme, optionStateStore, option);
      this.option = option;
   }

   public BooleanOption getOption() {
      return this.option;
   }

   @Override
   protected int controlContentWidth() {
      return 30;
   }

   @Override
   public List<ControlGuide> controlGuides() {
      return this.canShowControlGuide() ? List.of(ControlGuide.press(Component.translatable("rso.controller.guide.toggle"))) : List.of();
   }

   @Override
   protected void renderControl(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      if (this.option.showControl()) {
         LayoutBounds checkboxDim = this.checkboxDim();
         boolean enabled = this.option.isEnabled();
         boolean checked = (Boolean)this.option.getValidatedValue();
         int color = this.checkboxColor(enabled, checked);
         if (checked) {
            this.drawRect(guiGraphics, checkboxDim.x() + 2, checkboxDim.y() + 2, checkboxDim.getLimitX() - 2, checkboxDim.getLimitY() - 2, color);
         }

         if (enabled) {
            this.drawBorder(guiGraphics, checkboxDim.x(), checkboxDim.y(), checkboxDim.getLimitX(), checkboxDim.getLimitY(), color);
         } else {
            this.drawDisabledCorners(guiGraphics, checkboxDim, color);
         }

         this.requestPointerCursorIfHovered(guiGraphics);
      }
   }

   @Override
   protected boolean activateControl() {
      if (this.option.isEnabled() && this.option.showControl()) {
         this.option.modifyValue(!(Boolean)this.option.getValidatedValue());
         this.playClickSound();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected Component narrationValue() {
      return this.option.showControl() ? CommonComponents.optionStatus((Boolean)this.option.getValidatedValue()) : null;
   }

   @Override
   protected void updateControlNarration(NarrationElementOutput builder) {
      if (!this.option.isEnabled()) {
         builder.add(NarratedElementType.HINT, Component.translatable("rso.narration.option_unavailable"));
      } else if (this.option.showControl()) {
         boolean checked = (Boolean)this.option.getValidatedValue();
         if (this.isFocused()) {
            builder.add(
               NarratedElementType.USAGE,
               Component.translatable(checked ? "narration.checkbox.usage.focused.uncheck" : "narration.checkbox.usage.focused.check")
            );
         } else if (this.isHovered()) {
            builder.add(
               NarratedElementType.USAGE,
               Component.translatable(checked ? "narration.checkbox.usage.hovered.uncheck" : "narration.checkbox.usage.hovered.check")
            );
         }
      }
   }

   private LayoutBounds checkboxDim() {
      int x = this.rightAlignedControlX(10);
      int y = this.getDimensions().getCenterY() - 5;
      return new LayoutBounds(x, y, 10, 10);
   }

   private int checkboxColor(boolean enabled, boolean checked) {
      if (!enabled) {
         return -5592406;
      } else {
         return checked ? this.theme.theme : -1;
      }
   }

   private void drawDisabledCorners(GuiGraphics guiGraphics, LayoutBounds dim, int color) {
      int size = 3;
      this.drawRect(guiGraphics, dim.x(), dim.y(), dim.x() + size, dim.y() + 1, color);
      this.drawRect(guiGraphics, dim.x(), dim.y(), dim.x() + 1, dim.y() + size, color);
      this.drawRect(guiGraphics, dim.getLimitX() - size, dim.y(), dim.getLimitX(), dim.y() + 1, color);
      this.drawRect(guiGraphics, dim.getLimitX() - 1, dim.y(), dim.getLimitX(), dim.y() + size, color);
      this.drawRect(guiGraphics, dim.x(), dim.getLimitY() - 1, dim.x() + size, dim.getLimitY(), color);
      this.drawRect(guiGraphics, dim.x(), dim.getLimitY() - size, dim.x() + 1, dim.getLimitY(), color);
      this.drawRect(guiGraphics, dim.getLimitX() - size, dim.getLimitY() - 1, dim.getLimitX(), dim.getLimitY(), color);
      this.drawRect(guiGraphics, dim.getLimitX() - 1, dim.getLimitY() - size, dim.getLimitX(), dim.getLimitY(), color);
   }
}
