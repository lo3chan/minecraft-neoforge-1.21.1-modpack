package net.mehvahdjukaar.moonlight.core.client.config;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

class ConfigRowList extends ContainerObjectSelectionList<ConfigListRow> {
   private boolean drawFooterSeparator = true;
   private int rowWidth = 280;

   ConfigRowList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
      super(minecraft, width, height, y, itemHeight);
   }

   void setRows(List<ConfigListRow> rows) {
      this.clearEntries();

      for (ConfigListRow row : rows) {
         this.addEntry(row);
      }

      this.clampScrollAmount();
   }

   @Nullable
   ConfigListRow getHovered(double mouseX, double mouseY) {
      return (ConfigListRow)this.getEntryAtPosition(mouseX, mouseY);
   }

   public int getRowWidth() {
      return this.rowWidth;
   }

   void setRowWidth(int rowWidth) {
      this.rowWidth = rowWidth;
   }

   protected int getScrollbarPosition() {
      return this.getX() + this.width / 2 + this.getRowWidth() / 2 + 6;
   }

   void setTopPadding(int padding) {
      this.setRenderHeader(padding > 0, Math.max(0, padding));
   }

   void setDrawFooterSeparator(boolean draw) {
      this.drawFooterSeparator = draw;
   }

   protected void renderListSeparators(GuiGraphics graphics) {
      if (this.drawFooterSeparator) {
         ResourceLocation footer = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
         RenderSystem.enableBlend();
         graphics.blit(footer, this.getX(), this.getBottom(), 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
         RenderSystem.disableBlend();
      }
   }
}
