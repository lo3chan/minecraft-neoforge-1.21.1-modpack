package com.iafenvoy.jupiter.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class BadgeRenderer {
   public static void draw(GuiGraphics graphics, Font font, int x, int y, Component text, int color) {
      drawFrame(graphics, x, y, font.width(text) + 3, 9 + 2, color);
      graphics.drawString(font, text, x + 2, y + 2, -1);
   }

   public static void drawFrame(GuiGraphics graphics, int x, int y, int width, int height, int color) {
      fill(graphics, x, y, x + width, y + height, color);
      fill(graphics, x, y, x - 1, y + height, color);
      fill(graphics, x + width, y, x + width + 1, y + height, color);
      fill(graphics, x, y, x + width, y - 1, color);
      fill(graphics, x, y + height, x + width, y + height + 1, color);
   }

   public static void fill(GuiGraphics graphics, int minX, int minY, int maxX, int maxY, int color) {
      graphics.fill(minX, minY, maxX, maxY, color);
   }
}
