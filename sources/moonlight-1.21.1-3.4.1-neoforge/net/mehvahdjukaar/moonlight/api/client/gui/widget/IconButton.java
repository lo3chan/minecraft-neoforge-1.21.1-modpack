package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IconButton extends Button {
   private static final int PAD = 4;
   private final ResourceLocation sprite;
   private final int spriteWidth;
   private final int spriteHeight;
   private boolean drawBackground = true;

   public IconButton(int x, int y, int width, int height, Component message, ResourceLocation sprite, int spriteWidth, int spriteHeight, OnPress onPress) {
      super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
      this.sprite = sprite;
      this.spriteWidth = spriteWidth;
      this.spriteHeight = spriteHeight;
   }

   public IconButton borderless() {
      this.drawBackground = false;
      return this;
   }

   private boolean hasText() {
      return !this.getMessage().getString().isEmpty();
   }

   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      int iconY = this.getY() + (this.getHeight() - this.spriteHeight) / 2;
      int iconX;
      if (this.drawBackground) {
         super.renderWidget(graphics, mouseX, mouseY, partialTick);
         if (this.hasText()) {
            Font font = Minecraft.getInstance().font;
            int textLeft = this.getX() + (this.getWidth() - font.width(this.getMessage())) / 2;
            iconX = Math.max(this.getX() + 4, textLeft - 4 - this.spriteWidth);
         } else {
            iconX = this.getX() + (this.getWidth() - this.spriteWidth) / 2;
         }
      } else {
         if (this.isHoveredOrFocused()) {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 822083583);
         }

         iconX = this.getX() + (this.getWidth() - this.spriteWidth) / 2;
      }

      if (!this.active) {
         graphics.setColor(0.5F, 0.5F, 0.5F, 1.0F);
      }

      graphics.blitSprite(this.sprite, iconX, iconY, this.spriteWidth, this.spriteHeight);
      if (!this.active) {
         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
