package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.joefoxe.hexerei.container.PackageContainer;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Matrix4f;

public class CourierPackageScreen extends AbstractContainerScreen<PackageContainer> {
   private final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/courier_package_gui.png");
   private final ResourceLocation INVENTORY = HexereiUtil.getResource("textures/gui/inventory.png");
   boolean clicked = false;
   int clickedTicks = 0;
   int clickedTicksOld = 0;

   public CourierPackageScreen(PackageContainer screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn);
      this.titleLabelY = -27;
      this.titleLabelX = 16;
      this.inventoryLabelY = 19;
      this.imageHeight = 67;
   }

   protected void init() {
      super.init();
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
   }

   public Component getTitle() {
      return super.getTitle();
   }

   protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
      super.renderLabels(pGuiGraphics, pMouseX, pMouseY);
      pGuiGraphics.pose().pushPose();
      pGuiGraphics.pose().translate(0.0F, 0.0F, 2.0F);
      pGuiGraphics.drawString(
         this.font, Component.translatable("hexerei.package_letter.seal"), 145, -7, ((PackageContainer)this.menu).isEmpty() ? 3355443 : 4210752, false
      );
      pGuiGraphics.pose().popPose();
   }

   protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.leftPos;
      int j = this.topPos - 28;
      guiGraphics.blit(this.GUI, i, j - 3, 0, 0, 182, 69);
      guiGraphics.blit(this.GUI, i + 78 - 15, j - 30, 230, 0, 26, 26);
      guiGraphics.blit(this.INVENTORY, i + 3, j + 41, 0, 0, 176, 100);
      if (!((PackageContainer)this.menu).isEmpty()) {
         if (this.clicked) {
            guiGraphics.blit(this.GUI, i + 141, j + 17, 1, 102, 30, 16);
         } else if (x > this.leftPos + 141 && x < this.leftPos + 141 + 30 && y > this.topPos + 17 - 28 && y < this.topPos + 17 + 16 - 28) {
            guiGraphics.blit(this.GUI, i + 141, j + 17, 1, 118, 30, 16);
         } else {
            guiGraphics.blit(this.GUI, i + 141, j + 17, 1, 70, 30, 16);
         }

         float xOffset = Mth.clamp(35.0F * (Mth.lerp(partialTicks, this.clickedTicksOld, this.clickedTicks) / 15.0F), 0.0F, 34.0F);
         float xOffset2 = Mth.clamp(35.0F * (Mth.lerp(partialTicks, this.clickedTicksOld - 8, this.clickedTicks - 8) / 10.0F), 0.0F, 36.0F);
         float yOffset = Mth.clamp(18.0F * (Mth.lerp(partialTicks, this.clickedTicksOld - 13, this.clickedTicks - 13) / 5.0F), 0.0F, 17.0F);
         this.floatBlit(guiGraphics, this.GUI, i + 139, i + 139 + xOffset, j + 21, j + 28, 3.0F, 0.125F, (32.0F + xOffset) / 256.0F, 0.2734375F, 0.30078125F);
         this.floatBlit(
            guiGraphics, this.GUI, i + 138, i + 138 + xOffset2, j + 25, j + 29, 4.0F, 0.14453125F, (37.0F + xOffset2) / 256.0F, 0.3046875F, 0.3203125F
         );
         this.floatBlit(
            guiGraphics, this.GUI, i + 160, i + 160 + 4, j + 16 + 17 - yOffset, j + 16 + 17, 5.0F, 0.125F, 0.140625F, (95.0F - yOffset) / 256.0F, 0.37109375F
         );
      } else {
         guiGraphics.blit(this.GUI, i + 141, j + 17, 1, 86, 30, 16);
      }

      RenderSystem.disableDepthTest();
      guiGraphics.renderItem(((PackageContainer)this.menu).getSelf(), i + 83 - 15, j - 25);
      RenderSystem.enableDepthTest();
   }

   void floatBlit(
      GuiGraphics guiGraphics,
      ResourceLocation pAtlasLocation,
      float pX1,
      float pX2,
      float pY1,
      float pY2,
      float pBlitOffset,
      float pMinU,
      float pMaxU,
      float pMinV,
      float pMaxV
   ) {
      RenderSystem.setShaderTexture(0, pAtlasLocation);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      Matrix4f matrix4f = guiGraphics.pose().last().pose();
      BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.addVertex(matrix4f, pX1, pY1, pBlitOffset).setUv(pMinU, pMinV);
      bufferbuilder.addVertex(matrix4f, pX1, pY2, pBlitOffset).setUv(pMinU, pMaxV);
      bufferbuilder.addVertex(matrix4f, pX2, pY2, pBlitOffset).setUv(pMaxU, pMaxV);
      bufferbuilder.addVertex(matrix4f, pX2, pY1, pBlitOffset).setUv(pMaxU, pMinV);
      BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
   }

   public boolean mouseClicked(double x, double y, int button) {
      boolean mouseClicked = super.mouseClicked(x, y, button);
      if (button == 0
         && !((PackageContainer)this.menu).isEmpty()
         && x > this.leftPos + 141
         && x < this.leftPos + 141 + 30
         && y > this.topPos + 17 - 28
         && y < this.topPos + 17 + 16 - 28) {
         this.clicked = true;
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 0.25F));
      }

      return mouseClicked;
   }

   protected void containerTick() {
      super.containerTick();
      this.clickedTicksOld = this.clickedTicks;
      if (this.clicked && !((PackageContainer)this.menu).isEmpty()) {
         if (this.clickedTicks % 8 == 0) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BUNDLE_INSERT, 1.0F, 0.75F));
         }

         this.clickedTicks++;
         if (this.clickedTicks > 25) {
            this.clicked = false;
            if (((PackageContainer)this.menu).clickMenuButton(this.minecraft.player, 1) && this.minecraft.gameMode != null) {
               this.minecraft.gameMode.handleInventoryButtonClick(((PackageContainer)this.menu).containerId, 1);
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BUNDLE_DROP_CONTENTS, 1.3F, 0.75F));
            }

            this.onClose();
         }
      } else {
         this.clickedTicks--;
         if (this.clickedTicks < 0) {
            this.clickedTicks = 0;
         }
      }
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      if (this.clicked && pButton == 0) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 0.85F, 0.25F));
      }

      this.clicked = false;
      return super.mouseReleased(pMouseX, pMouseY, pButton);
   }

   public boolean mouseDragged(double x, double y, int pButton, double pDragX, double pDragY) {
      if (this.clicked && (!(x > this.leftPos + 141) || !(x < this.leftPos + 141 + 30) || !(y > this.topPos + 19 - 28) || !(y < this.topPos + 19 + 16 - 28))) {
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 0.85F, 0.25F));
         this.clicked = false;
      }

      return super.mouseDragged(x, y, pButton, pDragX, pDragY);
   }
}
