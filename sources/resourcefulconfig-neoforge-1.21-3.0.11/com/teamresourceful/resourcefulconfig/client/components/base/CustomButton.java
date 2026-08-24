package com.teamresourceful.resourcefulconfig.client.components.base;

import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CustomButton extends AbstractButton {
   private final Component text;
   private final Runnable onPress;

   public CustomButton(int width, int height, Component text, Runnable onPress) {
      super(0, 0, width + 4, height + 4, CommonComponents.EMPTY);
      this.text = text;
      this.onPress = onPress;
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      ResourceLocation button = this.isHovered() ? ModSprites.BUTTON_HOVER : ModSprites.BUTTON;
      graphics.blitSprite(button, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      renderScrollingString(
         graphics,
         Minecraft.getInstance().font,
         this.text,
         this.getX() + 2,
         this.getY() + 2,
         this.getX() + this.getWidth() - 2,
         this.getY() + this.getHeight() - 2,
         -329226
      );
   }

   public void onPress() {
      this.onPress.run();
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }
}
