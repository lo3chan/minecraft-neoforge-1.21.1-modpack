package com.teamresourceful.resourcefulconfig.client.components.options.types;

import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import com.teamresourceful.resourcefulconfig.client.components.options.range.OptionRange;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class RangeOptionWidget extends BaseWidget {
   private static final int FOCUSED_EXTRA_WIDTH = 40;
   private static final int WIDTH = 80;
   private static final int FOCUSED_WIDTH = 120;
   private static final int PADDING = 20;
   private final Supplier<Component> display;
   private final Component minDisplay;
   private final Component maxDisplay;
   private final DoubleSupplier getter;
   private final DoubleConsumer setter;
   private final double step;
   private final boolean canBeFocused;
   private int padding = 5;
   private boolean canChangeValue;

   public RangeOptionWidget(OptionRange range) {
      this(range::toComponent, range.minComponent(), range.maxComponent(), range::getPercent, range::setPercent, range.getStepPercent());
   }

   public RangeOptionWidget(Supplier<Component> display, Component minDisplay, Component maxDisplay, DoubleSupplier getter, DoubleConsumer setter, Double step) {
      super(80, 16);
      this.display = display;
      this.minDisplay = minDisplay;
      this.maxDisplay = maxDisplay;
      this.getter = getter;
      this.setter = setter;
      this.step = step;
      this.canBeFocused = this.minDisplay != null && this.maxDisplay != null;
   }

   @Override
   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      this.updateIfFocused();
      graphics.blitSprite(ModSprites.BUTTON, this.getX(), this.getY(), this.width, this.height);
      graphics.blitSprite(ModSprites.BUTTON_HOVER, this.getX() + this.padding, this.getY() + 5, this.width - this.padding * 2, this.height - 10);
      int sliderX = this.getX() + this.padding + (int)((this.width - this.padding * 2) * this.getter.getAsDouble()) - (this.height - 6) / 2;
      graphics.blitSprite(ModSprites.CONTAINER, sliderX, this.getY() + 4, this.height - 8, this.height - 8);
      Component tooltip = null;
      if (mouseX >= this.getX() + this.padding
         && mouseX <= this.getX() + this.width - this.padding
         && mouseY >= this.getY() + 4
         && mouseY <= this.getY() + this.height - 4) {
         tooltip = this.display.get();
      }

      if (this.isHoveredOrFocused() && this.canBeFocused) {
         renderScrollingString(
            graphics, this.font, this.minDisplay, this.getX() + 2, this.getY() + 2, this.getX() + this.padding - 2, this.getY() + this.height - 2, 16777215
         );
         renderScrollingString(
            graphics,
            this.font,
            this.maxDisplay,
            this.getX() + this.width - this.padding + 2,
            this.getY() + 2,
            this.getX() + this.width - 2,
            this.getY() + this.height - 2,
            16777215
         );
         if (mouseX >= this.getX() + 2 && mouseX <= this.getX() + this.padding - 2) {
            tooltip = this.minDisplay;
         } else if (mouseX >= this.getX() + this.width - this.padding + 2 && mouseX <= this.getX() + this.width - 2) {
            tooltip = this.maxDisplay;
         }
      }

      if (tooltip != null && Minecraft.getInstance().screen != null && this.isHovered()) {
         Minecraft.getInstance().screen.setTooltipForNextRenderPass(List.of(tooltip.getVisualOrderText()));
      }
   }

   public void updateIfFocused() {
      if (this.canBeFocused) {
         if (this.width != 120 && this.isHoveredOrFocused()) {
            this.setWidth(120);
            this.setX(this.getX() - 40);
            this.padding = 20;
         } else if (this.width != 80 && !this.isHoveredOrFocused()) {
            this.setWidth(80);
            this.setX(this.getX() + 40);
            this.padding = 5;
         }
      }
   }

   public void setFocused(boolean focused) {
      super.setFocused(focused);
      if (!focused) {
         this.canChangeValue = false;
      } else {
         InputType type = Minecraft.getInstance().getLastInputType();
         if (type.isMouse() || type == InputType.KEYBOARD_TAB) {
            this.canChangeValue = true;
         }
      }
   }

   public boolean mouseClicked(double d, double e, int i) {
      if (this.isHovered()) {
         this.setValueFromMouse(d);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseDragged(double d, double e, int i, double f, double g) {
      if (this.isHovered() && i == 0) {
         this.setValueFromMouse(d);
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int i, int j, int k) {
      if (CommonInputs.selected(i)) {
         this.canChangeValue = !this.canChangeValue;
         return true;
      } else {
         if (this.canChangeValue) {
            boolean leftArrow = i == 263;
            if (leftArrow || i == 262) {
               double step = leftArrow ? -this.step : this.step;
               step *= Screen.hasShiftDown() ? 10.0 : 1.0;
               double value = (this.getter.getAsDouble() + step) / (this.width - 8);
               this.setter.accept(Mth.clamp(value, 0.0, 1.0));
               return true;
            }
         }

         return false;
      }
   }

   private void setValueFromMouse(double mouseX) {
      if (this.isFocused()) {
         double correctX = mouseX - (this.getX() + this.padding);
         double value = correctX / (this.getWidth() - this.padding * 2);
         this.setter.accept(Mth.clamp(value, 0.0, 1.0));
      }
   }
}
