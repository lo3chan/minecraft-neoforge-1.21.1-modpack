package dev.isxander.yacl3.gui.controllers;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import dev.isxander.yacl3.platform.YACLConfig;
import java.awt.Color;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ColorController implements IStringController<Color> {
   private final Option<Color> option;
   private final boolean allowAlpha;

   public ColorController(Option<Color> option) {
      this(option, false);
   }

   public ColorController(Option<Color> option, boolean allowAlpha) {
      this.option = option;
      this.allowAlpha = allowAlpha;
   }

   @Override
   public Option<Color> option() {
      return this.option;
   }

   public boolean allowAlpha() {
      return this.allowAlpha;
   }

   @Override
   public String getString() {
      return this.formatValue().getString();
   }

   @Override
   public Component formatValue() {
      MutableComponent text = Component.literal("#");
      text.append(Component.literal(this.toHex(this.option().pendingValue().getRed())).withStyle(ChatFormatting.RED));
      text.append(Component.literal(this.toHex(this.option().pendingValue().getGreen())).withStyle(ChatFormatting.GREEN));
      text.append(Component.literal(this.toHex(this.option().pendingValue().getBlue())).withStyle(ChatFormatting.BLUE));
      if (this.allowAlpha()) {
         text.append(this.toHex(this.option().pendingValue().getAlpha()));
      }

      return text;
   }

   private String toHex(int value) {
      String hex = Integer.toString(value, 16).toUpperCase();
      if (hex.length() == 1) {
         hex = "0" + hex;
      }

      return hex;
   }

   @Override
   public void setFromString(String value) {
      if (value.startsWith("#")) {
         value = value.substring(1);
      }

      int red = Integer.parseInt(value.substring(0, 2), 16);
      int green = Integer.parseInt(value.substring(2, 4), 16);
      int blue = Integer.parseInt(value.substring(4, 6), 16);
      if (this.allowAlpha()) {
         int alpha = Integer.parseInt(value.substring(6, 8), 16);
         this.option().requestSet(new Color(red, green, blue, alpha));
      } else {
         this.option().requestSet(new Color(red, green, blue));
      }
   }

   @Override
   public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
      return new ColorController.ColorControllerElement(this, screen, widgetDimension);
   }

   public static class ColorControllerElement extends StringControllerElement {
      private final ColorController colorController;
      private ColorPickerWidget colorPickerWidget;
      protected MutableDimension<Integer> colorPreviewDim;
      private final List<Character> allowedChars;
      public boolean hoveredOverColorPreview = false;
      private boolean colorPickerVisible = false;
      private int previewOutlineFadeTicks = 0;

      public ColorControllerElement(ColorController control, YACLScreen screen, Dimension<Integer> dim) {
         super(control, screen, dim, true);
         this.colorController = control;
         this.allowedChars = ImmutableList.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', new Character[]{'c', 'd', 'e', 'f'});
      }

      @Override
      protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
         this.hovered = this.isMouseOver(mouseX, mouseY);
         if (this.isHovered()) {
            this.colorPreviewDim.move(-this.inputFieldBounds.width() - 8, -2);
            this.colorPreviewDim.expand(4, 4);
            this.previewOutlineFadeTicks++;
            super.drawValueText(graphics, mouseX, mouseY, delta);
         }

         int previewColor = this.colorController.option().pendingValue().getRGB();
         if (GuiUtils.extractAlpha(previewColor) < 255) {
            GuiUtils.blitSprite(
               graphics,
               ColorPickerWidget.TRANSPARENT_SPRITE,
               this.colorPreviewDim.x(),
               this.colorPreviewDim.y(),
               this.colorPreviewDim.width(),
               this.colorPreviewDim.height()
            );
         }

         graphics.fill(this.colorPreviewDim.x(), this.colorPreviewDim.y(), this.colorPreviewDim.xLimit(), this.colorPreviewDim.yLimit(), previewColor);
         boolean isMouseOverColorPreview = this.isMouseOverColorPreview(mouseX, mouseY);
         Color outlineColor = this.getPreviewOutlineColor(this.hoveredOverColorPreview || isMouseOverColorPreview);
         this.drawOutline(
            graphics,
            this.colorPreviewDim.x(),
            this.colorPreviewDim.y(),
            this.colorPreviewDim.xLimit(),
            this.colorPreviewDim.yLimit(),
            1,
            outlineColor.getRGB()
         );
         if (isMouseOverColorPreview) {
         }
      }

      @Override
      public void write(String string) {
         if (string.startsWith("0x")) {
            string = string.substring(2);
         }

         for (char chr : string.toCharArray()) {
            if (!this.allowedChars.contains(Character.toLowerCase(chr))) {
               return;
            }
         }

         if (this.caretPos != 0) {
            String trimmed = string.substring(0, Math.min(this.inputField.length() - this.caretPos, string.length()));
            if (this.modifyInput(builder -> builder.replace(this.caretPos, this.caretPos + trimmed.length(), trimmed))) {
               this.caretPos = this.caretPos + trimmed.length();
               this.setSelectionLength();
               this.updateControl();
            }
         }
      }

      @Override
      protected void doBackspace() {
         if (this.caretPos > 1 && this.modifyInput(builder -> builder.setCharAt(this.caretPos - 1, '0'))) {
            this.caretPos--;
            this.updateControl();
         }
      }

      @Override
      protected void doDelete() {
         if (this.caretPos >= 1 && this.modifyInput(builder -> builder.setCharAt(this.caretPos, '0'))) {
            this.updateControl();
         }
      }

      @Override
      protected boolean doCut() {
         return false;
      }

      @Override
      protected boolean doCopy() {
         return false;
      }

      @Override
      protected boolean doSelectAll() {
         return false;
      }

      protected void setSelectionLength() {
         this.selectionLength = this.caretPos < this.inputField.length() && this.caretPos > 0 ? 1 : 0;
      }

      @Override
      protected int getDefaultCaretPos() {
         return this.colorController.allowAlpha() ? 3 : 1;
      }

      @Override
      public void setDimension(Dimension<Integer> dim) {
         super.setDimension(dim);
         int previewSize = (dim.height() - this.getYPadding() * 2) / 2;
         this.colorPreviewDim = Dimension.ofInt(dim.xLimit() - this.getXPadding() - previewSize, dim.centerY() - previewSize / 2, previewSize, previewSize);
         if (this.colorPickerWidget != null) {
            this.colorPickerWidget.setDimension(this.colorPickerWidget.getDimension().withY(this.getDimension().y()));
            if (this.getDimension().y() < this.screen.tabArea.top() || this.getDimension().yLimit() > this.screen.tabArea.bottom()) {
               this.removeColorPicker();
            }
         }
      }

      @Override
      public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
         int prevSelectionLength = this.selectionLength;
         this.selectionLength = 0;
         if (super.onKeyPressed(keyCode, scanCode, modifiers)) {
            this.caretPos = Math.max(1, this.caretPos);
            this.setSelectionLength();
            return true;
         } else {
            this.selectionLength = prevSelectionLength;
            return false;
         }
      }

      @Override
      public boolean onMouseClicked(double mouseX, double mouseY, int button) {
         if (this.isMouseOverColorPreview(mouseX, mouseY)) {
            this.playDownSound();
            this.createOrRemoveColorPicker();
            if (YACLConfig.HANDLER.instance().showColorPickerIndicator) {
               YACLConfig.HANDLER.instance().showColorPickerIndicator = false;
               YACLConfig.HANDLER.save();
            }
         }

         this.caretPos = Math.max(1, this.caretPos);
         this.setSelectionLength();
         return true;
      }

      public boolean isMouseOverColorPreview(double mouseX, double mouseY) {
         return this.colorPreviewDim.isPointInside((int)mouseX, (int)mouseY);
      }

      public void createOrRemoveColorPicker() {
         this.colorPickerVisible = !this.colorPickerVisible;
         if (this.colorPickerVisible) {
            this.colorPickerWidget = this.createColorPicker();
            this.screen.addPopupControllerWidget(this.colorPickerWidget);
         } else {
            this.removeColorPicker();
         }
      }

      @Override
      public void unfocus() {
         if (this.colorPickerVisible) {
            this.removeColorPicker();
         }

         this.previewOutlineFadeTicks = 0;
         super.unfocus();
      }

      public Color getPreviewOutlineColor(boolean colorPreviewHovered) {
         Color outlineColor = new Color(-16777216);
         Color highlightedColor = this.getHighlightedOutlineColor();
         if (!this.hovered && !colorPreviewHovered) {
            this.previewOutlineFadeTicks = 0;
            return outlineColor;
         } else {
            int fadeInTicks = 80;
            int fadeOutTicks = fadeInTicks + 120;
            if (colorPreviewHovered) {
               this.previewOutlineFadeTicks = 0;
               return highlightedColor;
            } else {
               if (YACLConfig.HANDLER.instance().showColorPickerIndicator) {
                  if (this.previewOutlineFadeTicks <= fadeInTicks) {
                     return this.getFadedColor(outlineColor, highlightedColor, this.previewOutlineFadeTicks, fadeInTicks);
                  }

                  if (this.previewOutlineFadeTicks <= fadeOutTicks) {
                     return this.getFadedColor(highlightedColor, outlineColor, this.previewOutlineFadeTicks - fadeInTicks, fadeOutTicks - fadeInTicks);
                  }

                  if (this.previewOutlineFadeTicks >= fadeInTicks + fadeOutTicks + 10) {
                     this.previewOutlineFadeTicks = 0;
                  }
               }

               return outlineColor;
            }
         }
      }

      private Color getFadedColor(Color original, Color fadeToColor, int fadeTick, int maxFadeTicks) {
         int red = fadeToColor.getRed() - original.getRed();
         int green = fadeToColor.getGreen() - original.getGreen();
         int blue = fadeToColor.getBlue() - original.getBlue();
         return new Color(
            original.getRed() + red * fadeTick / maxFadeTicks,
            original.getGreen() + green * fadeTick / maxFadeTicks,
            original.getBlue() + blue * fadeTick / maxFadeTicks
         );
      }

      private Color getHighlightedOutlineColor() {
         Color pendingValue = this.colorController.option().pendingValue();
         float[] HSL = Color.RGBtoHSB(pendingValue.getRed(), pendingValue.getGreen(), pendingValue.getBlue(), null);
         Color highlightedColor = new Color(-1);
         if (HSL[1] < 0.1F && HSL[2] > 0.9F) {
            highlightedColor = new Color(-3750202);
         }

         return highlightedColor;
      }

      public ColorPickerWidget colorPickerWidget() {
         return this.colorPickerWidget;
      }

      public boolean colorPickerVisible() {
         return this.colorPickerVisible;
      }

      public ColorPickerWidget createColorPicker() {
         return new ColorPickerWidget(this.colorController, this.screen, this.getDimension(), this);
      }

      public void removeColorPicker() {
         this.screen.clearPopupControllerWidget();
         this.colorPickerVisible = false;
         this.colorPickerWidget = null;
         this.hoveredOverColorPreview = false;
      }
   }
}
