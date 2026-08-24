package com.seibel.distanthorizons.common.wrappers.gui;

import net.minecraft.class_310;
import net.minecraft.class_4265;

public class MinecraftScreen$ConfigListWidget_fabric extends class_4265 {
   public MinecraftScreen$ConfigListWidget_fabric(class_310 minecraftClient, int canvasWidth, int canvasHeight, int topMargin, int botMargin, int itemSpacing) {
      super(minecraftClient, canvasWidth, canvasHeight - (topMargin + botMargin), topMargin, itemSpacing);
      this.field_22744 = false;
   }
}
