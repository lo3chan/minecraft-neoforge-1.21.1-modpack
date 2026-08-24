package com.aetherteam.cumulus.client.gui.component;

import com.aetherteam.cumulus.api.Menu;
import com.aetherteam.cumulus.client.gui.screen.MenuSelectionScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList.Entry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class MenuSelectionList extends ObjectSelectionList<MenuSelectionList.MenuEntry> {
   private final MenuSelectionScreen parent;
   private static final int ENTRY_PADDING = 2;

   public MenuSelectionList(MenuSelectionScreen parent, int width, int height, int y, int itemHeight) {
      super(Minecraft.getInstance(), width, height, y, itemHeight);
      this.parent = parent;
      this.refreshList();
   }

   protected void renderSelection(GuiGraphics guiGraphics, int top, int width, int height, int outerColor, int innerColor) {
      int i = this.getX() + (this.width - width) / 2;
      int j = this.getX() + (this.width + width) / 2;
      guiGraphics.fill(i + 1, top - 3, j - 7, top + height + 1, -1);
   }

   protected void renderListBackground(GuiGraphics guiGraphics) {
   }

   protected void renderListSeparators(GuiGraphics guiGraphics) {
   }

   protected int getScrollbarPosition() {
      return this.parent.width / 2 + 141 / 2 - 18;
   }

   public int getRowWidth() {
      return 115;
   }

   public void refreshList() {
      this.clearEntries();
      this.parent.buildMenuList(x$0 -> this.addEntry(x$0), menu -> new MenuSelectionList.MenuEntry(this.parent, menu));
   }

   public class MenuEntry extends Entry<MenuSelectionList.MenuEntry> {
      private final MenuSelectionScreen parent;
      private final Menu menu;

      public MenuEntry(MenuSelectionScreen parent, Menu menu) {
         this.parent = parent;
         this.menu = menu;
      }

      public Component getNarration() {
         return this.menu.name();
      }

      public void render(
         GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks
      ) {
         PoseStack poseStack = guiGraphics.pose();
         poseStack.pushPose();
         guiGraphics.fillGradient(
            left, top - 2, left + MenuSelectionList.this.getRowWidth() - 4 - 6, top + MenuSelectionList.this.itemHeight - 4, -10066330, -8750470
         );
         poseStack.popPose();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         poseStack.pushPose();
         guiGraphics.blit(this.menu.icon(), left + 2 + 1, top + 1, 0.0F, 0.0F, 16, 16, 16, 16);
         poseStack.popPose();
         Font font = this.parent.getFontRenderer();
         int fontWidth = MenuSelectionList.this.getRowWidth() - 4 - 24;
         List<FormattedCharSequence> lines = font.split(this.menu.name(), fontWidth);
         int length = 1;

         for (FormattedCharSequence line : lines) {
            int y = top + length * 10 - lines.size() * 10 / 2;
            guiGraphics.drawString(font, line, left + 2 + 21, y, 16777215);
            length++;
         }
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         this.parent.setSelected(this);
         MenuSelectionList.this.setSelected(this);
         return false;
      }

      public Menu getMenu() {
         return this.menu;
      }
   }
}
