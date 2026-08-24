package com.alonie.brbe.mixins;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.util.BrbeLogger;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RecipeBookComponent.class})
public abstract class DisableCraftableFilter {
   @Shadow
   protected StateSwitchingButton filterButton;

   @Inject(
      method = {"initVisuals"},
      at = {@At("TAIL")}
   )
   private void brbe$hideFilterButton(CallbackInfo ci) {
      boolean shouldHide = BetterRecipeBook.ctx().config().partialCraftingEnabled;
      BrbeLogger.log(BrbeLogger.Category.FILTER, "DisableCraftableFilter — pCE=%s hiding=%s", shouldHide, shouldHide);
      if (shouldHide) {
         this.filterButton.visible = false;
         this.filterButton.active = false;
      }
   }
}
