package com.alonie.brbe.mixins;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.mixins.accessors.RecipeBookPageAccessor;
import java.util.List;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RecipeBookPage.class})
public class ExpandedRecipeBookPageMixin {
   private static final int EXPANDED_BUTTONS = 48;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void brbe$addExtraButtons(CallbackInfo ci) {
      if (BetterRecipeBook.ctx().config().expandedRecipeBook) {
         List<RecipeButton> buttons = ((RecipeBookPageAccessor)this).getButtons();

         while (buttons.size() < 48) {
            buttons.add(new RecipeButton());
         }
      }
   }

   @ModifyConstant(
      method = {"updateButtonsForPage"},
      constant = {@Constant(
         intValue = 20
      )},
      require = 0
   )
   private int brbe$expandPagination(int original) {
      return BetterRecipeBook.ctx().config().expandedRecipeBook ? 48 : original;
   }
}
