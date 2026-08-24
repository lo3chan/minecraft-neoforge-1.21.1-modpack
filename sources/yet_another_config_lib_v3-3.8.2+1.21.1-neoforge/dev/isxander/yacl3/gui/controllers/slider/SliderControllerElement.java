package dev.isxander.yacl3.gui.controllers.slider;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import dev.isxander.yacl3.gui.utils.KeyUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class SliderControllerElement extends ControllerWidget<ISliderController<?>> {
   private final double min;
   private final double max;
   private final double interval;
   private float interpolation;
   private Dimension<Integer> sliderBounds;
   private boolean mouseDown = false;

   public SliderControllerElement(ISliderController<?> option, YACLScreen screen, Dimension<Integer> dim, double min, double max, double interval) {
      super(option, screen, dim);
      this.min = min;
      this.max = max;
      this.interval = interval;
      this.setDimension(dim);
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      super.render(graphics, mouseX, mouseY, delta);
      this.calculateInterpolation();
   }

   @Override
   protected void drawHoveredControl(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      graphics.fill(this.sliderBounds.x(), this.sliderBounds.centerY() - 1, this.sliderBounds.xLimit(), this.sliderBounds.centerY(), -1);
      graphics.fill(this.sliderBounds.x() + 1, this.sliderBounds.centerY(), this.sliderBounds.xLimit() + 1, this.sliderBounds.centerY() + 1, -12566464);
      graphics.fill(
         this.getThumbX() - this.getThumbWidth() / 2 + 1,
         this.sliderBounds.y() + 1,
         this.getThumbX() + this.getThumbWidth() / 2 + 1,
         this.sliderBounds.yLimit() + 1,
         -12566464
      );
      graphics.fill(
         this.getThumbX() - this.getThumbWidth() / 2, this.sliderBounds.y(), this.getThumbX() + this.getThumbWidth() / 2, this.sliderBounds.yLimit(), -1
      );
   }

   @Override
   protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      GuiUtils.pushPose(graphics);
      if (this.isHovered()) {
         GuiUtils.translate2D(graphics, -(this.sliderBounds.width() + 6 + this.getThumbWidth() / 2.0F), 0.0F);
      }

      super.drawValueText(graphics, mouseX, mouseY, delta);
      GuiUtils.popPose(graphics);
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int button) {
      if (this.isAvailable() && button == 0 && this.isHoveredSliderBounds(mouseX, mouseY)) {
         this.mouseDown = true;
         this.setValueFromMouse(mouseX);
         return true;
      } else {
         return false;
      }
   }

   private boolean isHoveredSliderBounds(double mouseX, double mouseY) {
      return this.sliderBounds.isPointInside((int)mouseX, (int)mouseY);
   }

   @Override
   public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (this.isAvailable() && button == 0 && this.mouseDown) {
         this.setValueFromMouse(mouseX);
         return true;
      } else {
         return false;
      }
   }

   public void incrementValue(double amount) {
      this.control.setPendingValue(Mth.clamp(this.control.pendingValue() + this.interval * amount, this.min, this.max));
      this.calculateInterpolation();
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
      if (this.isAvailable() && this.isMouseOver(mouseX, mouseY) && (KeyUtils.hasShiftDown() || KeyUtils.hasControlDown())) {
         this.incrementValue(vertical);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseReleased(double mouseX, double mouseY, int button) {
      if (this.isAvailable() && this.mouseDown) {
         this.playDownSound();
      }

      this.mouseDown = false;
      return super.onMouseReleased(mouseX, mouseY, button);
   }

   @Override
   public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
      if (!this.focused) {
         return false;
      } else {
         switch (keyCode) {
            case 262:
               this.incrementValue(1.0);
               break;
            case 263:
               this.incrementValue(-1.0);
               break;
            default:
               return false;
         }

         return true;
      }
   }

   @Override
   public boolean isMouseOver(double mouseX, double mouseY) {
      return super.isMouseOver(mouseX, mouseY) || this.mouseDown;
   }

   protected void setValueFromMouse(double mouseX) {
      double value = (mouseX - this.sliderBounds.x().intValue()) / this.sliderBounds.width().intValue() * this.control.range();
      this.control.setPendingValue(this.roundToInterval(value));
      this.calculateInterpolation();
   }

   protected double roundToInterval(double value) {
      return Mth.clamp(this.min + this.interval * Math.round(value / this.interval), this.min, this.max);
   }

   @Override
   protected int getHoveredControlWidth() {
      return this.sliderBounds.width() + this.getUnhoveredControlWidth() + 6 + this.getThumbWidth() / 2;
   }

   protected void calculateInterpolation() {
      this.interpolation = Mth.clamp((float)((this.control.pendingValue() - this.control.min()) * 1.0 / this.control.range()), 0.0F, 1.0F);
   }

   @Override
   public void setDimension(Dimension<Integer> dim) {
      super.setDimension(dim);
      int trackWidth = dim.width() / 3;
      if (this.optionNameString.isEmpty()) {
         trackWidth = dim.width() / 2;
      }

      this.sliderBounds = Dimension.ofInt(dim.xLimit() - this.getXPadding() - this.getThumbWidth() / 2 - trackWidth, dim.centerY() - 5, trackWidth, 10);
   }

   protected int getThumbX() {
      return (int)(this.sliderBounds.x().intValue() + this.sliderBounds.width().intValue() * this.interpolation);
   }

   protected int getThumbWidth() {
      return 4;
   }
}
