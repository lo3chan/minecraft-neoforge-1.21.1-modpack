package com.alonie.brbe.mixins;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.interfaces.ExpandedBookScreen;
import com.alonie.brbe.mixins.accessors.RecipeBookComponentAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractContainerScreen.class})
public class HideInventoryForExpandedBook {
   @Inject(
      method = {"renderBg"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void brbe$cancelBgWhenExpanded(GuiGraphics gui, float delta, int mouseX, int mouseY, CallbackInfo ci) {
      if (this.brbe$isExpandedBookOpen()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderLabels"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void brbe$cancelLabelsWhenExpanded(GuiGraphics gui, int mouseX, int mouseY, CallbackInfo ci) {
      if (this.brbe$isExpandedBookOpen()) {
         ci.cancel();
      }
   }

   @Unique
   private boolean brbe$isExpandedBookOpen() {
      if (!BetterRecipeBook.ctx().config().expandedRecipeBook) {
         return false;
      } else {
         AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>)this;
         if (screen instanceof ExpandedBookScreen ebs) {
            return ebs.brbe$isExpandedBookOpen();
         } else if (!(screen instanceof RecipeUpdateListener rul)) {
            return false;
         } else {
            RecipeBookComponent rbc = rul.getRecipeBookComponent();
            return rbc != null && ((RecipeBookComponentAccessor)rbc).getVisible();
         }
      }
   }
}
