package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.joefoxe.hexerei.data.books.BookPaintElement;
import net.joefoxe.hexerei.data.books.PaintSystem;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.SetPaintingToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class CanvasPaintingCropScreen extends Screen {
   private final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/owl_courier_delivery_gui.png");
   int ticks = 0;
   int img_width = 124;
   int img_height = 164;
   int left;
   int top;
   boolean clicked = false;
   boolean hasUV = false;
   PaintSystem paintSystem;
   BookPaintElement paintElement;
   int clickX = 0;
   int clickY = 0;
   int clickW = 16;
   int clickH = 16;
   float u0 = 0.0F;
   float v0 = 0.0F;
   float u1 = 1.0F;
   float v1 = 1.0F;
   boolean toolsVisibleOld;
   boolean draggingCenter = false;
   int draggingOffsetX = 0;
   int draggingOffsetY = 0;

   public CanvasPaintingCropScreen(BookPaintElement paintElement, PaintSystem paintSystem) {
      super(Component.translatable("screen.hexerei.canvas_painting_crop"));
      this.paintElement = paintElement;
      this.paintSystem = paintSystem;
      this.minecraft = Minecraft.getInstance();
      this.clickX = 0;
      this.clickY = 0;
      this.clickW = 16;
      this.clickH = 16;
      this.hasUV = true;
      this.setUV();
      this.toolsVisibleOld = paintSystem.toolsVisible;
      paintSystem.setToolsVisible(false);
   }

   private void setUV() {
      this.u0 = Math.clamp((float)this.clickX / this.paintSystem.width, 0.0F, 1.0F);
      this.v0 = Math.clamp((float)this.clickY / this.paintSystem.height, 0.0F, 1.0F);
      this.u1 = Math.clamp((float)(this.clickX + this.clickW) / this.paintSystem.width, 0.0F, 1.0F);
      this.v1 = Math.clamp((float)(this.clickY + this.clickH) / this.paintSystem.height, 0.0F, 1.0F);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int pButton) {
      if (pButton == 0) {
         int clickX = (int)((mouseX - (this.width / 2 - this.paintSystem.width * 2 * this.paintElement.scale)) / (4.0F * this.paintElement.scale));
         int clickY = (int)((mouseY - (this.height / 2 - this.paintSystem.height * 2 * this.paintElement.scale)) / (4.0F * this.paintElement.scale));
         if (clickX >= this.clickX && clickX < this.clickX + this.clickW && clickY >= this.clickY && clickY < this.clickY + this.clickH) {
            this.draggingCenter = true;
            this.draggingOffsetX = clickX - this.clickX;
            this.draggingOffsetY = clickY - this.clickY;
         } else {
            this.clickX = Math.clamp(clickX, 0, this.paintSystem.width - 1);
            this.clickY = Math.clamp(clickY, 0, this.paintSystem.height - 1);
            this.clickW = 0;
            this.clickH = 0;
            this.setUV();
            this.clicked = true;
         }
      } else if (pButton == 1) {
         this.clickW = 0;
         this.clickH = 0;
         this.u0 = 0.0F;
         this.v0 = 0.0F;
         this.u1 = 0.0F;
         this.v1 = 0.0F;
      }

      return super.mouseClicked(mouseX, mouseY, pButton);
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      if (this.clicked) {
         int w = Math.clamp(
            (int)((mouseX - (this.width / 2 - this.paintSystem.width * 2 * this.paintElement.scale) + 4.0) / (4.0F * this.paintElement.scale)) - this.clickX,
            0,
            this.paintSystem.width - this.clickX
         );
         int h = Math.clamp(
            (int)((mouseY - (this.height / 2 - this.paintSystem.height * 2 * this.paintElement.scale) + 4.0) / (4.0F * this.paintElement.scale)) - this.clickY,
            0,
            this.paintSystem.height - this.clickY
         );
         this.clickW = Math.min(w, h);
         this.clickH = Math.min(w, h);
         this.setUV();
      }

      if (this.draggingCenter) {
         this.clickX = Math.clamp(
            (int)((mouseX - (this.width / 2 - this.paintSystem.width * 2 * this.paintElement.scale)) / (4.0F * this.paintElement.scale)) - this.draggingOffsetX,
            0,
            this.paintSystem.width - this.clickW
         );
         this.clickY = Math.clamp(
            (int)((mouseY - (this.height / 2 - this.paintSystem.height * 2 * this.paintElement.scale)) / (4.0F * this.paintElement.scale))
               - this.draggingOffsetY,
            0,
            this.paintSystem.height - this.clickH
         );
         this.setUV();
      }

      return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int pButton) {
      if (this.clicked) {
         this.clicked = false;
         this.setUV();
         this.hasUV = this.clickW != 0;
      }

      if (this.draggingCenter) {
         this.draggingCenter = false;
      }

      return super.mouseClicked(mouseX, mouseY, pButton);
   }

   public void onClose() {
      if (this.hasUV) {
         String loc = this.paintSystem.getImageLocation().toString();
         HexereiPacketHandler.sendToServer(new SetPaintingToServer(loc, this.u0, this.u1, this.v0, this.v1));
      }

      this.paintSystem.setToolsVisible(this.toolsVisibleOld);
      super.onClose();
   }

   protected void init() {
      this.left = this.width / 2 - this.img_width / 2;
      this.top = this.height / 2 - this.img_height / 2;
   }

   public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float pPartialTick) {
      pGuiGraphics.pose().pushPose();
      pGuiGraphics.pose().translate(0.0F, 0.0F, 5.0F);
      Lighting.setupForFlatItems();
      pGuiGraphics.drawCenteredString(this.font, Component.literal(this.clickW + " x " + this.clickH), this.width / 2 - 148, this.top + 40, 10066329);
      float scale = 2.0F * this.paintElement.scale;
      innerBlit(
         pGuiGraphics,
         ResourceLocation.parse("hexerei:textures/book/canvas.png"),
         this.width / 2 - this.paintSystem.width * scale + this.u0 * this.paintSystem.width * scale * 2.0F,
         this.width / 2 - this.paintSystem.width * scale + this.u1 * this.paintSystem.width * scale * 2.0F,
         this.height / 2 - this.paintSystem.height * scale + this.v0 * this.paintSystem.height * scale * 2.0F,
         this.height / 2 - this.paintSystem.height * scale + this.v1 * this.paintSystem.height * scale * 2.0F,
         1.0F,
         0.0F,
         1.0F,
         0.0F,
         1.0F,
         0.45F
      );
      innerBlit(
         pGuiGraphics,
         ResourceLocation.parse("hexerei:textures/book/canvas.png"),
         this.width / 2 - this.paintSystem.width * scale + this.u0 * this.paintSystem.width * scale * 2.0F,
         this.width / 2 - this.paintSystem.width * scale + this.u1 * this.paintSystem.width * scale * 2.0F,
         this.height / 2 - this.paintSystem.height * scale + this.v0 * this.paintSystem.height * scale * 2.0F,
         this.height / 2 - this.paintSystem.height * scale + this.v1 * this.paintSystem.height * scale * 2.0F,
         -1.0F,
         0.0F,
         1.0F,
         0.0F,
         1.0F,
         1.0F
      );
      innerBlit(
         pGuiGraphics,
         this.paintSystem.getImageLocation(),
         this.width / 2 - this.paintSystem.width * scale,
         this.width / 2 + this.paintSystem.width * scale,
         this.height / 2 - this.paintSystem.height * scale,
         this.height / 2 + this.paintSystem.height * scale,
         0.0F,
         0.0F,
         1.0F,
         0.0F,
         1.0F,
         1.0F
      );
      innerBlit(
         pGuiGraphics,
         ResourceLocation.parse("hexerei:textures/book/canvas.png"),
         this.width / 2 - 180,
         this.width / 2 - 180 + 64,
         this.height / 2 - 32,
         this.height / 2 + 32,
         -1.0F,
         0.0F,
         1.0F,
         0.0F,
         1.0F,
         1.0F
      );
      if (this.clickW != 0) {
         innerBlit(
            pGuiGraphics,
            this.paintSystem.getImageLocation(),
            this.width / 2 - 180,
            this.width / 2 - 180 + 64,
            this.height / 2 - 32,
            this.height / 2 + 32,
            0.0F,
            this.u0,
            this.u1,
            this.v0,
            this.v1,
            1.0F
         );
      }

      innerBlit(
         pGuiGraphics,
         ResourceLocation.parse("hexerei:textures/book/blank.png"),
         this.width / 2 - this.paintSystem.width * scale,
         this.width / 2 + this.paintSystem.width * scale,
         this.height / 2 - this.paintSystem.height * scale,
         this.height / 2 + this.paintSystem.height * scale,
         -4.0F,
         0.0F,
         1.0F,
         0.0F,
         1.0F,
         0.5F
      );
      innerBlit(
         pGuiGraphics,
         ResourceLocation.parse("hexerei:textures/book/blank.png"),
         this.width / 2 - this.paintSystem.width * scale - 4.0F,
         this.width / 2 - this.paintSystem.width * scale,
         this.height / 2 - this.paintSystem.height * scale - 4.0F,
         this.height / 2 + this.paintSystem.height * scale + 4.0F,
         -5.0F,
         0.0F,
         1.0F,
         0.0F,
         1.0F,
         0.0F,
         0.0F,
         0.0F,
         0.975F
      );
      innerBlit(
         pGuiGraphics,
         ResourceLocation.parse("hexerei:textures/book/blank.png"),
         this.width / 2 + this.paintSystem.width * scale,
         this.width / 2 + this.paintSystem.width * scale + 4.0F,
         this.height / 2 - this.paintSystem.height * scale - 4.0F,
         this.height / 2 + this.paintSystem.height * scale + 4.0F,
         -5.0F,
         0.0F,
         1.0F,
         0.0F,
         1.0F,
         0.0F,
         0.0F,
         0.0F,
         0.975F
      );
      innerBlit(
         pGuiGraphics,
         ResourceLocation.parse("hexerei:textures/book/blank.png"),
         this.width / 2 - this.paintSystem.width * scale - 4.0F,
         this.width / 2 + this.paintSystem.width * scale + 4.0F,
         this.height / 2 - this.paintSystem.height * scale - 4.0F,
         this.height / 2 - this.paintSystem.height * scale,
         -5.0F,
         0.0F,
         1.0F,
         0.0F,
         1.0F,
         0.0F,
         0.0F,
         0.0F,
         0.975F
      );
      innerBlit(
         pGuiGraphics,
         ResourceLocation.parse("hexerei:textures/book/blank.png"),
         this.width / 2 - this.paintSystem.width * scale - 4.0F,
         this.width / 2 + this.paintSystem.width * scale + 4.0F,
         this.height / 2 + this.paintSystem.height * scale,
         this.height / 2 + this.paintSystem.height * scale + 4.0F,
         -5.0F,
         0.0F,
         1.0F,
         0.0F,
         1.0F,
         0.0F,
         0.0F,
         0.0F,
         0.975F
      );
      List<Component> tooltipLines = new ArrayList<>();
      if (!tooltipLines.isEmpty()) {
         pGuiGraphics.renderTooltip(this.font, tooltipLines, Optional.empty(), mouseX, mouseY);
      }

      Lighting.setupFor3DItems();
      pGuiGraphics.pose().popPose();
      super.render(pGuiGraphics, mouseX, mouseY, pPartialTick);
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.renderTransparentBackground(guiGraphics);
   }

   public Component getTitle() {
      return super.getTitle();
   }

   private void onDone() {
      this.minecraft.setScreen(null);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void tick() {
      this.ticks++;
      if (!this.isValid()) {
         this.onDone();
      }
   }

   private boolean isValid() {
      return this.minecraft != null && this.minecraft.player != null;
   }

   private static void bufferQuad(
      ResourceLocation atlasLocation,
      GuiGraphics guiGraphics,
      float x,
      float y,
      float z,
      float width,
      float height,
      float uOffset,
      float vOffset,
      int uWidth,
      int vHeight,
      int spriteWidth,
      int spriteHeight,
      float alpha
   ) {
      blit(guiGraphics, atlasLocation, x, x + width, y, y + height, z, uWidth, vHeight, uOffset, vOffset, spriteWidth, spriteHeight, alpha);
   }

   private static void blit(
      GuiGraphics guiGraphics,
      ResourceLocation atlasLocation,
      float x1,
      float x2,
      float y1,
      float y2,
      float blitOffset,
      int uWidth,
      int vHeight,
      float uOffset,
      float vOffset,
      int textureWidth,
      int textureHeight,
      float alpha
   ) {
      innerBlit(
         guiGraphics,
         atlasLocation,
         x1,
         x2,
         y1,
         y2,
         blitOffset,
         (uOffset + 0.0F) / textureWidth,
         (uOffset + uWidth) / textureWidth,
         (vOffset + 0.0F) / textureHeight,
         (vOffset + vHeight) / textureHeight,
         alpha
      );
   }

   private static void innerBlit(
      GuiGraphics guiGraphics,
      ResourceLocation atlasLocation,
      float x1,
      float x2,
      float y1,
      float y2,
      float blitOffset,
      float minU,
      float maxU,
      float minV,
      float maxV,
      float alpha
   ) {
      Matrix4f matrix4f = guiGraphics.pose().last().pose();
      if (alpha == 1.0F) {
         RenderSystem.setShaderTexture(0, atlasLocation);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         bufferbuilder.addVertex(matrix4f, x1, y1, blitOffset)
            .setUv(minU, minV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         bufferbuilder.addVertex(matrix4f, x1, y2, blitOffset)
            .setUv(minU, maxV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         bufferbuilder.addVertex(matrix4f, x2, y2, blitOffset)
            .setUv(maxU, maxV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         bufferbuilder.addVertex(matrix4f, x2, y1, blitOffset)
            .setUv(maxU, minV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
      } else {
         VertexConsumer vertexBuilder = guiGraphics.bufferSource().getBuffer(RenderType.entityTranslucentEmissive(atlasLocation));
         vertexBuilder.addVertex(matrix4f, x1, y1, blitOffset)
            .setUv(minU, minV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, -1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         vertexBuilder.addVertex(matrix4f, x1, y2, blitOffset)
            .setUv(minU, maxV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, -1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         vertexBuilder.addVertex(matrix4f, x2, y2, blitOffset)
            .setUv(maxU, maxV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, -1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
         vertexBuilder.addVertex(matrix4f, x2, y1, blitOffset)
            .setUv(maxU, minV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, -1.0F, 0.0F)
            .setColor(1.0F, 1.0F, 1.0F, alpha);
      }
   }

   private static void innerBlit(
      GuiGraphics guiGraphics,
      ResourceLocation atlasLocation,
      float x1,
      float x2,
      float y1,
      float y2,
      float blitOffset,
      float minU,
      float maxU,
      float minV,
      float maxV,
      float red,
      float green,
      float blue,
      float alpha
   ) {
      Matrix4f matrix4f = guiGraphics.pose().last().pose();
      if (alpha == 1.0F) {
         RenderSystem.setShaderTexture(0, atlasLocation);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         bufferbuilder.addVertex(matrix4f, x1, y1, blitOffset)
            .setUv(minU, minV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(red, green, blue, alpha);
         bufferbuilder.addVertex(matrix4f, x1, y2, blitOffset)
            .setUv(minU, maxV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(red, green, blue, alpha);
         bufferbuilder.addVertex(matrix4f, x2, y2, blitOffset)
            .setUv(maxU, maxV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(red, green, blue, alpha);
         bufferbuilder.addVertex(matrix4f, x2, y1, blitOffset)
            .setUv(maxU, minV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, 1.0F, 0.0F)
            .setColor(red, green, blue, alpha);
         BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
      } else {
         VertexConsumer vertexBuilder = guiGraphics.bufferSource().getBuffer(RenderType.entityTranslucentEmissive(atlasLocation));
         vertexBuilder.addVertex(matrix4f, x1, y1, blitOffset)
            .setUv(minU, minV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, -1.0F, 0.0F)
            .setColor(red, green, blue, alpha);
         vertexBuilder.addVertex(matrix4f, x1, y2, blitOffset)
            .setUv(minU, maxV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, -1.0F, 0.0F)
            .setColor(red, green, blue, alpha);
         vertexBuilder.addVertex(matrix4f, x2, y2, blitOffset)
            .setUv(maxU, maxV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, -1.0F, 0.0F)
            .setColor(red, green, blue, alpha);
         vertexBuilder.addVertex(matrix4f, x2, y1, blitOffset)
            .setUv(maxU, minV)
            .setLight(15728880)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(0.0F, -1.0F, 0.0F)
            .setColor(red, green, blue, alpha);
      }
   }

   public static void nineSlice(
      ResourceLocation atlasLocation,
      GuiGraphics guiGraphics,
      float posX,
      float posY,
      float posZ,
      float width,
      float height,
      int sliceLeftWidth,
      int sliceRightWidth,
      int sliceTopHeight,
      int sliceBottomHeight,
      int spriteWidth,
      int spriteHeight,
      float uOffset,
      float vOffset,
      int uWidth,
      int vHeight,
      float alpha
   ) {
      int middleTextureWidth = uWidth - sliceLeftWidth - sliceRightWidth;
      int middleTextureHeight = vHeight - sliceBottomHeight - sliceTopHeight;
      float topV1 = vOffset + sliceTopHeight;
      float leftU1 = uOffset + sliceLeftWidth;
      float rightU0 = uOffset + uWidth - sliceRightWidth;
      float bottomV0 = vOffset + vHeight - sliceBottomHeight;
      float middleU0 = leftU1 + 1.0F;
      float middleU1 = rightU0 + 1.0F;
      float middleV0 = vOffset + (sliceTopHeight + 1);
      float middleV1 = vOffset + vHeight - (sliceBottomHeight + 1);
      float leftX = posX + sliceLeftWidth;
      float rightX = posX + width - sliceRightWidth;
      float topY = posY + sliceTopHeight;
      float bottomY = posY + height - sliceBottomHeight;
      float middleWidth = rightX - leftX;
      float middleHeight = bottomY - topY;
      bufferQuad(
         atlasLocation,
         guiGraphics,
         posX,
         posY,
         posZ,
         sliceLeftWidth,
         sliceTopHeight,
         uOffset,
         vOffset,
         sliceLeftWidth,
         sliceTopHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         rightX,
         posY,
         posZ,
         sliceRightWidth,
         sliceTopHeight,
         rightU0,
         vOffset,
         sliceRightWidth,
         sliceTopHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         posX,
         bottomY,
         posZ,
         sliceLeftWidth,
         sliceBottomHeight,
         uOffset,
         bottomV0,
         sliceRightWidth,
         sliceTopHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         rightX,
         bottomY,
         posZ,
         sliceRightWidth,
         sliceBottomHeight,
         rightU0,
         bottomV0,
         sliceRightWidth,
         sliceTopHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         leftX,
         posY,
         posZ,
         middleWidth,
         sliceTopHeight,
         middleU0,
         vOffset,
         middleTextureWidth,
         sliceTopHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         leftX,
         bottomY,
         posZ,
         middleWidth,
         sliceBottomHeight,
         middleU0,
         bottomV0,
         middleTextureWidth,
         sliceTopHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         posX,
         topY,
         posZ,
         sliceLeftWidth,
         middleHeight,
         uOffset,
         middleV0,
         sliceLeftWidth,
         middleTextureHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         rightX,
         topY,
         posZ,
         sliceRightWidth,
         middleHeight,
         rightU0,
         middleV0,
         sliceLeftWidth,
         middleTextureHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      bufferQuad(
         atlasLocation,
         guiGraphics,
         leftX,
         topY,
         posZ,
         middleWidth,
         middleHeight,
         middleU0,
         middleV0 + 1.0F,
         middleTextureWidth,
         middleTextureHeight,
         spriteWidth,
         spriteHeight,
         alpha
      );
      guiGraphics.bufferSource().endBatch();
   }
}
