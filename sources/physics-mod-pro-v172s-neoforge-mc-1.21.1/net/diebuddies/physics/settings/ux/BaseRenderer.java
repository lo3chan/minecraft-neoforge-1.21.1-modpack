package net.diebuddies.physics.settings.ux;

import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor.ARGB32;

public class BaseRenderer {
   public static void renderSettingsTooltip(LegacyOptionsList list, GuiGraphics guiGraphics, int mouseX, int mouseY, int width, int height) {
      Component tooltip = LegacyOptionsList.tooltipAt(list, mouseX, mouseY);
      if (tooltip != null) {
         float border = 28.0F;
         AbstractWidget widget = LegacyOptionsList.widgetAt(list, mouseX, mouseY);
         if (mouseY > height * 0.6F) {
            MainToolTipRenderer.renderToolTip(
               MainToolTipRenderer.TooltipAlignment.TOP,
               (Animatable)widget,
               tooltip,
               guiGraphics,
               border,
               width - border * 2.0F,
               border,
               1.0F,
               ARGB32.color(255, 0, 0, 0)
            );
         } else {
            MainToolTipRenderer.renderToolTip(
               (Animatable)widget, tooltip, guiGraphics, border, width - border * 2.0F, height - border, 1.0F, ARGB32.color(255, 0, 0, 0)
            );
         }
      }
   }
}
