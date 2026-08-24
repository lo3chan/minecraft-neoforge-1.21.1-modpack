package net.nycto_team.overpacked.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.nycto_team.overpacked.menu.GiantBackpackMenu;

@OnlyIn(Dist.CLIENT)
public class GiantBackpackScreen extends AbstractContainerScreen<GiantBackpackMenu> {
   private static final ResourceLocation texture = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
   private final int rows;

   public GiantBackpackScreen(GiantBackpackMenu menu, Inventory inv, Component title) {
      super(menu, inv, title);
      this.rows = menu.rows;
      this.imageHeight = 114 + this.rows * 18;
      this.inventoryLabelY = this.imageHeight - 94;
   }

   public void render(GuiGraphics gr, int mouse_x, int mouse_y, float partial_tick) {
      this.renderBackground(gr, mouse_x, mouse_y, partial_tick);
      super.render(gr, mouse_x, mouse_y, partial_tick);
      this.renderTooltip(gr, mouse_x, mouse_y);
   }

   protected void renderBg(GuiGraphics gr, float pPartialTick, int pMouseX, int pMouseY) {
      int x = this.leftPos;
      int y = this.topPos;
      blit(gr, x, y, 0, 0, this.imageWidth, this.rows * 18 + 17);
      blit(gr, x, y + this.rows * 18 + 17, 0, 126, this.imageWidth, 96);
   }

   private static void blit(GuiGraphics gr, int x, int y, int u, int v, int w, int h) {
      gr.blit(texture, x, y, u, v, w, h);
   }
}
