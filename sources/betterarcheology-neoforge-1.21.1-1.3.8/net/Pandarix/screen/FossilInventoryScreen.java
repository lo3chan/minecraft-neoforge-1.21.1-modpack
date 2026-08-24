package net.Pandarix.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.Pandarix.BACommon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FossilInventoryScreen extends AbstractContainerScreen<FossilInventoryMenu> {
   private static final ResourceLocation TEXTURE = BACommon.createResource("textures/gui/fossil_gui.png");

   public FossilInventoryScreen(FossilInventoryMenu handler, Inventory inventory, Component title) {
      super(handler, inventory, Component.translatable(title.getString()).withStyle(ChatFormatting.GRAY));
   }

   protected void init() {
      super.init();
      this.titleLabelX = this.imageWidth / 2 - 35;
   }

   protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2;
      guiGraphics.blit(TEXTURE, x, y - 8, 0, 0, 176, 176);
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      this.renderBackground(guiGraphics, mouseX, mouseY, delta);
      super.render(guiGraphics, mouseX, mouseY, delta);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
   }
}
