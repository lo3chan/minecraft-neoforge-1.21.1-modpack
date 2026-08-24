package com.seibel.distanthorizons.common.wrappers.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public class MinecraftScreen$ConfigListWidget_neoforge extends ContainerObjectSelectionList {
   public MinecraftScreen$ConfigListWidget_neoforge(Minecraft minecraftClient, int canvasWidth, int canvasHeight, int topMargin, int botMargin, int itemSpacing) {
      super(minecraftClient, canvasWidth, canvasHeight - (topMargin + botMargin), topMargin, itemSpacing);
      this.centerListVertically = false;
   }
}
