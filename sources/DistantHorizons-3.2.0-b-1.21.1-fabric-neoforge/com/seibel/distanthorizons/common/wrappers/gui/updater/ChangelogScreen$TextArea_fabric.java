package com.seibel.distanthorizons.common.wrappers.gui.updater;

import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_4265;

public class ChangelogScreen$TextArea_fabric extends class_4265<ChangelogScreen$ButtonEntry_fabric> {
   class_327 textRenderer;

   public ChangelogScreen$TextArea_fabric(class_310 minecraftClient, int canvasWidth, int canvasHeight, int topMargin, int botMargin, int itemSpacing) {
      super(minecraftClient, canvasWidth, canvasHeight - (topMargin + botMargin), topMargin, itemSpacing);
      this.field_22744 = false;
      this.textRenderer = minecraftClient.field_1772;
   }

   public void addButton(class_2561 text) {
      this.method_25321(ChangelogScreen$ButtonEntry_fabric.create(text));
   }

   public int method_25322() {
      return 10000;
   }
}
