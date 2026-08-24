package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.render.ColorGradientRenderState;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import dev.isxander.yacl3.gui.utils.WidgetUtils;
import dev.isxander.yacl3.platform.YACLPlatform;
import java.awt.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ColorPickerWidget extends ControllerPopupWidget<ColorController> {
   public static final ResourceLocation COLOR_PICKER_SPRITE = YACLPlatform.rl("controller/colorpicker");
   public static final ResourceLocation TRANSPARENT_SPRITE = YACLPlatform.rl("controller/transparent");
   private final ColorController controller;
   private final ColorController.ColorControllerElement entryWidget;
   protected MutableDimension<Integer> colorPickerDim;
   protected MutableDimension<Integer> previewColorDim;
   protected MutableDimension<Integer> saturationLightDim;
   protected MutableDimension<Integer> hueGradientDim;
   protected MutableDimension<Integer> alphaGradientDim;
   private boolean mouseDown;
   private boolean hueSliderDown;
   private boolean satLightGradientDown;
   private boolean alphaSliderDown;
   private int hueThumbX;
   private int satLightThumbX;
   private int alphaThumbX;
   private boolean charTyped;
   private final int outline = 1;
   private final int previewPortion = 7;
   private final int sliderHeight = 7;
   private final int paddingX = 1;
   private final int paddingY = 3;
   private float[] HSL;
   private float hue;
   private float saturation;
   private float light;
   private int alpha;

   public ColorPickerWidget(ColorController control, YACLScreen screen, Dimension<Integer> dim, ColorController.ColorControllerElement entryWidget) {
      super(control, screen, dim, entryWidget);
      this.controller = control;
      this.entryWidget = entryWidget;
      this.setDimension(dim);
      this.updateHSL();
      this.setThumbX();
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      this.updateHSL();
      int thumbWidth = 4;
      int thumbHeight = 4;
      GuiUtils.pushPose(graphics);
      GuiUtils.translateZ(graphics, 10.0F);
      GuiUtils.blitSprite(
         graphics,
         COLOR_PICKER_SPRITE,
         this.colorPickerDim.x() - 5,
         this.colorPickerDim.y() - 5,
         this.colorPickerDim.width() + 10,
         this.colorPickerDim.height() + 10
      );
      graphics.fill(
         this.previewColorDim.x() - 1, this.previewColorDim.y() - 1, this.previewColorDim.xLimit() + 1, this.previewColorDim.yLimit() + 1, Color.black.getRGB()
      );
      if (this.controller.allowAlpha()) {
         GuiUtils.blitSprite(
            graphics, TRANSPARENT_SPRITE, this.previewColorDim.x(), this.previewColorDim.y(), this.previewColorDim.width(), this.previewColorDim.height()
         );
      }

      graphics.fill(
         this.previewColorDim.x(),
         this.previewColorDim.y(),
         this.previewColorDim.xLimit(),
         this.previewColorDim.yLimit(),
         this.controller.option().pendingValue().getRGB()
      );
      graphics.fill(
         this.saturationLightDim.x() - 1,
         this.saturationLightDim.y() - 1,
         this.saturationLightDim.xLimit() + 1,
         this.saturationLightDim.yLimit() + 1,
         Color.black.getRGB()
      );
      ColorGradientRenderState.createHorizontal(
            graphics,
            this.saturationLightDim.x(),
            this.saturationLightDim.y(),
            this.saturationLightDim.xLimit(),
            this.saturationLightDim.yLimit(),
            -1,
            GuiUtils.putAlpha((int)this.getRgbFromHueX(), 255)
         )
         .submit(graphics);
      graphics.fillGradient(
         this.saturationLightDim.x(), this.saturationLightDim.y(), this.saturationLightDim.xLimit(), this.saturationLightDim.yLimit(), 0, -16777216
      );
      graphics.fill(
         this.satLightThumbX - thumbWidth / 2 - 2,
         this.getSatLightThumbY() + thumbHeight / 2 + 2,
         this.satLightThumbX + thumbWidth / 2 + 1,
         this.getSatLightThumbY() - thumbHeight / 2 - 1,
         -12566464
      );
      graphics.fill(
         this.satLightThumbX - thumbWidth / 2 - 1,
         this.getSatLightThumbY() + thumbHeight / 2 + 1,
         this.satLightThumbX + thumbWidth / 2,
         this.getSatLightThumbY() - thumbHeight / 2,
         -1
      );
      graphics.fill(
         this.hueGradientDim.x() - 1, this.hueGradientDim.y() - 1, this.hueGradientDim.xLimit() + 1, this.hueGradientDim.yLimit() + 1, Color.black.getRGB()
      );
      this.drawRainbowGradient(graphics, this.hueGradientDim.x(), this.hueGradientDim.y(), this.hueGradientDim.xLimit(), this.hueGradientDim.yLimit());
      graphics.fill(
         this.hueThumbX - thumbWidth / 2 - 1,
         this.hueGradientDim.y() - 1 - 1,
         this.hueThumbX + thumbWidth / 2 + 1,
         this.hueGradientDim.yLimit() + 1 + 1,
         -12566464
      );
      graphics.fill(this.hueThumbX - thumbWidth / 2, this.hueGradientDim.y() - 1, this.hueThumbX + thumbWidth / 2, this.hueGradientDim.yLimit() + 1, -1);
      if (this.controller.allowAlpha()) {
         graphics.fill(
            this.alphaGradientDim.x() - 1,
            this.alphaGradientDim.y() - 1,
            this.alphaGradientDim.xLimit() + 1,
            this.alphaGradientDim.yLimit() + 1,
            Color.black.getRGB()
         );
         GuiUtils.blitSprite(graphics, TRANSPARENT_SPRITE, this.alphaGradientDim.x(), this.alphaGradientDim.y(), this.alphaGradientDim.width(), 7);
         ColorGradientRenderState.createHorizontal(
               graphics,
               this.alphaGradientDim.x(),
               this.alphaGradientDim.y(),
               this.alphaGradientDim.xLimit(),
               this.alphaGradientDim.yLimit(),
               GuiUtils.putAlpha(this.getRgbWithoutAlpha(), 255),
               0
            )
            .submit(graphics);
         graphics.fill(
            this.alphaThumbX - thumbWidth / 2 - 1,
            this.alphaGradientDim.y() - 1 - 1,
            this.alphaThumbX + thumbWidth / 2 + 1,
            this.alphaGradientDim.yLimit() + 1 + 1,
            -12566464
         );
         graphics.fill(
            this.alphaThumbX - thumbWidth / 2, this.alphaGradientDim.y() - 1, this.alphaThumbX + thumbWidth / 2, this.alphaGradientDim.yLimit() + 1, -1
         );
      }

      GuiUtils.popPose(graphics);
   }

   private boolean isHoveringHueSlider(double mouseX, double mouseY) {
      return mouseY >= this.hueGradientDim.y().intValue()
         && mouseY <= this.hueGradientDim.yLimit().intValue()
         && mouseX >= this.hueGradientDim.x().intValue()
         && mouseX <= this.hueGradientDim.xLimit().intValue();
   }

   public boolean clickedHueSlider(double mouseX, double mouseY) {
      if (!this.satLightGradientDown && !this.alphaSliderDown) {
         if (this.isHoveringHueSlider(mouseX, mouseY)) {
            this.hueSliderDown = true;
         }

         if (this.hueSliderDown) {
            this.hueThumbX = (int)Mth.clamp(mouseX, this.hueGradientDim.x().intValue(), this.hueGradientDim.xLimit().intValue());
         }

         return this.hueSliderDown;
      } else {
         return false;
      }
   }

   private boolean isHoveringSatLightGradient(double mouseX, double mouseY) {
      return mouseY >= this.saturationLightDim.y().intValue()
         && mouseY <= this.saturationLightDim.yLimit().intValue()
         && mouseX >= this.saturationLightDim.x().intValue()
         && mouseX <= this.saturationLightDim.xLimit().intValue();
   }

   public boolean clickedSatLightGradient(double mouseX, double mouseY) {
      if (!this.hueSliderDown && !this.alphaSliderDown) {
         if (this.isHoveringSatLightGradient(mouseX, mouseY)) {
            this.satLightGradientDown = true;
         }

         if (this.satLightGradientDown) {
            this.satLightThumbX = (int)Mth.clamp(mouseX, this.saturationLightDim.x().intValue(), this.saturationLightDim.xLimit().intValue());
         }

         return this.satLightGradientDown;
      } else {
         return false;
      }
   }

   private boolean isHoveringAlphaSlider(double mouseX, double mouseY) {
      return this.alphaGradientDim == null
         ? false
         : mouseY >= this.alphaGradientDim.y().intValue()
            && mouseY <= this.alphaGradientDim.yLimit().intValue()
            && mouseX >= this.alphaGradientDim.x().intValue()
            && mouseX <= this.alphaGradientDim.xLimit().intValue();
   }

   public boolean clickedAlphaSlider(double mouseX, double mouseY) {
      if (!this.satLightGradientDown && !this.hueSliderDown) {
         if (this.isHoveringAlphaSlider(mouseX, mouseY)) {
            this.alphaSliderDown = true;
         }

         if (this.alphaSliderDown) {
            this.alphaThumbX = (int)Mth.clamp(mouseX, this.alphaGradientDim.x().intValue(), this.alphaGradientDim.xLimit().intValue());
         }

         return this.alphaSliderDown;
      } else {
         return false;
      }
   }

   public void setColorFromMouseClick(double mouseX, double mouseY) {
      if (this.clickedSatLightGradient(mouseX, mouseY)) {
         this.setSatLightFromMouse(mouseX, mouseY);
      } else if (this.clickedHueSlider(mouseX, mouseY)) {
         this.setHueFromMouse(mouseX);
      } else if (this.controller.allowAlpha() && this.clickedAlphaSlider(mouseX, mouseY)) {
         this.setAlphaFromMouse(mouseX);
      }
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int button) {
      if (this.isMouseOver(mouseX, mouseY)) {
         this.mouseDown = true;
         this.hueSliderDown = false;
         this.satLightGradientDown = false;
         this.alphaSliderDown = false;
         this.setColorFromMouseClick(mouseX, mouseY);
         return true;
      } else if (this.entryWidget.isMouseOver(mouseX, mouseY)) {
         return WidgetUtils.mouseClicked(this.entryWidget, mouseX, mouseY, button);
      } else {
         this.close();
         return false;
      }
   }

   @Override
   public boolean onMouseReleased(double mouseX, double mouseY, int button) {
      this.mouseDown = false;
      return false;
   }

   @Override
   public boolean isMouseOver(double mouseX, double mouseY) {
      return mouseX >= this.colorPickerDim.x() - 1 - 3
         && mouseX <= this.colorPickerDim.xLimit() + 1 + 3
         && mouseY >= this.colorPickerDim.y() - 1 - 3
         && mouseY <= this.colorPickerDim.yLimit() + 1 + 3;
   }

   @Override
   public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (!this.mouseDown && !this.isMouseOver(mouseX, mouseY)) {
         return this.entryWidget.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
      } else {
         this.setColorFromMouseClick(mouseX, mouseY);
         return true;
      }
   }

   @Override
   public boolean onCharTyped(char chr, String cpStr, int modifiers) {
      this.charTyped = true;
      return this.entryWidget.onCharTyped(chr, cpStr, modifiers);
   }

   @Override
   public void setDimension(Dimension<Integer> dim) {
      super.setDimension(dim);
      int colorPickerHeight = dim.height() * 2 + 7;
      int colorPickerX = dim.centerX() - this.getXPadding() * 2;
      int colorPickerY = dim.y() - colorPickerHeight - 7;
      int alphaSliderHeight = 0;
      if (this.controller.allowAlpha()) {
         alphaSliderHeight = 11;
         colorPickerHeight += alphaSliderHeight;
         colorPickerY -= alphaSliderHeight;
      }

      if (colorPickerY < this.screen.tabArea.top()) {
         colorPickerY = dim.yLimit() + 7;
      }

      this.colorPickerDim = Dimension.ofInt(colorPickerX, colorPickerY, dim.xLimit() - colorPickerX, colorPickerHeight);
      this.previewColorDim = Dimension.ofInt(
         this.colorPickerDim.x(),
         this.colorPickerDim.y(),
         this.colorPickerDim.x() + this.colorPickerDim.xLimit() / 7 - 1 - this.colorPickerDim.x(),
         this.colorPickerDim.yLimit() - 7 - 3 - this.colorPickerDim.y() - alphaSliderHeight
      );
      this.saturationLightDim = Dimension.ofInt(
         this.colorPickerDim.x() + this.colorPickerDim.xLimit() / 7 + 1 + 1,
         this.colorPickerDim.y(),
         this.colorPickerDim.xLimit() - (this.colorPickerDim.x() + this.colorPickerDim.xLimit() / 7 + 1 + 1),
         this.colorPickerDim.yLimit() - 7 - 3 - this.colorPickerDim.y() - alphaSliderHeight
      );
      this.hueGradientDim = Dimension.ofInt(this.colorPickerDim.x(), this.colorPickerDim.yLimit() - 7 - alphaSliderHeight, this.colorPickerDim.width(), 7);
      if (this.controller.allowAlpha()) {
         this.alphaGradientDim = Dimension.ofInt(this.hueGradientDim.x(), this.hueGradientDim.y() + alphaSliderHeight, this.hueGradientDim.width(), 7);
      }
   }

   @Override
   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.entryWidget.hoveredOverColorPreview = this.entryWidget.isMouseOverColorPreview(mouseX, mouseY);
   }

   @Override
   public void close() {
      this.entryWidget.removeColorPicker();
   }

   @Override
   public Component popupTitle() {
      return Component.translatable("yacl.control.color.color_picker_title");
   }

   public void setThumbX() {
      this.hueThumbX = this.getHueThumbX();
      this.satLightThumbX = this.getSatLightThumbX();
      if (this.controller.allowAlpha()) {
         this.alphaThumbX = this.getAlphaThumbX();
      }
   }

   protected int getHueThumbX() {
      int min = this.hueGradientDim.x();
      int max = this.hueGradientDim.xLimit();
      int value = (int)(min + this.hueGradientDim.width().intValue() * this.hue);
      return Mth.clamp(value, min, max);
   }

   protected int getSatLightThumbX() {
      int min = this.saturationLightDim.x();
      int max = this.saturationLightDim.xLimit();
      int value = (int)(min + this.saturationLightDim.width().intValue() * this.saturation);
      return Mth.clamp(value, min, max);
   }

   protected int getSatLightThumbY() {
      int min = this.saturationLightDim.y();
      int max = this.saturationLightDim.yLimit();
      int value = (int)(min + this.saturationLightDim.height().intValue() * (1.0F - this.light));
      return Mth.clamp(value, min, max);
   }

   protected int getAlphaThumbX() {
      int min = this.alphaGradientDim.x();
      int max = this.alphaGradientDim.xLimit();
      int value = max - this.alphaGradientDim.width() * this.alpha / 255;
      return Mth.clamp(value, min, max);
   }

   public void setHueFromMouse(double mouseX) {
      if (mouseX < this.hueGradientDim.x().intValue()) {
         this.hue = 0.0F;
      } else if (mouseX > this.hueGradientDim.xLimit().intValue()) {
         this.hue = 1.0F;
      } else {
         float newHue = (float)(mouseX - this.hueGradientDim.x().intValue()) / this.hueGradientDim.width().intValue();
         this.hue = Mth.clamp(newHue, 0.0F, 1.0F);
      }

      this.setColorControllerFromHSL();
   }

   public void setSatLightFromMouse(double mouseX, double mouseY) {
      if (mouseX < this.saturationLightDim.x().intValue()) {
         this.saturation = 0.0F;
      } else if (mouseX > this.saturationLightDim.xLimit().intValue()) {
         this.saturation = 1.0F;
      } else {
         float newSat = (float)(mouseX - this.saturationLightDim.x().intValue()) / this.saturationLightDim.width().intValue();
         this.saturation = Mth.clamp(newSat, 0.0F, 1.0F);
      }

      if (mouseY < this.saturationLightDim.y().intValue()) {
         this.light = 1.0F;
      } else if (mouseY > this.saturationLightDim.yLimit().intValue()) {
         this.light = 0.0F;
      } else {
         float newLight = (float)(mouseY - this.saturationLightDim.y().intValue()) / this.saturationLightDim.height().intValue();
         this.light = Mth.clamp(1.0F - newLight, 0.0F, 1.0F);
      }

      this.setColorControllerFromHSL();
   }

   public void setAlphaFromMouse(double mouseX) {
      if (mouseX < this.alphaGradientDim.x().intValue()) {
         this.alpha = 255;
      } else if (mouseX > this.alphaGradientDim.xLimit().intValue()) {
         this.alpha = 0;
      } else {
         int newAlpha = (int)((mouseX - this.alphaGradientDim.xLimit().intValue()) / this.alphaGradientDim.width().intValue() * -255.0);
         this.alpha = Mth.clamp(newAlpha, 0, 255);
      }

      this.setColorControllerFromHSL();
   }

   public void setColorControllerFromHSL() {
      float trueHue = (float)(this.hueThumbX - this.colorPickerDim.x()) / this.colorPickerDim.width().intValue();
      Color color = Color.getHSBColor(trueHue, this.saturation, this.light);
      Color returnColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), this.alpha);
      this.controller.option().requestSet(returnColor);
   }

   protected void updateHSL() {
      this.HSL = this.getHSL();
      this.hue = this.hue();
      this.saturation = this.saturation();
      this.light = this.light();
      this.alpha = this.getAlpha();
      if (this.charTyped) {
         this.setThumbX();
         this.charTyped = false;
      }
   }

   protected float[] getHSL() {
      Color pendingValue = this.controller.option().pendingValue();
      return Color.RGBtoHSB(pendingValue.getRed(), pendingValue.getGreen(), pendingValue.getBlue(), null);
   }

   protected float hue() {
      return this.HSL[0];
   }

   protected float saturation() {
      return this.HSL[1];
   }

   protected float light() {
      return this.HSL[2];
   }

   protected int getAlpha() {
      return this.controller.option().pendingValue().getAlpha();
   }

   protected float getRgbFromHueX() {
      float trueHue = (float)(this.hueThumbX - this.colorPickerDim.x()) / this.colorPickerDim.width().intValue();
      return Color.HSBtoRGB(trueHue, 1.0F, 1.0F);
   }

   protected int getRgbWithoutAlpha() {
      Color pendingColor = this.controller.option().pendingValue();
      Color returnColor = new Color(pendingColor.getRed(), pendingColor.getGreen(), pendingColor.getBlue(), 255);
      return returnColor.getRGB();
   }
}
