package traben.tconfig.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.AbstractSelectionList.Entry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.jetbrains.annotations.Nullable;
import traben.tconfig.gui.entries.TConfigEntry;

public class TConfigEntryListWidget extends AbstractSelectionList<TConfigEntryListWidget.TConfigEntryForList> {
   protected boolean fullWidthBackgroundEvenIfSmaller = false;

   public TConfigEntryListWidget(int width, int height, int y, int x, int itemHeight, TConfigEntry... entries) {
      super(Minecraft.getInstance(), width, height, y, itemHeight);

      for (TConfigEntry option : entries) {
         if (option != null && option.getWidget(0, 0, 0, 0) != null) {
            this.addEntry(option);
         }
      }

      this.setX(x);
   }

   public int getRowWidth() {
      return Math.min(this.width - 14, super.getRowWidth());
   }

   protected int getScrollbarPosition() {
      return this.getX() == 0 ? super.getScrollbarPosition() : this.getX() + this.getRowWidth() + 4;
   }

   protected void updateWidgetNarration(NarrationElementOutput builder) {
   }

   protected boolean isValidMouseClick(int button) {
      return true;
   }

   public void setSelected(@Nullable TConfigEntryListWidget.TConfigEntryForList entry) {
   }

   public void setWidgetBackgroundToFullWidth() {
      this.fullWidthBackgroundEvenIfSmaller = true;
   }

   protected void renderListBackground(GuiGraphics context) {
      if (this.fullWidthBackgroundEvenIfSmaller) {
         int x = this.getX();
         int width = this.getWidth();
         this.setX(0);

         assert Minecraft.getInstance().screen != null;

         this.setWidth(Minecraft.getInstance().screen.width);
         super.renderListBackground(context);
         this.setX(x);
         this.setWidth(width);
      } else {
         super.renderListBackground(context);
      }
   }

   protected void renderListSeparators(GuiGraphics context) {
      if (this.fullWidthBackgroundEvenIfSmaller) {
         int x = this.getX();
         int width = this.getWidth();
         this.setX(0);

         assert Minecraft.getInstance().screen != null;

         this.setWidth(Minecraft.getInstance().screen.width);
         super.renderListSeparators(context);
         this.setX(x);
         this.setWidth(width);
      } else {
         super.renderListSeparators(context);
      }
   }

   public abstract static class TConfigEntryForList extends Entry<TConfigEntryListWidget.TConfigEntryForList> {
      @Nullable
      protected AbstractWidget lastWidgetRendered = null;

      public void render(
         GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
      ) {
         this.lastWidgetRendered = this.getWidget(x, y, entryWidth, entryHeight);
         if (this.lastWidgetRendered != null) {
            this.lastWidgetRendered.render(context, mouseX, mouseY, tickDelta);
         }
      }

      public abstract AbstractWidget getWidget(int var1, int var2, int var3, int var4);

      private boolean ignoreMouseAt(double mouseX, double mouseY) {
         return this.lastWidgetRendered == null || !this.lastWidgetRendered.isMouseOver(mouseX, mouseY);
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         return this.ignoreMouseAt(mouseX, mouseY) ? false : this.lastWidgetRendered.mouseClicked(mouseX, mouseY, button);
      }

      public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
         return this.ignoreMouseAt(mouseX, mouseY) ? false : this.lastWidgetRendered.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
      }

      public boolean mouseReleased(double mouseX, double mouseY, int button) {
         return this.ignoreMouseAt(mouseX, mouseY) ? false : this.lastWidgetRendered.mouseReleased(mouseX, mouseY, button);
      }

      public void setFocused(boolean focused) {
         if (this.lastWidgetRendered != null) {
            this.lastWidgetRendered.setFocused(focused);
         }
      }
   }
}
