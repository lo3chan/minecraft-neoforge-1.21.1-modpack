package dev.isxander.yacl3.gui.tab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import dev.isxander.yacl3.gui.render.ColorGradientRenderState;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import dev.isxander.yacl3.mixin.TabNavigationBarAccessor;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class ScrollableNavigationBar extends TabNavigationBar {
   private static final int NAVBAR_MARGIN = 28;
   private static final Font font;
   private int scrollOffset;
   private int maxScrollOffset;
   private final TabNavigationBarAccessor accessor = (TabNavigationBarAccessor)this;

   public ScrollableNavigationBar(int width, TabManager tabManager, Iterable<? extends Tab> tabs) {
      super(width, tabManager, ImmutableList.copyOf(tabs));
      UnmodifiableIterator var4 = this.accessor.yacl$getTabButtons().iterator();

      while (var4.hasNext()) {
         TabButton tabButton = (TabButton)var4.next();
         if (tabButton.tab() instanceof TabExt tab) {
            tabButton.setTooltip(tab.getTooltip());
         }
      }
   }

   public void arrangeElements() {
      ImmutableList<TabButton> tabButtons = this.accessor.yacl$getTabButtons();
      int noScrollWidth = this.accessor.yacl$getWidth() - 56;
      int allTabsWidth = 0;
      UnmodifiableIterator layout = tabButtons.iterator();

      while (layout.hasNext()) {
         TabButton tabButton = (TabButton)layout.next();
         int buttonWidth = font.width(tabButton.getMessage()) + 20;
         allTabsWidth += buttonWidth;
         tabButton.setWidth(buttonWidth);
      }

      if (allTabsWidth < noScrollWidth) {
         int equalWidth = noScrollWidth / tabButtons.size();
         List<TabButton> smallTabs = tabButtons.stream().filter(btn -> btn.getWidth() < equalWidth).toList();
         List<TabButton> bigTabs = tabButtons.stream().filter(btn -> btn.getWidth() >= equalWidth).toList();
         int leftoverWidth = noScrollWidth - bigTabs.stream().mapToInt(AbstractWidget::getWidth).sum();
         int equalWidthForSmallTabs = leftoverWidth / smallTabs.size();

         for (TabButton tabButton : smallTabs) {
            tabButton.setWidth(equalWidthForSmallTabs);
         }

         allTabsWidth = noScrollWidth;
      }

      Layout layoutx = ((TabNavigationBarAccessor)this).yacl$getLayout();
      layoutx.arrangeElements();
      layoutx.setY(0);
      this.scrollOffset = 0;
      layoutx.setX(Math.max((this.accessor.yacl$getWidth() - allTabsWidth) / 2, 28));
      this.maxScrollOffset = Math.max(0, allTabsWidth - noScrollWidth);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      GuiUtils.pushPose(graphics);
      GuiUtils.translateZ(graphics, 10.0F);
      super.render(graphics, mouseX, mouseY, delta);
      LinearLayout layout = this.accessor.yacl$getLayout();
      if (this.scrollOffset < this.maxScrollOffset - 28) {
         int right = this.accessor.yacl$getWidth();
         ColorGradientRenderState.createHorizontal(graphics, right - 40, layout.getY(), right, layout.getY() + layout.getHeight(), 0, -16777216)
            .submit(graphics);
         graphics.drawString(font, "→", right - 10, layout.getY() + (layout.getHeight() - 9) / 2, -1, false);
      }

      if (this.scrollOffset > 28) {
         ColorGradientRenderState.createHorizontal(graphics, 0, layout.getY(), 40, layout.getY() + layout.getHeight(), -16777216, 0).submit(graphics);
         graphics.drawString(font, "←", 5, layout.getY() + (layout.getHeight() - 9) / 2, -1, false);
      }

      GuiUtils.popPose(graphics);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
      this.setScrollOffset(this.scrollOffset - (int)(vertical * 15.0) - (int)(horizontal * 15.0));
      return true;
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return mouseY <= 24.0;
   }

   public void setScrollOffset(int scrollOffset) {
      Layout layout = ((TabNavigationBarAccessor)this).yacl$getLayout();
      layout.setX(layout.getX() + this.scrollOffset);
      this.scrollOffset = Mth.clamp(scrollOffset, 0, this.maxScrollOffset);
      layout.setX(layout.getX() - this.scrollOffset);
   }

   public int getScrollOffset() {
      return this.scrollOffset;
   }

   public void setFocused(@Nullable GuiEventListener child) {
      super.setFocused(child);
      if (child instanceof TabButton tabButton) {
         this.ensureVisible(tabButton);
      }
   }

   protected void ensureVisible(TabButton tabButton) {
      if (tabButton.getX() < 28) {
         this.setScrollOffset(this.scrollOffset - (28 - tabButton.getX()));
      } else if (tabButton.getX() + tabButton.getWidth() > this.accessor.yacl$getWidth() - 28) {
         this.setScrollOffset(this.scrollOffset + (tabButton.getX() + tabButton.getWidth() - (this.accessor.yacl$getWidth() - 28)));
      }
   }

   public ImmutableList<Tab> getTabs() {
      return this.accessor.yacl$getTabs();
   }

   public TabManager getTabManager() {
      return this.accessor.yacl$getTabManager();
   }

   static {
      font = Minecraft.getInstance().font;
   }
}
