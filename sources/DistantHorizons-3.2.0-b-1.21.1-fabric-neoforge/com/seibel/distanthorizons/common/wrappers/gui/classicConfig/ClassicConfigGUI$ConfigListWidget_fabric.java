package com.seibel.distanthorizons.common.wrappers.gui.classicConfig;

import com.seibel.distanthorizons.core.config.types.AbstractConfigBase;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_339;
import net.minecraft.class_4265;

public class ClassicConfigGUI$ConfigListWidget_fabric extends class_4265<ClassicConfigGUI$DhButtonEntry_fabric> {
   class_327 textRenderer;

   public ClassicConfigGUI$ConfigListWidget_fabric(class_310 minecraftClient, int canvasWidth, int canvasHeight, int topMargin, int botMargin, int itemSpacing) {
      super(minecraftClient, canvasWidth, canvasHeight - (topMargin + botMargin), topMargin, itemSpacing);
      this.field_22744 = false;
      this.textRenderer = minecraftClient.field_1772;
   }

   public void addButton(
      DhConfigScreen_fabric gui, AbstractConfigBase dhConfigType, class_339 button, class_339 resetButton, class_339 indexButton, class_2561 text
   ) {
      this.method_25321(new ClassicConfigGUI$DhButtonEntry_fabric(gui, dhConfigType, button, text, resetButton, indexButton));
   }

   public int method_25322() {
      return 10000;
   }

   public class_339 getHoveredButton(double mouseX, double mouseY) {
      for (ClassicConfigGUI$DhButtonEntry_fabric buttonEntry : this.method_25396()) {
         class_339 button = buttonEntry.button;
         if (button != null && button.field_22764) {
            double minX = button.method_46426();
            double minY = button.method_46427();
            double maxX = minX + button.method_25368();
            double maxY = minY + button.method_25364();
            if (mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY) {
               return button;
            }
         }
      }

      return null;
   }
}
