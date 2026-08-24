package com.alonie.brbe.mixins.pins;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.PinnedRecipeManager;
import com.alonie.brbe.mixins.accessors.OverlayRecipeButtonAccessor;
import com.alonie.brbe.mixins.accessors.OverlayRecipeComponentAccessor;
import com.alonie.brbe.mixins.accessors.RecipeBookComponentAccessor;
import com.alonie.brbe.mixins.accessors.RecipeBookPageAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractContainerScreen.class})
public abstract class AbstractContainerScreenMixin {
   @Inject(
      method = {"keyPressed"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
      if (this instanceof RecipeUpdateListener rul) {
         Minecraft minecraft = Minecraft.getInstance();
         RecipeBookComponent book = rul.getRecipeBookComponent();
         if (book.isVisible()) {
            RecipeBookPage page = ((RecipeBookComponentAccessor)book).getRecipeBookPage();
            OverlayRecipeComponent alternatesWidget = ((RecipeBookPageAccessor)page).getOverlay();
            EditBox searchBox = ((RecipeBookComponentAccessor)book).getSearchBox();
            if (BetterRecipeBook.PIN_MAPPING.matches(keyCode, scanCode) && !searchBox.canConsumeInput()) {
               if (alternatesWidget.isVisible()) {
                  for (AbstractWidget alternativeButton : ((OverlayRecipeComponentAccessor)alternatesWidget).getRecipeButtons()) {
                     if (alternativeButton.isHoveredOrFocused()) {
                        PinnedRecipeManager.handlePinRecipe(book, page, ((OverlayRecipeButtonAccessor)alternativeButton).getRecipe());
                        cir.setReturnValue(true);
                        return;
                     }
                  }

                  return;
               }

               for (RecipeButton button : ((RecipeBookPageAccessor)page).getButtons()) {
                  if (button.isHoveredOrFocused()) {
                     PinnedRecipeManager.handlePinRecipe(book, page, button.getRecipe());
                     cir.setReturnValue(true);
                     return;
                  }
               }
            }

            if (minecraft.options.keyChat.matches(keyCode, scanCode)) {
               minecraft.screen.setFocused(book);
            }
         }
      }
   }
}
