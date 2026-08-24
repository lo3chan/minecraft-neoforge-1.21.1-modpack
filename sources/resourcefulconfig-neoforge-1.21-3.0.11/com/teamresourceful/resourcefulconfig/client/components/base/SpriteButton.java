package com.teamresourceful.resourcefulconfig.client.components.base;

import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SpriteButton extends AbstractButton {
   protected final int padding;
   protected final ResourceLocation sprite;
   protected final Runnable onPress;

   protected SpriteButton(int width, int height, int padding, ResourceLocation sprite, Runnable onPress, @Nullable Component tooltip) {
      super(0, 0, width + padding * 2, height + padding * 2, tooltip == null ? CommonComponents.EMPTY : tooltip);
      this.padding = padding;
      this.sprite = sprite;
      this.onPress = onPress;
      this.setTooltip(Tooltip.create(this.getMessage()));
   }

   public static SpriteButton.Builder builder(int width, int height) {
      return new SpriteButton.Builder(width, height);
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      ResourceLocation button = this.isHovered() ? ModSprites.BUTTON_HOVER : ModSprites.BUTTON;
      graphics.blitSprite(button, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      graphics.blitSprite(
         this.sprite, this.getX() + this.padding, this.getY() + this.padding, this.getWidth() - this.padding * 2, this.getHeight() - this.padding * 2
      );
   }

   public void onPress() {
      this.onPress.run();
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }

   public static class Builder {
      private final int width;
      private final int height;
      private int padding;
      private ResourceLocation sprite;
      private Runnable onPress = () -> {};
      private Component tooltip = null;

      public Builder(int width, int height) {
         this.width = width;
         this.height = height;
      }

      public SpriteButton.Builder padding(int padding) {
         this.padding = padding;
         return this;
      }

      public SpriteButton.Builder sprite(ResourceLocation sprite) {
         this.sprite = sprite;
         return this;
      }

      public SpriteButton.Builder onPress(Runnable onPress) {
         this.onPress = onPress;
         return this;
      }

      public SpriteButton.Builder tooltip(Component tooltip) {
         this.tooltip = tooltip;
         return this;
      }

      public SpriteButton build() {
         return new SpriteButton(this.width, this.height, this.padding, this.sprite, this.onPress, this.tooltip);
      }
   }
}
