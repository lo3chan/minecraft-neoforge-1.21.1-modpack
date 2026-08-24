package net.Pandarix.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.Pandarix.BACommon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IdentifyingScreen extends AbstractContainerScreen<IdentifyingMenu> {
   private static final ResourceLocation TEXTURE = BACommon.createResource("textures/gui/archeology_table_gui.png");

   public IdentifyingScreen(IdentifyingMenu inventoryMenu, Inventory inventory, Component title) {
      super(inventoryMenu, inventory, title);
   }

   protected void init() {
      super.init();
      this.titleLabelX = this.imageWidth / 2 - 43;
      this.titleLabelY += 2;
   }

   protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2;
      guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
      this.renderProgressArrow(guiGraphics, x, y);
   }

   private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
      if (((IdentifyingMenu)this.menu).isCrafting()) {
         guiGraphics.blit(TEXTURE, x + 51, y + 48, 176, 0, ((IdentifyingMenu)this.menu).getScaledProgress(), 17);
      }
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      this.renderBackground(guiGraphics, mouseX, mouseY, delta);
      super.render(guiGraphics, mouseX, mouseY, delta);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
   }
}
