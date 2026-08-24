package com.alonie.brbe.mixins.incompatibleenvironment;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.util.IncompatibleCraftingUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RecipeButton.class})
public abstract class RecipeButtonMixin {
   @Shadow
   private RecipeCollection collection;

   @Shadow
   public abstract RecipeHolder<?> getRecipe();

   @Shadow
   private List<RecipeHolder<?>> getOrderedRecipes() {
      throw new AssertionError();
   }

   @Inject(
      method = {"getOrderedRecipes"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void betterRecipeBook$ensureNonEmptyRecipes(CallbackInfoReturnable<List<RecipeHolder<?>>> cir) {
      List<RecipeHolder<?>> recipes = (List<RecipeHolder<?>>)cir.getReturnValue();
      if ((recipes == null || recipes.isEmpty())
         && BetterRecipeBook.ctx().config().showAllRecipesInSurvival
         && Minecraft.getInstance().screen instanceof InventoryScreen) {
         List<RecipeHolder<?>> fallback = new ArrayList<>();

         for (RecipeHolder<?> holder : this.collection.getRecipes()) {
            if (IncompatibleCraftingUtil.checkIncompatible(this.collection, holder.id())) {
               fallback.add(holder);
            }
         }

         if (!fallback.isEmpty()) {
            cir.setReturnValue(fallback);
         }
      }
   }

   @Inject(
      method = {"getTooltipText"},
      at = {@At("RETURN")}
   )
   private void betterRecipeBook$appendIncompatibleWarning(CallbackInfoReturnable<List<Component>> cir) {
      if (BetterRecipeBook.ctx().config().showAllRecipesInSurvival) {
         if (Minecraft.getInstance().screen instanceof InventoryScreen) {
            List<Component> list = (List<Component>)cir.getReturnValue();
            if (list != null && !list.isEmpty()) {
               RecipeHolder<?> current;
               try {
                  current = this.getRecipe();
               } catch (ArithmeticException var5) {
                  return;
               }

               if (current != null) {
                  if (IncompatibleCraftingUtil.checkIncompatible(this.collection, current.id())) {
                     list.add(Component.empty());
                     list.add(Component.translatable("brbe.gui.environmentIncompatible").withStyle(ChatFormatting.RED));
                  }
               }
            }
         }
      }
   }
}
