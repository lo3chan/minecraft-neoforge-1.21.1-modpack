package dev.isxander.yacl3.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public class LowProfileButtonWidget extends Button {
   public LowProfileButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress) {
      super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
   }

   public LowProfileButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress, Tooltip tooltip) {
      this(x, y, width, height, message, onPress);
      this.setTooltip(tooltip);
   }

   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
      if (this.isHoveredOrFocused() && this.active) {
         super.renderWidget(graphics, mouseX, mouseY, deltaTicks);
      } else {
         int j = this.active ? -1 : -6250336;
         this.renderString(graphics, Minecraft.getInstance().font, j);
      }
   }
}
