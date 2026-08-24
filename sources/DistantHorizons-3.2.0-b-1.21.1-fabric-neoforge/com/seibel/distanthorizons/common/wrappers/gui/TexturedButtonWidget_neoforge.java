package com.seibel.distanthorizons.common.wrappers.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TexturedButtonWidget_neoforge extends Button {
   public final boolean renderBackground;
   private final int u;
   private final int v;
   private final int hoveredVOffset;
   private final ResourceLocation textureResourceLocation;
   private final int textureWidth;
   private final int textureHeight;

   public TexturedButtonWidget_neoforge(
      int x,
      int y,
      int width,
      int height,
      int u,
      int v,
      int hoveredVOffset,
      ResourceLocation textureResourceLocation,
      int textureWidth,
      int textureHeight,
      OnPress pressAction,
      Component text
   ) {
      this(x, y, width, height, u, v, hoveredVOffset, textureResourceLocation, textureWidth, textureHeight, pressAction, text, true);
   }

   public TexturedButtonWidget_neoforge(
      int x,
      int y,
      int width,
      int height,
      int u,
      int v,
      int hoveredVOffset,
      ResourceLocation textureResourceLocation,
      int textureWidth,
      int textureHeight,
      OnPress pressAction,
      Component text,
      boolean renderBackground
   ) {
      super(x, y, width, height, Component.empty(), pressAction, DEFAULT_NARRATION);
      this.u = u;
      this.v = v;
      this.hoveredVOffset = hoveredVOffset;
      this.textureResourceLocation = textureResourceLocation;
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.renderBackground = renderBackground;
   }

   public void renderWidget(GuiGraphics matrices, int mouseX, int mouseY, float delta) {
      if (this.renderBackground) {
         matrices.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
      }

      int i = 0;
      if (!this.active) {
         i = 2;
      } else if (this.isHovered) {
         i = 1;
      }

      matrices.blit(
         this.textureResourceLocation,
         this.getX(),
         this.getY(),
         this.u,
         this.v + this.hoveredVOffset * i,
         this.width,
         this.height,
         this.textureWidth,
         this.textureHeight
      );
   }
}
