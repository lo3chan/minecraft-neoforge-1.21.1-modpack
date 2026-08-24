package io.github.razordevs.deep_aether.screen;

import com.aetherteam.aether.client.gui.screen.inventory.AbstractRecipeBookScreen;
import io.github.razordevs.deep_aether.recipe.combiner.CombinerRecipe;
import io.github.razordevs.deep_aether.recipe.combiner.CombinerRecipeInput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CombinerScreen
   extends AbstractRecipeBookScreen<CombinerRecipeInput, CombinerRecipe, CombinerMenu, CombinerRecipeBookComponent>
   implements RecipeUpdateListener {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/gui/combiner_gui.png");

   public CombinerScreen(CombinerMenu menu, Inventory pPlayerInventory, Component pTitle) {
      super(menu, new CombinerRecipeBookComponent(), pPlayerInventory, pTitle);
   }

   protected void init() {
      super.init();
      this.initScreen(20);
   }

   public void containerTick() {
      super.containerTick();
      ((CombinerRecipeBookComponent)this.recipeBookComponent).tick();
   }

   protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
      int left = this.getGuiLeft();
      int top = this.getGuiTop();
      guiGraphics.blit(TEXTURE, left, top, 0, 0, this.getXSize(), this.getYSize());
      this.renderProgressArrow(guiGraphics, left, top);
   }

   private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
      if (((CombinerMenu)this.menu).isCrafting()) {
         guiGraphics.blit(TEXTURE, x + 63, y + 36, 176, 0, 51, ((CombinerMenu)this.menu).getScaledProgress());
      }
   }
}
