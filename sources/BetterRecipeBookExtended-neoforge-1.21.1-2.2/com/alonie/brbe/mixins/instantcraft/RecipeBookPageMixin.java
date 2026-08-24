package com.alonie.brbe.mixins.instantcraft;

import com.alonie.brbe.BetterRecipeBook;
import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({RecipeBookPage.class})
public class RecipeBookPageMixin {
   @Shadow
   @Final
   private List<RecipeButton> buttons;
   @Shadow
   private List<RecipeCollection> recipeCollections;

   @Inject(
      method = {"updateCollections"},
      at = {@At("HEAD")},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   public void updateCollections(List<RecipeCollection> list, boolean bl, CallbackInfo ci) {
      if (BetterRecipeBook.instantCraftingManager.isEnabled()) {
         this.buttons.stream().filter(AbstractWidget::isHovered).findAny().ifPresent(btn -> {
            RecipeCollection hoveredCollection = btn.getCollection();
            int idx = this.recipeCollections.indexOf(hoveredCollection);
            if (idx != -1 && idx < list.size() && BetterRecipeBook.instantCraftingManager.lastClickedRecipe != null) {
               list.remove(hoveredCollection);
               list.add(idx, hoveredCollection);
               BetterRecipeBook.instantCraftingManager.lastHoveredCollection = btn.getCollection();
            }
         });
      }
   }
}
