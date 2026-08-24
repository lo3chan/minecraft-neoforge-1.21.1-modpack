package com.seibel.distanthorizons.common.wrappers.gui.classicConfig;

import com.seibel.distanthorizons.core.config.types.AbstractConfigBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.network.chat.Component;

public class ClassicConfigGUI$ConfigListWidget_neoforge extends ContainerObjectSelectionList<ClassicConfigGUI$DhButtonEntry_neoforge> {
   Font textRenderer;

   public ClassicConfigGUI$ConfigListWidget_neoforge(
      Minecraft minecraftClient, int canvasWidth, int canvasHeight, int topMargin, int botMargin, int itemSpacing
   ) {
      super(minecraftClient, canvasWidth, canvasHeight - (topMargin + botMargin), topMargin, itemSpacing);
      this.centerListVertically = false;
      this.textRenderer = minecraftClient.font;
   }

   public void addButton(
      DhConfigScreen_neoforge gui,
      AbstractConfigBase dhConfigType,
      AbstractWidget button,
      AbstractWidget resetButton,
      AbstractWidget indexButton,
      Component text
   ) {
      this.addEntry(new ClassicConfigGUI$DhButtonEntry_neoforge(gui, dhConfigType, button, text, resetButton, indexButton));
   }

   public int getRowWidth() {
      return 10000;
   }

   public AbstractWidget getHoveredButton(double mouseX, double mouseY) {
      for (ClassicConfigGUI$DhButtonEntry_neoforge buttonEntry : this.children()) {
         AbstractWidget button = buttonEntry.button;
         if (button != null && button.visible) {
            double minX = button.getX();
            double minY = button.getY();
            double maxX = minX + button.getWidth();
            double maxY = minY + button.getHeight();
            if (mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY) {
               return button;
            }
         }
      }

      return null;
   }
}
