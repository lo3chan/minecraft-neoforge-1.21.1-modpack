package com.alonie.brbe.mixins.unlockrecipes;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.interfaces.unlockrecipes.IMixinRecipeManager;
import com.alonie.brbe.mixins.accessors.RecipeBookComponentAccessor;
import com.alonie.brbe.util.ClientInventoryUtil;
import com.alonie.brbe.util.RecipeMenuUtil;
import com.alonie.brbe.util.RecipePlacement;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MultiPlayerGameMode.class})
public abstract class MultiPlayerGameModeMixin {
   @Shadow
   @Final
   private Minecraft minecraft;
   @Shadow
   private int carriedIndex;

   @Shadow
   protected abstract void startPrediction(ClientLevel var1, PredictiveAction var2);

   @Inject(
      method = {"handlePlaceRecipe"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onPlaceRecipe(int z, RecipeHolder<?> recipe, boolean shiftKeyDown, CallbackInfo ci) {
      if (BetterRecipeBook.ctx().config().newRecipes.unlockAll
         && this.minecraft.player != null
         && this.minecraft.gameMode != null
         && this.minecraft.getConnection() != null
         && this.minecraft.screen instanceof RecipeUpdateListener rul
         && this.minecraft.player.containerMenu instanceof RecipeBookMenu<?, ?> menu) {
         RecipeBookComponent comp = rul.getRecipeBookComponent();
         RecipeBookPage page = ((RecipeBookComponentAccessor)comp).getRecipeBookPage();
         RecipeCollection lastRecipe = page.getLastClickedRecipeCollection();
         StackedContents contents = new StackedContents();

         for (Slot slot : menu.slots) {
            if (slot.index != menu.getResultSlotIndex()) {
               contents.accountStack(slot.getItem());
            }
         }

         lastRecipe.canCraft(contents, menu.getGridWidth(), menu.getGridHeight(), this.minecraft.player.getRecipeBook());
         Set<ResourceLocation> serverUnlockedRecipes = ((IMixinRecipeManager)this.minecraft.getConnection().getRecipeManager()).brbe$getServerUnlockedRecipes();
         if (!lastRecipe.isCraftable(recipe)) {
            for (int i = 0; i < menu.getSize() && i != menu.getResultSlotIndex(); i++) {
               ClientInventoryUtil.storeItem(i, idx -> idx != menu.getResultSlotIndex() || idx >= menu.getSize());
            }

            comp.setupGhostRecipe(recipe, menu.slots);
            if (!serverUnlockedRecipes.contains(recipe.id())) {
               ci.cancel();
            }
         } else if (!serverUnlockedRecipes.contains(recipe.id())) {
            MultiPlayerGameMode gameMode = this.minecraft.gameMode;
            ci.cancel();
            if (!menu.getCarried().isEmpty()) {
               ClientInventoryUtil.storeItem(-1, idx -> idx != menu.getResultSlotIndex() || idx >= menu.getSize());
            }

            List<List<Ingredient>> placement = RecipePlacement.create(recipe, menu.getGridWidth(), menu.getGridHeight());

            for (Slot craftingSlot : menu.slots) {
               if (RecipeMenuUtil.isRecipeSlot(menu, craftingSlot.index)) {
                  List<Ingredient> slotIngredients = placement.get(craftingSlot.index - (menu.getResultSlotIndex() > 0 ? 0 : 1));
                  if (!craftingSlot.getItem().isEmpty()
                     && (slotIngredients.isEmpty() || slotIngredients.stream().anyMatch(i -> !i.test(craftingSlot.getItem())))) {
                     ClientInventoryUtil.storeItem(craftingSlot.index, idx -> idx != menu.getResultSlotIndex() || idx >= menu.getSize());
                  }

                  for (Slot inventorySlot : menu.slots) {
                     if (!RecipeMenuUtil.isRecipeSlot(menu, inventorySlot.index) && slotIngredients.stream().anyMatch(i -> i.test(inventorySlot.getItem()))) {
                        gameMode.handleInventoryMouseClick(menu.containerId, inventorySlot.index, 0, ClickType.PICKUP, this.minecraft.player);
                        gameMode.handleInventoryMouseClick(menu.containerId, craftingSlot.index, 1, ClickType.PICKUP, this.minecraft.player);
                        if (!menu.getCarried().isEmpty()) {
                           gameMode.handleInventoryMouseClick(menu.containerId, inventorySlot.index, 0, ClickType.PICKUP, this.minecraft.player);
                        }
                        break;
                     }
                  }
               }
            }

            if (menu instanceof AbstractFurnaceMenu) {
               if (menu.getSlot(menu.getResultSlotIndex()).hasItem()) {
                  ClientInventoryUtil.storeItem(menu.getResultSlotIndex(), idx -> idx != menu.getResultSlotIndex() || idx >= menu.getSize());
               }
            } else if (BetterRecipeBook.instantCraftingManager.isEnabled() && !menu.getSlot(menu.getResultSlotIndex()).getItem().isEmpty()) {
               BetterRecipeBook.instantCraftingManager.recipeClicked(recipe, this.minecraft.level.registryAccess());
            }
         }
      }
   }
}
