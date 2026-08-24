package dev.isxander.yacl3.gui;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LegacySelectionList<E extends LegacySelectionList.Entry<E>> extends ContainerObjectSelectionList<E> implements LayoutElement {
   public LegacySelectionList(Minecraft client, int y, int width, int height) {
      super(client, width, height, y, 20);
      this.setRenderHeader(false, 0);
   }

   protected void repositionEntries() {
   }

   protected int getScrollbarPosition() {
      return this.scrollBarX();
   }

   protected int scrollBarX() {
      return this.getX() + this.getWidth() - 6;
   }

   protected int getMaxPosition() {
      return this.contentHeight();
   }

   protected int contentHeight() {
      return this.children().stream().mapToInt(LegacySelectionList.Entry::getHeight).sum();
   }

   protected void centerScrollOn(E entry) {
      double d = this.height / -2.0;

      for (int i = 0; i < this.children().indexOf(entry) && i < this.getItemCount(); i++) {
         d += ((LegacySelectionList.Entry)this.children().get(i)).getHeight();
      }

      this.setScrollAmount(d);
   }

   protected int getRowTop(int index) {
      int integer = this.getY() + 4 - (int)this.scrollAmount() + this.headerHeight;

      for (int i = 0; i < this.children().size() && i < index; i++) {
         integer += ((LegacySelectionList.Entry)this.children().get(i)).getHeight();
      }

      return integer;
   }

   protected void ensureVisible(E entry) {
      int entryIndex = this.children().indexOf(entry);
      int top = this.getRowTop(entryIndex);
      int j = top - this.getY() - 4 - entry.getHeight();
      if (j < 0) {
         this.setScrollAmount(this.scrollAmount() + j);
      }

      int k = this.getY() + this.getHeight() - top - entry.getHeight() * 2;
      if (k < 0) {
         this.setScrollAmount(this.scrollAmount() - k);
      }
   }

   @Nullable
   protected E getEntryAtPosition(double x, double y) {
      y += this.scrollAmount();
      if (!(x < this.getX()) && !(x > this.getX() + this.getWidth())) {
         int currentY = this.getY() - this.headerHeight + 4;

         for (E entry : this.children()) {
            if (y >= currentY && y <= currentY + entry.getHeight()) {
               return entry;
            }

            currentY += entry.getHeight();
         }

         return null;
      } else {
         return null;
      }
   }

   protected void renderListItems(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      int left = this.getRowLeft();
      int right = this.getRowWidth();
      int count = this.getItemCount();

      for (int i = 0; i < count; i++) {
         E entry = (E)this.children().get(i);
         int top = this.getRowTop(i);
         int bottom = top + entry.getHeight();
         int entryHeight = entry.getHeight() - 4;
         if (bottom >= this.getY() && top <= this.getY() + this.getHeight()) {
            this.renderItem(graphics, mouseX, mouseY, delta, i, left, top, right, entryHeight);
         }
      }
   }

   public double scrollAmount() {
      return this.getScrollAmount();
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      this.setScrollAmount(this.scrollAmount() - scrollY * 20.0);
      return true;
   }

   public abstract static class Entry<E extends LegacySelectionList.Entry<E>>
      extends net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry<E>
      implements LayoutElement {
      public static final int CONTENT_PADDING = 2;
      private int x;
      private int y;
      private int width;
      private int height;
      private boolean hovered;
      private final LegacySelectionList<E> parent;

      public Entry(LegacySelectionList<E> parent) {
         this.parent = parent;
      }

      public abstract void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5);

      public void render(
         GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick
      ) {
         this.setX(left);
         this.setY(top);
         this.setWidth(width);
         this.hovered = hovering;
         this.renderContent(guiGraphics, mouseX, mouseY, hovering, partialTick);
      }

      public boolean isMouseOver(double mouseX, double mouseY) {
         return this.getRectangle().containsPoint((int)mouseX, (int)mouseY);
      }

      public boolean isFocused() {
         return this.parent.getFocused() == this;
      }

      public boolean isHovered() {
         return this.hovered;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public void setX(int x) {
         this.x = x;
      }

      public void setY(int y) {
         this.y = y;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public void setWidth(int width) {
         this.width = width;
      }

      public void setHeight(int height) {
         this.height = height;
      }

      public int getContentX() {
         return this.getX() + 2;
      }

      public int getContentY() {
         return this.getY() + 2;
      }

      public int getContentHeight() {
         return this.getHeight() - 4;
      }

      public int getContentYMiddle() {
         return this.getContentY() + this.getContentHeight() / 2;
      }

      public int getContentBottom() {
         return this.getContentY() + this.getContentHeight();
      }

      public int getContentWidth() {
         return this.getWidth() - 4;
      }

      public int getContentXMiddle() {
         return this.getContentX() + this.getContentWidth() / 2;
      }

      public int getContentRight() {
         return this.getContentX() + this.getContentWidth();
      }

      public void visitWidgets(Consumer<net.minecraft.client.gui.components.AbstractWidget> consumer) {
      }

      @NotNull
      public ScreenRectangle getRectangle() {
         return super.getRectangle();
      }
   }

   public static class Holder<T extends LegacySelectionList<?>>
      extends net.minecraft.client.gui.components.AbstractWidget
      implements ContainerEventHandler,
      WidgetAndType<T> {
      private final T list;

      public Holder(T list) {
         super(0, 0, 100, 0, CommonComponents.EMPTY);
         this.list = list;
      }

      public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float deltaTick) {
         this.list.render(guiGraphics, mouseX, mouseY, deltaTick);
      }

      protected void updateWidgetNarration(NarrationElementOutput output) {
         this.list.updateNarration(output);
      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of(this.list);
      }

      public T getList() {
         return this.list;
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         return this.list.mouseClicked(mouseX, mouseY, button);
      }

      public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
         return this.list.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
      }

      public boolean mouseReleased(double mouseX, double mouseY, int button) {
         return this.list.mouseReleased(mouseX, mouseY, button);
      }

      public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
         System.out.println("Legacy list scrolled: " + horizontal + ", " + vertical);
         return this.list.mouseScrolled(mouseX, mouseY, horizontal, vertical);
      }

      public boolean keyPressed(int i, int j, int k) {
         return this.list.keyPressed(i, j, k);
      }

      public boolean charTyped(char c, int i) {
         return this.list.charTyped(c, i);
      }

      public boolean isDragging() {
         return this.list.isDragging();
      }

      public void setDragging(boolean dragging) {
         this.list.setDragging(dragging);
      }

      @Nullable
      public GuiEventListener getFocused() {
         return this.list.getFocused();
      }

      public void setFocused(@Nullable GuiEventListener listener) {
         this.list.setFocused(listener);
      }

      @Nullable
      public ComponentPath nextFocusPath(FocusNavigationEvent event) {
         return this.list.nextFocusPath(event);
      }

      @Nullable
      public ComponentPath getCurrentFocusPath() {
         return this.list.getCurrentFocusPath();
      }

      public void setX(int x) {
         this.list.setX(x);
      }

      public void setY(int y) {
         this.list.setY(y);
      }

      public int getX() {
         return this.list.getX();
      }

      public int getY() {
         return this.list.getY();
      }

      public void setWidth(int width) {
         this.list.setWidth(width);
      }

      public void setHeight(int height) {
         this.list.setHeight(height);
      }

      public int getWidth() {
         return this.list.getWidth();
      }

      public int getHeight() {
         return this.list.getHeight();
      }

      public void visitWidgets(Consumer<net.minecraft.client.gui.components.AbstractWidget> consumer) {
         this.list.visitWidgets(consumer);
      }

      public T getType() {
         return this.list;
      }

      @Override
      public net.minecraft.client.gui.components.AbstractWidget getWidget() {
         return this;
      }

      public boolean isMouseOver(double mouseX, double mouseY) {
         return this.isActive()
            && this.visible
            && mouseX >= this.getX()
            && mouseY >= this.getY()
            && mouseX < this.getX() + this.getWidth()
            && mouseY < this.getY() + this.getHeight();
      }
   }
}
