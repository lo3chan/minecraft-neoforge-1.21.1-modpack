package com.teamresourceful.resourcefulconfig.client.components.base;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ListWidget extends ContainerWidget {
   private static final int SCROLLBAR_WIDTH = 2;
   private static final int SCROLLBAR_PADDING = 4;
   private static final int OVERSCROLL = 2;
   protected final List<ListWidget.Item> items = new ArrayList<>();
   private double scroll = 0.0;
   private int lastHeight = 0;
   private boolean scrolling = false;

   public ListWidget(int x, int y, int width, int height) {
      super(x, y, width, height);
   }

   public void update(ListWidget old) {
      if (this.items.size() == old.items.size()) {
         if (this.height == old.height) {
            this.updateLastHeight();
            if (this.lastHeight == old.lastHeight) {
               this.scroll = old.scroll;
               this.scrolling = old.scrolling;
            }
         }
      }
   }

   public void add(ListWidget.Item item) {
      this.items.add(item);
      this.updateScrollBar();
   }

   @Override
   public void clear() {
      super.clear();
      this.items.clear();
   }

   @NotNull
   @Override
   public List<? extends GuiEventListener> children() {
      return this.items;
   }

   @Override
   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      boolean showsScrollBar = this.lastHeight > this.height;
      int actualWidth = this.getWidth() - (showsScrollBar ? 6 : 0);
      graphics.enableScissor(this.getX(), this.getY(), this.getX() + actualWidth, this.getY() + this.height);
      int y = this.getY() - (int)this.scroll + 1;
      this.lastHeight = 0;

      for (ListWidget.Item item : this.items) {
         item.setItemWidth(actualWidth);
         item.setX(this.getX());
         item.setY(y);
         item.render(graphics, mouseX, mouseY, partialTicks);
         y += item.getHeight();
         this.lastHeight = this.lastHeight + item.getHeight();
      }

      graphics.disableScissor();
      if (this.lastHeight > this.height) {
         int scrollBarHeight = (int)((double)this.height / this.lastHeight * this.height) - 8;
         int scrollBarX = this.getX() + this.width - 2 - 1;
         int scrollBarY = this.getY() + 4 + (int)(this.scroll / this.lastHeight * this.height);
         int scrollBarColor = this.isMouseOver(mouseX, mouseY)
               && mouseX >= scrollBarX
               && mouseX <= scrollBarX + 2
               && mouseY >= scrollBarY
               && mouseY <= scrollBarY + scrollBarHeight
            ? -986896
            : -4144960;
         graphics.fill(scrollBarX, scrollBarY, scrollBarX + 2, scrollBarY + scrollBarHeight, scrollBarColor);
      }
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      if (this.scrolling) {
         double scrollBarHeight = (double)this.height / this.lastHeight * this.height;
         double scrollBarDragY = dragY / (this.height - scrollBarHeight);
         this.scroll = Mth.clamp(this.scroll + scrollBarDragY * this.lastHeight, 0.0, Math.max(0, this.lastHeight - this.height + 2));
         return true;
      } else {
         return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      this.scroll = Mth.clamp(this.scroll - scrollY * 10.0, 0.0, Math.max(0, this.lastHeight - this.height + 2));
      return true;
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.isMouseOver(mouseX, mouseY)) {
         if (this.isMouseOverScrollBar(mouseX, mouseY)) {
            this.scrolling = true;
            return true;
         } else {
            return super.mouseClicked(mouseX, mouseY, button);
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseReleased(double d, double e, int i) {
      if (i == 0) {
         this.scrolling = false;
      }

      return super.mouseReleased(d, e, i);
   }

   private boolean isMouseOverScrollBar(double mouseX, double mouseY) {
      if (this.lastHeight <= this.height) {
         return false;
      } else {
         int scrollBarX = this.getX() + this.width - 2 - 1;
         return mouseX >= scrollBarX && mouseX <= scrollBarX + 2 && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
      }
   }

   protected void updateLastHeight() {
      boolean showsScrollBar = this.lastHeight > this.height;
      int actualWidth = this.getWidth() - (showsScrollBar ? 6 : 0);
      this.lastHeight = 0;
      int y = this.getY() - (int)this.scroll + 1;

      for (ListWidget.Item item : this.items) {
         item.setItemWidth(actualWidth);
         item.setX(this.getX());
         item.setY(y);
         this.lastHeight = this.lastHeight + item.getHeight();
         y += item.getHeight();
      }
   }

   protected void updateScrollBar() {
      this.updateLastHeight();
      this.scroll = Mth.clamp(this.scroll, 0.0, Math.max(0, this.lastHeight - this.height + 2));
   }

   public interface Item extends GuiEventListener, Renderable, NarratableEntry, LayoutElement {
      @NotNull
      default ScreenRectangle getRectangle() {
         return super.getRectangle();
      }

      void setItemWidth(int var1);
   }
}
