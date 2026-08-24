package snownee.jade.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.FocusNavigationEvent.ArrowNavigation;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class OptionsNav extends ObjectSelectionList<OptionsNav.Entry> {
   private final OptionsList options;
   private int current;

   public OptionsNav(OptionsList options, int width, int height, int top, int itemHeight) {
      super(Minecraft.getInstance(), width, height, top, itemHeight);
      this.options = options;
   }

   protected void renderListItems(GuiGraphics guiGraphics, int i, int j, float f) {
      super.renderListItems(guiGraphics, i, j, f);
      if (!this.children().isEmpty()) {
         OptionsNav.Entry focused = (OptionsNav.Entry)this.getFocused();
         if (focused != null && this.minecraft.getLastInputType().isKeyboard()) {
            this.current = this.children().indexOf(focused);
         }

         double top = this.getY() + 4 - this.getScrollAmount() + this.current * this.itemHeight + this.headerHeight;
         int left = this.getRowLeft() + 2;
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(0.0, top, 0.0);
         guiGraphics.fill(left, 0, left + 2, this.itemHeight - 4, -1);
         guiGraphics.pose().popPose();
      }
   }

   protected void renderListSeparators(GuiGraphics guiGraphics) {
   }

   protected void renderSelection(GuiGraphics guiGraphics, int i, int j, int k, int l, int m) {
   }

   public void addEntry(OptionsList.Title entry) {
      super.addEntry(new OptionsNav.Entry(this, entry));
   }

   public int getRowWidth() {
      return this.width;
   }

   protected int getScrollbarPosition() {
      return this.getRowLeft() + this.getRowWidth() - 8;
   }

   public void refresh() {
      this.clearEntries();
      if (this.options.children().size() > 1) {
         for (OptionsList.Entry child : this.options.children()) {
            if (child instanceof OptionsList.Title titleEntry) {
               this.addEntry(titleEntry);
            }
         }
      }
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent event) {
      if (!this.isFocused() && event instanceof ArrowNavigation nav && nav.direction() == ScreenDirection.LEFT) {
         for (OptionsNav.Entry entry : this.children()) {
            if (entry.title == this.options.currentTitle) {
               return ComponentPath.path(entry, new ContainerEventHandler[]{this});
            }
         }
      }

      return super.nextFocusPath(event);
   }

   public void setFocused(@Nullable GuiEventListener listener) {
      super.setFocused(listener);
      if (this.minecraft.getLastInputType().isKeyboard() && this.getFocused() instanceof OptionsNav.Entry entry) {
         this.options.showOnTop(entry.title);
      }
   }

   public static class Entry extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<OptionsNav.Entry> {
      private final OptionsList.Title title;
      private final OptionsNav parent;

      public Entry(OptionsNav parent, OptionsList.Title title) {
         this.parent = parent;
         this.title = title;
      }

      public void render(
         GuiGraphics guiGraphics, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime
      ) {
         guiGraphics.drawString(this.title.client.font, this.title.getTitle().getString(), rowLeft + 10, rowTop + height / 2 - 9 / 2, 16777215);
         if (this.isFocused() && this.parent.minecraft.getLastInputType().isKeyboard()) {
            int color = -5592406;
            int left = rowLeft + 2;
            int right = rowLeft + width - 2;
            int bottom = rowTop + height;
            guiGraphics.fill(left, rowTop, right, rowTop + 1, color);
            guiGraphics.fill(left, bottom, right, bottom - 1, color);
            guiGraphics.fill(left, rowTop, left + 1, bottom, color);
            guiGraphics.fill(right, rowTop, right - 1, bottom, color);
         } else if (this.parent.options.currentTitle == this.title) {
            if (!this.parent.isMouseOver(mouseX, mouseY)) {
               this.parent.ensureVisible(this);
            }

            this.parent.current = index;
         }
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         if (button == 0) {
            this.onPress();
         }

         return true;
      }

      public boolean keyPressed(int i, int j, int k) {
         if (CommonInputs.selected(i)) {
            this.onPress();
            return true;
         } else {
            return false;
         }
      }

      public Component getNarration() {
         return this.title.narration;
      }

      public void onPress() {
         this.parent.playDownSound(Minecraft.getInstance().getSoundManager());
         this.parent.options.showOnTop(this.title);
      }

      public OptionsList.Title getTitle() {
         return this.title;
      }
   }
}
