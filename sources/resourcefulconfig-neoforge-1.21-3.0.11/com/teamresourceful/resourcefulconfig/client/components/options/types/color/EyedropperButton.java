package com.teamresourceful.resourcefulconfig.client.components.options.types.color;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.SpriteButton;
import com.teamresourceful.resourcefulconfig.client.screens.base.OverlayScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class EyedropperButton extends SpriteButton {
   private static final ResourceLocation SCREEN_TEXTURE = ResourceLocation.fromNamespaceAndPath("resourcefulconfig", "dynamic/screen");

   public EyedropperButton(HsbState state) {
      super(12, 12, 2, ModSprites.EYE_DROPPER, () -> {
         Minecraft minecraft = Minecraft.getInstance();
         DynamicTexture texture = new DynamicTexture(Screenshot.takeScreenshot(minecraft.getMainRenderTarget()));
         minecraft.getTextureManager().register(SCREEN_TEXTURE, texture);
         minecraft.setScreen(new EyedropperButton.Overlay(minecraft.screen, texture.getPixels(), state));
      }, null);
   }

   private static class Overlay extends OverlayScreen {
      private final NativeImage image;
      private final HsbState state;

      protected Overlay(Screen background, NativeImage image, HsbState state) {
         super(background);
         this.image = image;
         this.state = state;
      }

      @Override
      public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
         double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
         RenderSystem.setShaderTexture(0, EyedropperButton.SCREEN_TEXTURE);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         Matrix4f matrix4f = graphics.pose().last().pose();
         BufferBuilder bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         bufferBuilder.addVertex(matrix4f, 0.0F, this.height, 0.0F).setUv(0.0F, 1.0F);
         bufferBuilder.addVertex(matrix4f, this.width, this.height, 0.0F).setUv(1.0F, 1.0F);
         bufferBuilder.addVertex(matrix4f, this.width, 0.0F, 0.0F).setUv(1.0F, 0.0F);
         bufferBuilder.addVertex(matrix4f, 0.0F, 0.0F, 0.0F).setUv(0.0F, 0.0F);
         BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
         int x = mouseX - 5;
         int y = mouseY - 5;
         matrix4f = graphics.pose().last().pose();
         bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         float u0 = (float)x / this.width;
         float v0 = (float)y / this.height;
         float u1 = (float)(x + 10) / this.width;
         float v1 = (float)(y + 10) / this.height;
         bufferBuilder.addVertex(matrix4f, x - 5, y + 15, 0.0F).setUv(u0, v1);
         bufferBuilder.addVertex(matrix4f, x + 15, y + 15, 0.0F).setUv(u1, v1);
         bufferBuilder.addVertex(matrix4f, x + 15, y - 5, 0.0F).setUv(u1, v0);
         bufferBuilder.addVertex(matrix4f, x - 5, y - 5, 0.0F).setUv(u0, v0);
         BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
         graphics.renderOutline(x - 5, y - 5, 20, 20, -1);
         int pixelX = (int)(mouseX * guiScale);
         int pixelY = (int)(mouseY * guiScale);
         if (pixelX >= 0 && pixelY >= 0 && pixelX < this.image.getWidth() && pixelY < this.image.getHeight()) {
            int abgr = this.image.getPixelRGBA(pixelX, pixelY);
            int pixel = abgr & -16711936 | (abgr & 0xFF) << 16 | (abgr & 0xFF0000) >> 16;
            this.setTooltipForNextRenderPass(Component.literal(String.format("#%08X", pixel)).withColor(pixel));
         }
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         if (button != 0) {
            return false;
         } else {
            double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
            int pixelX = (int)(mouseX * guiScale);
            int pixelY = (int)(mouseY * guiScale);
            if (pixelX >= 0 && pixelY >= 0 && pixelX < this.image.getWidth() && pixelY < this.image.getHeight()) {
               int abgr = this.image.getPixelRGBA(pixelX, pixelY);
               int pixel = abgr & -16711936 | (abgr & 0xFF) << 16 | (abgr & 0xFF0000) >> 16;
               this.state.set(HsbColor.fromRgb(pixel));
               this.onClose();
               return true;
            } else {
               return false;
            }
         }
      }
   }
}
