package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TickBoxController implements Controller<Boolean> {
   private final Option<Boolean> option;

   public TickBoxController(Option<Boolean> option) {
      this.option = option;
   }

   @Override
   public Option<Boolean> option() {
      return this.option;
   }

   @Override
   public Component formatValue() {
      return Component.empty();
   }

   @Override
   public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
      return new TickBoxController.TickBoxControllerElement(this, screen, widgetDimension);
   }

   public static class TickBoxControllerElement extends ControllerWidget<TickBoxController> {
      public TickBoxControllerElement(TickBoxController control, YACLScreen screen, Dimension<Integer> dim) {
         super(control, screen, dim);
      }

      @Override
      protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
         int outlineSize = 10;
         int outlineX1 = this.getDimension().xLimit() - this.getXPadding() - outlineSize;
         int outlineY1 = this.getDimension().centerY() - outlineSize / 2;
         int outlineX2 = this.getDimension().xLimit() - this.getXPadding();
         int outlineY2 = this.getDimension().centerY() + outlineSize / 2;
         int color = this.getValueColor();
         int shadowColor = this.multiplyColor(color, 0.25F);
         this.drawOutline(graphics, outlineX1 + 1, outlineY1 + 1, outlineX2 + 1, outlineY2 + 1, 1, shadowColor);
         this.drawOutline(graphics, outlineX1, outlineY1, outlineX2, outlineY2, 1, color);
         if (this.control.option().pendingValue()) {
            graphics.fill(outlineX1 + 3, outlineY1 + 3, outlineX2 - 1, outlineY2 - 1, shadowColor);
            graphics.fill(outlineX1 + 2, outlineY1 + 2, outlineX2 - 2, outlineY2 - 2, color);
         }

         if (this.hovered) {
         }
      }

      @Override
      public boolean onMouseClicked(double mouseX, double mouseY, int button) {
         if (this.isMouseOver(mouseX, mouseY) && this.isAvailable()) {
            this.toggleSetting();
            return true;
         } else {
            return false;
         }
      }

      @Override
      protected int getHoveredControlWidth() {
         return 10;
      }

      @Override
      protected int getUnhoveredControlWidth() {
         return 10;
      }

      public void toggleSetting() {
         this.control.option().requestSet(!this.control.option().pendingValue());
         this.playDownSound();
      }

      @Override
      public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
         if (!this.focused) {
            return false;
         } else if (keyCode != 257 && keyCode != 32 && keyCode != 335) {
            return false;
         } else {
            this.toggleSetting();
            return true;
         }
      }
   }
}
