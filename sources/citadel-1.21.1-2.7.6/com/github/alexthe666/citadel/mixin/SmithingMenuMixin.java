package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.server.item.CitadelRecipes;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({SmithingMenu.class})
public class SmithingMenuMixin {
   @ModifyExpressionValue(
      method = {"createResult"},
      remap = true,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/List;"
      )}
   )
   private List<RecipeHolder<SmithingRecipe>> citadel_getRecipesFor(List<RecipeHolder<SmithingRecipe>> original, @Local SmithingRecipeInput input) {
      Builder<RecipeHolder<SmithingRecipe>> builder = ImmutableList.builder();
      builder.addAll(original);
      if (input.size() >= 2 && !input.getItem(0).isEmpty() && !input.getItem(1).isEmpty()) {
         builder.addAll(CitadelRecipes.getSmithingRecipes());
      }

      return builder.build();
   }
}
