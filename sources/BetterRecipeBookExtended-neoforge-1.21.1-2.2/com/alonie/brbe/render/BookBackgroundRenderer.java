package com.alonie.brbe.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class BookBackgroundRenderer {
   private static final ResourceLocation RECIPE_BOOK_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/recipe_book.png");

   public void render(GuiGraphics gui, int left, int top, int width, boolean expanded) {
      int height = 166;
      if (!expanded) {
         gui.blit(RECIPE_BOOK_TEXTURE, left, top, 1.0F, 1.0F, width, height, 256, 256);
      } else {
         this.renderExpanded(gui, left, top, width, height);
      }
   }

   private void renderExpanded(GuiGraphics gui, int left, int top, int width, int height) {
      int texSize = 256;
      int leftCap = 32;
      int rightCap = 12;
      int bodyWidth = 103;
      int bookTexW = 147;
      gui.blit(RECIPE_BOOK_TEXTURE, left, top, 1.0F, 1.0F, leftCap, height, texSize, texSize);
      int bodyX = left + leftCap;
      int remainingWidth = width - leftCap - rightCap;
      int uvLeft = leftCap + 1;
      int uvRight = uvLeft + bodyWidth;
      int filled = 0;

      while (filled < remainingWidth) {
         int segmentWidth = Math.min(bodyWidth, remainingWidth - filled);
         gui.blit(RECIPE_BOOK_TEXTURE, bodyX + filled, top, uvLeft, 1.0F, segmentWidth, height, texSize, texSize);
         filled += segmentWidth;
      }

      int rightX = bodyX + remainingWidth;
      int uvRightCap = bookTexW - rightCap + 1;
      gui.blit(RECIPE_BOOK_TEXTURE, rightX, top, uvRightCap, 1.0F, rightCap, height, texSize, texSize);
   }
}
