package com.seibel.distanthorizons.common.wrappers.gui.updater;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.network.chat.Component;

public class ChangelogScreen$TextArea_neoforge extends ContainerObjectSelectionList<ChangelogScreen$ButtonEntry_neoforge> {
   Font textRenderer;

   public ChangelogScreen$TextArea_neoforge(Minecraft minecraftClient, int canvasWidth, int canvasHeight, int topMargin, int botMargin, int itemSpacing) {
      super(minecraftClient, canvasWidth, canvasHeight - (topMargin + botMargin), topMargin, itemSpacing);
      this.centerListVertically = false;
      this.textRenderer = minecraftClient.font;
   }

   public void addButton(Component text) {
      this.addEntry(ChangelogScreen$ButtonEntry_neoforge.create(text));
   }

   public int getRowWidth() {
      return 10000;
   }
}
