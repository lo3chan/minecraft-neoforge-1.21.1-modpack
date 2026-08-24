package com.alonie.brbe.mixins.rei;

import com.alonie.brbe.compat.ItemViewCompat;
import com.alonie.brbe.mixins.accessors.RecipeBookComponentAccessor;
import com.alonie.brbe.mixins.accessors.RecipeBookPageAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe.GhostIngredient;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RecipeBookComponent.class})
public abstract class RecipeBookComponentMixin {
   @Unique
   private ItemStack brbe$hoveredGhostStack;

   @Inject(
      method = {"keyPressed"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void brbe$guardReiViewingScreen(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
      Screen screen = Minecraft.getInstance().screen;
      if (screen != null) {
         String screenClass = screen.getClass().getName();
         if (screenClass.contains("AbstractDisplayViewingScreen") || screenClass.contains("DisplayViewingScreen")) {
            cir.setReturnValue(false);
         }
      }
   }

   @Inject(
      method = {"renderGhostRecipeTooltip"},
      at = {@At("HEAD")}
   )
   private void brbe$captureGhostHover(GuiGraphics gui, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
      GhostRecipe ghostRecipe = ((RecipeBookComponentAccessor)this).getGhostRecipe();
      if (ghostRecipe != null && ghostRecipe.size() != 0) {
         for (int idx = 0; idx < ghostRecipe.size(); idx++) {
            GhostIngredient ing = ghostRecipe.get(idx);
            int sx = ing.getX() + x;
            int sy = ing.getY() + y;
            if (mouseX >= sx && mouseX < sx + 16 && mouseY >= sy && mouseY < sy + 16) {
               this.brbe$hoveredGhostStack = ing.getItem();
               return;
            }
         }

         this.brbe$hoveredGhostStack = null;
      } else {
         this.brbe$hoveredGhostStack = null;
      }
   }

   @Inject(
      method = {"keyPressed"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void brbe$handleItemViewKeys(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
      if (ItemViewCompat.isLoaded() && !cir.getReturnValueZ()) {
         RecipeBookPage page = ((RecipeBookComponentAccessor)this).getRecipeBookPage();
         if (page != null) {
            for (RecipeButton button : ((RecipeBookPageAccessor)page).getButtons()) {
               if (button.isHoveredOrFocused() && button.getCollection() != null && button.getRecipe() != null) {
                  ItemStack hoveredStack = button.getRecipe().value().getResultItem(button.getCollection().registryAccess());
                  if (hoveredStack != null && !hoveredStack.isEmpty()) {
                     if (ItemViewCompat.matchesShowRecipe(keyCode, scanCode)) {
                        cir.setReturnValue(ItemViewCompat.openRecipeView(hoveredStack));
                     } else if (ItemViewCompat.matchesShowUses(keyCode, scanCode)) {
                        cir.setReturnValue(ItemViewCompat.openUsageView(hoveredStack));
                     }

                     return;
                  }

                  return;
               }
            }

            ItemStack ghostStack = this.brbe$hoveredGhostStack;
            if (ghostStack != null && !ghostStack.isEmpty()) {
               if (ItemViewCompat.matchesShowRecipe(keyCode, scanCode)) {
                  cir.setReturnValue(ItemViewCompat.openRecipeView(ghostStack));
               } else if (ItemViewCompat.matchesShowUses(keyCode, scanCode)) {
                  cir.setReturnValue(ItemViewCompat.openUsageView(ghostStack));
               }
            }
         }
      }
   }
}
