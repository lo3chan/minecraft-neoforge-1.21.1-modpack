package com.iafenvoy.origins.screen;

import com.iafenvoy.origins.util.math.TextAlignment;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ScrollingTextWidget extends AbstractStringWidget {
   private TextAlignment textAlignment = TextAlignment.CENTER;
   private final boolean hasShadow;

   public ScrollingTextWidget(int x, int y, int width, int height, Component text, boolean hasShadow, Font textRenderer) {
      super(x, y, width, height, text, textRenderer);
      this.hasShadow = hasShadow;
   }

   public void setAlignment(TextAlignment textAlignment) {
      this.textAlignment = textAlignment;
   }

   public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      int left = this.getX() + 2;
      int right = this.getX() + this.getWidth() - 2;
      int top = this.getY();
      int bottom = this.getY() + this.getHeight();
      drawScrollingText(graphics, this.getFont(), this.getMessage(), this.textAlignment, left, top, right, bottom, this.getColor(), this.hasShadow);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return false;
   }

   protected static void drawScrollingText(
      GuiGraphics graphics,
      Font textRenderer,
      Component text,
      TextAlignment textAlignment,
      int left,
      int top,
      int right,
      int bottom,
      int color,
      boolean hasShadow
   ) {
      int textWidth = textRenderer.width(text);
      int height = (top + bottom - 9) / 2 + 1;
      int width = right - left;
      Optional<Integer> horizontalAlignment = textAlignment.horizontal(left, right, textWidth);
      if (textWidth <= width && horizontalAlignment.isPresent()) {
         graphics.drawString(textRenderer, text, horizontalAlignment.get(), height, color, hasShadow);
      } else {
         int horizontalDiff = textWidth - width;
         double d = Util.getMillis() / 1000.0;
         double e = Math.max(horizontalDiff * 0.5, 3.0);
         double f = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * d / e)) / 2.0 + 0.5;
         double g = Mth.lerp(f, 0.0, horizontalDiff);
         graphics.enableScissor(left, top, right, bottom);
         graphics.drawString(textRenderer, text, left - (int)g, height, color, hasShadow);
         graphics.disableScissor();
      }
   }
}
