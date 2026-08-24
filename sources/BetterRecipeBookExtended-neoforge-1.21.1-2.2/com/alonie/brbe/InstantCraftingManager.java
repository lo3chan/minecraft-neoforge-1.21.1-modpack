package com.alonie.brbe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class InstantCraftingManager {
   public RecipeHolder<?> lastClickedRecipe = null;
   public RecipeCollection lastHoveredCollection = null;
   public StateSwitchingButton lastInstantCraftButton = null;
   public ItemStack lastCraftResult;
   public long lastContainerId = -1L;

   public InstantCraftingManager() {
      BetterRecipeBook.configHolder.registerLoadListener((h, c) -> {
         if (this.lastInstantCraftButton != null) {
            this.lastInstantCraftButton.setStateTriggered(this.isEnabled());
         }

         return InteractionResult.SUCCESS;
      });
      BetterRecipeBook.configHolder.registerSaveListener((h, c) -> {
         if (this.lastInstantCraftButton != null) {
            this.lastInstantCraftButton.setStateTriggered(this.isEnabled());
         }

         return InteractionResult.SUCCESS;
      });
   }

   public void recipeClicked(RecipeHolder<?> recipe, RegistryAccess registryAccess) {
      if (this.isEnabled()) {
         Minecraft client = Minecraft.getInstance();
         if (!(client.screen instanceof AbstractContainerScreen<?> screen)) {
            return;
         }

         this.lastClickedRecipe = recipe;
         this.lastCraftResult = recipe.value().getResultItem(registryAccess);
         this.lastContainerId = screen.getMenu().containerId;
      } else {
         this.lastCraftResult = null;
      }
   }

   public void onResultSlotUpdated(ItemStack itemStack) {
      Minecraft client = Minecraft.getInstance();
      if (this.isEnabled() && client.gameMode != null && client.screen instanceof AbstractContainerScreen<?> screen) {
         if (this.lastCraftResult != null
            && this.lastClickedRecipe != null
            && ItemStack.isSameItemSameComponents(itemStack, this.lastCraftResult)
            && this.lastContainerId == screen.getMenu().containerId) {
            client.gameMode.handleInventoryMouseClick(screen.getMenu().containerId, 0, 0, ClickType.QUICK_MOVE, client.player);
            this.lastCraftResult = null;
         }
      }
   }

   public boolean toggleEnabled() {
      BetterRecipeBook.ctx().config().instantCraft.enabled = !BetterRecipeBook.ctx().config().instantCraft.enabled;
      return this.isEnabled();
   }

   public boolean isEnabled() {
      return BetterRecipeBook.ctx().config().instantCraft.enabled;
   }
}
