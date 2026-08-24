package mezz.jei.common.gui.elements;

import java.util.List;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.gui.widgets.ITextWidget;
import mezz.jei.common.config.DebugConfig;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.Pair;
import mezz.jei.common.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

public class TextWidget implements ITextWidget, IRecipeWidget {
   private final List<FormattedText> text;
   private ImmutableRect2i availableArea;
   private HorizontalAlignment horizontalAlignment;
   private VerticalAlignment verticalAlignment;
   private Font font;
   private int color;
   private boolean shadow;
   private int lineSpacing;
   @Nullable
   private List<FormattedText> wrappedText;
   private boolean truncated = false;

   public TextWidget(List<FormattedText> text, int xPos, int yPos, int maxWidth, int maxHeight) {
      this.availableArea = new ImmutableRect2i(xPos, yPos, maxWidth, maxHeight);
      Minecraft minecraft = Minecraft.getInstance();
      this.font = minecraft.font;
      this.color = -16777216;
      this.text = text;
      this.lineSpacing = 2;
      this.horizontalAlignment = HorizontalAlignment.LEFT;
      this.verticalAlignment = VerticalAlignment.TOP;
   }

   private void invalidateCachedValues() {
      this.wrappedText = null;
      this.truncated = false;
   }

   @Override
   public int getWidth() {
      return this.availableArea.width();
   }

   @Override
   public int getHeight() {
      return this.availableArea.height();
   }

   public TextWidget setPosition(int xPos, int yPos) {
      this.availableArea = this.availableArea.setPosition(xPos, yPos);
      this.invalidateCachedValues();
      return this;
   }

   public TextWidget setTextAlignment(HorizontalAlignment horizontalAlignment) {
      if (this.horizontalAlignment.equals(horizontalAlignment)) {
         return this;
      } else {
         this.horizontalAlignment = horizontalAlignment;
         this.invalidateCachedValues();
         return this;
      }
   }

   public TextWidget setTextAlignment(VerticalAlignment verticalAlignment) {
      if (this.verticalAlignment.equals(verticalAlignment)) {
         return this;
      } else {
         this.verticalAlignment = verticalAlignment;
         this.invalidateCachedValues();
         return this;
      }
   }

   @Override
   public ITextWidget setFont(Font font) {
      this.font = font;
      this.invalidateCachedValues();
      return this;
   }

   @Override
   public ITextWidget setColor(int color) {
      this.color = color;
      this.invalidateCachedValues();
      return this;
   }

   @Override
   public ITextWidget setLineSpacing(int lineSpacing) {
      this.lineSpacing = lineSpacing;
      this.invalidateCachedValues();
      return this;
   }

   @Override
   public ITextWidget setShadow(boolean shadow) {
      this.shadow = shadow;
      this.invalidateCachedValues();
      return this;
   }

   @Override
   public ScreenPosition getPosition() {
      return this.availableArea.getScreenPosition();
   }

   private List<FormattedText> calculateWrappedText() {
      if (this.wrappedText != null) {
         return this.wrappedText;
      } else {
         int lineHeight = this.getLineHeight();
         int maxLines = this.availableArea.height() / lineHeight;
         if (maxLines * lineHeight + 9 <= this.availableArea.height()) {
            maxLines++;
         }

         Pair<List<FormattedText>, Boolean> result = StringUtil.splitLines(this.font, this.text, this.availableArea.width(), maxLines);
         this.wrappedText = result.first();
         this.truncated = result.second();
         return this.wrappedText;
      }
   }

   private int getLineHeight() {
      return 9 + this.lineSpacing;
   }

   @Override
   public void drawWidget(GuiGraphics guiGraphics, double mouseX, double mouseY) {
      Language language = Language.getInstance();
      int lineHeight = this.getLineHeight();
      List<FormattedText> lines = this.calculateWrappedText();
      int yPos = this.getYPosStart(lineHeight, lines);

      for (FormattedText line : lines) {
         FormattedCharSequence charSequence = language.getVisualOrder(line);
         int xPos = this.getXPos(charSequence);
         guiGraphics.drawString(this.font, charSequence, xPos, yPos, this.color, this.shadow);
         yPos += lineHeight;
      }

      if (DebugConfig.isDebugGuisEnabled()) {
         guiGraphics.fill(0, 0, this.availableArea.width(), this.availableArea.height(), -1431655936);
      }
   }

   @Override
   public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY) {
      if (mouseX >= 0.0 && mouseX < this.availableArea.width() && mouseY >= 0.0 && mouseY < this.availableArea.height()) {
         this.calculateWrappedText();
         if (this.truncated) {
            tooltip.addAll(this.text);
         }
      }
   }

   private int getXPos(FormattedCharSequence text) {
      return this.getXPos(this.font.width(text));
   }

   private int getXPos(int lineWidth) {
      return this.horizontalAlignment.getXPos(this.availableArea.width(), lineWidth);
   }

   private int getYPosStart(int lineHeight, List<FormattedText> text) {
      int linesHeight = lineHeight * text.size() - this.lineSpacing - 1;
      return this.verticalAlignment.getYPos(this.availableArea.height(), linesHeight);
   }
}
