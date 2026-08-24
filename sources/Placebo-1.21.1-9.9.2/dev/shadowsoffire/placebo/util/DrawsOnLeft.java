package dev.shadowsoffire.placebo.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public interface DrawsOnLeft {
   default void drawOnLeft(GuiGraphics gfx, List<? extends FormattedText> list, int y) {
      if (!list.isEmpty()) {
         int xPos = this.__ths().getGuiLeft() - 16 - list.stream().<Integer>map(this.__ths().font::width).max(Integer::compare).get();
         int maxWidth = 9999;
         if (xPos < 0) {
            maxWidth = this.__ths().getGuiLeft() - 6;
            xPos = -8;
         }

         List<FormattedText> split = new ArrayList<>();
         int _maxWidth = maxWidth;
         list.forEach(text -> {
            Style style = text instanceof Component comp ? comp.getStyle() : Style.EMPTY;
            this.__ths().font.getSplitter().splitLines(text, _maxWidth, style, (splitLine, isBlank) -> split.add(splitLine));
         });
         gfx.renderComponentTooltip(this.__ths().font, split, xPos, y, ItemStack.EMPTY);
      }
   }

   default void drawOnLeft(GuiGraphics gfx, List<? extends FormattedText> list, int y, int maxWidth) {
      if (!list.isEmpty()) {
         List<FormattedText> split = new ArrayList<>();
         list.forEach(text -> {
            Style style = text instanceof Component comp ? comp.getStyle() : Style.EMPTY;
            this.__ths().font.getSplitter().splitLines(text, maxWidth, style, (splitLine, isBlank) -> split.add(splitLine));
         });
         int xPos = this.__ths().getGuiLeft() - 16 - split.stream().<Integer>map(this.__ths().font::width).max(Integer::compare).get();
         gfx.renderComponentTooltip(this.__ths().font, split, xPos, y, ItemStack.EMPTY);
      }
   }

   default AbstractContainerScreen<?> __ths() {
      return (AbstractContainerScreen<?>)this;
   }

   static void draw(AbstractContainerScreen<?> screen, GuiGraphics gfx, List<Component> list, int y) {
      ((DrawsOnLeft)screen).drawOnLeft(gfx, list, y);
   }
}
