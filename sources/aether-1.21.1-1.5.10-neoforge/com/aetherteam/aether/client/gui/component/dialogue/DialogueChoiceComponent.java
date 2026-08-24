package com.aetherteam.aether.client.gui.component.dialogue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class DialogueChoiceComponent extends Button {
   public DialogueChoiceComponent(MutableComponent message, OnPress onPress) {
      super(Button.builder(appendBrackets(message), onPress).pos(0, 0).size(0, 12).createNarration(DEFAULT_NARRATION));
      this.width = Minecraft.getInstance().font.width(this.getMessage()) + 2;
   }

   public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      guiGraphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 1711276032, 1711276032);
      guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + 1, this.getY() + 1, this.isHovered() ? 16777045 : 16777215);
   }

   public static MutableComponent appendBrackets(MutableComponent component) {
      return Component.literal("[").append(component).append("]");
   }
}
