package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.SyntaxHighlighter;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField.StringView;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

public class SyntaxEditBox extends MultiLineEditBox {
   private static final int TEXT_COLOR = 0xFF000000 | ConfigGuiColors.TEXT;
   private static final int PLACEHOLDER_COLOR = 0xFF000000 | ConfigGuiColors.DESCRIPTION;
   private static final int CURSOR_COLOR = 0xFF000000 | ConfigGuiColors.TEXT;
   private static final int SELECTION_COLOR = -14005632;
   private final Font font;
   private final Component placeholder;
   private final SyntaxHighlighter highlighter;
   private long focusedTime = Util.getMillis();

   public SyntaxEditBox(Font font, int x, int y, int width, int height, Component placeholder, SyntaxHighlighter highlighter) {
      super(font, x, y, width, height, placeholder, placeholder);
      this.font = font;
      this.placeholder = placeholder;
      this.highlighter = highlighter;
   }

   public void setFocused(boolean focused) {
      super.setFocused(focused);
      if (focused) {
         this.focusedTime = Util.getMillis();
      }
   }

   protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      String value = this.textField.value();
      if (value.isEmpty() && !this.isFocused()) {
         graphics.drawWordWrap(
            this.font,
            this.placeholder,
            this.getX() + this.innerPadding(),
            this.getY() + this.innerPadding(),
            this.width - this.totalInnerPadding(),
            PLACEHOLDER_COLOR
         );
      } else {
         int lineHeight = 9;
         int textX = this.getX() + this.innerPadding();
         int y = this.getY() + this.innerPadding();
         this.renderSelection(graphics, value, textX, lineHeight);
         int cursor = this.textField.cursor();
         boolean showCursor = this.isFocused() && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L;
         int cursorX = textX;
         int cursorY = y;
         boolean placedCursor = false;

         for (StringView line : this.textField.iterateLines()) {
            if (this.withinContentAreaTopBottom(y, y + lineHeight)) {
               String lineText = value.substring(line.beginIndex(), line.endIndex());
               graphics.drawString(this.font, this.highlighter.highlightLine(lineText), textX, y, TEXT_COLOR);
            }

            if (!placedCursor && cursor >= line.beginIndex() && cursor <= line.endIndex()) {
               cursorX = textX + this.font.width(value.substring(line.beginIndex(), cursor));
               cursorY = y;
               placedCursor = true;
            }

            y += lineHeight;
         }

         if (showCursor && placedCursor && this.withinContentAreaTopBottom(cursorY, cursorY + lineHeight)) {
            if (cursor >= value.length()) {
               graphics.drawString(this.font, "_", cursorX, cursorY, CURSOR_COLOR);
            } else {
               graphics.fill(cursorX, cursorY - 1, cursorX + 1, cursorY + lineHeight, CURSOR_COLOR);
            }
         }
      }
   }

   private void renderSelection(GuiGraphics graphics, String value, int textX, int lineHeight) {
      if (this.textField.hasSelection()) {
         StringView selection = this.textField.getSelected();
         int y = this.getY() + this.innerPadding();

         for (StringView line : this.textField.iterateLines()) {
            if (selection.beginIndex() <= line.endIndex()) {
               if (line.beginIndex() > selection.endIndex()) {
                  break;
               }

               if (this.withinContentAreaTopBottom(y, y + lineHeight)) {
                  int from = this.font.width(value.substring(line.beginIndex(), Math.max(selection.beginIndex(), line.beginIndex())));
                  int to = selection.endIndex() > line.endIndex()
                     ? this.width - this.innerPadding()
                     : this.font.width(value.substring(line.beginIndex(), selection.endIndex()));
                  graphics.fill(RenderType.guiTextHighlight(), textX + from, y, textX + to, y + lineHeight, -14005632);
               }
            }

            y += lineHeight;
         }
      }
   }
}
