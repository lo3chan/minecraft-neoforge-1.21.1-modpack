package com.github.alexthe666.alexsmobs.citadel.client.gui;

import com.github.alexthe666.alexsmobs.client.render.AMRenderCompat;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class LinkButton extends Button {
   public ItemStack previewStack;
   public GuiBasicBook book;

   public LinkButton(GuiBasicBook book, int x, int y, int width, int height, Component component, ItemStack previewStack, OnPress onPress) {
      super(x, y, width + (previewStack.isEmpty() ? 0 : 6), height, component, onPress, Button.DEFAULT_NARRATION);
      this.previewStack = previewStack;
      this.book = book;
   }

   public LinkButton(GuiBasicBook book, int x, int y, int width, int height, Component component, OnPress onPress) {
      this(book, x, y, width, height, component, ItemStack.EMPTY, onPress);
   }

   public int getFGColor() {
      return this.isHovered ? this.book.getWidgetColor() : (this.active ? 9729114 : 10526880);
   }

   private int getTextureY() {
      int i = 1;
      if (!this.active) {
         i = 0;
      } else if (this.isHoveredOrFocused()) {
         i = 2;
      }

      return 46 + i * 20;
   }

   public void renderWidget(GuiGraphics guiGraphics, int guiX, int guiY, float partialTicks) {
      Minecraft minecraft = Minecraft.getInstance();
      Font font = minecraft.font;
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, this.book.getBookButtonsTexture());
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
      int i = this.getTextureY();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableDepthTest();
      AMRenderCompat.blit(guiGraphics, this.book.getBookButtonsTexture(), this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height);
      AMRenderCompat.blit(
         guiGraphics,
         this.book.getBookButtonsTexture(),
         this.getX() + this.width / 2,
         this.getY(),
         200 - this.width / 2,
         46 + i * 20,
         this.width / 2,
         this.height
      );
      if (this.isHovered) {
         int color = this.book.getWidgetColor();
         int r = (color & 0xFF0000) >> 16;
         int g = (color & 0xFF00) >> 8;
         int b = color & 0xFF;
         int var12 = 3;
         BookBlit.blitWithColor(
            guiGraphics,
            this.book.getBookButtonsTexture(),
            this.getX(),
            this.getY(),
            0.0F,
            46 + var12 * 20,
            this.width / 2,
            this.height,
            256,
            256,
            r,
            g,
            b,
            255
         );
         BookBlit.blitWithColor(
            guiGraphics,
            this.book.getBookButtonsTexture(),
            this.getX() + this.width / 2,
            this.getY(),
            200 - this.width / 2,
            46 + var12 * 20,
            this.width / 2,
            this.height,
            256,
            256,
            r,
            g,
            b,
            255
         );
      }

      int j = this.getFGColor();
      int itemTextOffset = this.previewStack.isEmpty() ? 0 : 8;
      if (!this.previewStack.isEmpty()) {
         ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
         guiGraphics.renderItem(this.previewStack, this.getX() + 2, this.getY() + 1);
      }

      drawTextOf(
         guiGraphics,
         font,
         this.getMessage(),
         this.getX() + itemTextOffset + this.width / 2,
         this.getY() + (this.height - 8) / 2,
         j | Mth.ceil(this.alpha * 255.0F) << 24
      );
   }

   public static void drawTextOf(GuiGraphics guiGraphics, Font font, Component component, int x, int y, int color) {
      FormattedCharSequence formattedcharsequence = component.getVisualOrderText();
      guiGraphics.drawString(font, formattedcharsequence, x - font.width(formattedcharsequence) / 2, y, color, false);
   }

   public void playDownSound(SoundManager soundManager) {
      soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
   }
}
