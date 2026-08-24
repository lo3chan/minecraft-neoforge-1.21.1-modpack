package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.recipe.ModifiedCraftingRecipe;
import com.iafenvoy.origins.util.RecipeUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({RecipeManager.class})
public abstract class RecipeManagerMixin {
   @ModifyReturnValue(
      method = {"getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;"},
      at = {@At("RETURN")}
   )
   private Optional<RecipeHolder<?>> modifyCraftingRecipe(Optional<RecipeHolder<?>> original, RecipeType<?> type, RecipeInput input, Level world) {
      return original.map(
         entry -> {
            ResourceLocation id = entry.id();
            return entry.value() instanceof CraftingRecipe craftingRecipe && ModifiedCraftingRecipe.canModify(id, craftingRecipe, input)
               ? new RecipeHolder(id, new ModifiedCraftingRecipe(id, craftingRecipe))
               : entry;
         }
      );
   }

   @ModifyExpressionValue(
      method = {"lambda$apply$0"},
      at = {@At(
         value = "NEW",
         target = "(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/item/crafting/Recipe;)Lnet/minecraft/world/item/crafting/RecipeHolder;"
      )}
   )
   private static RecipeHolder<?> validateRecipe(RecipeHolder<?> original, @Local Recipe<?> recipe) {
      return (RecipeHolder<?>)RecipeUtil.validateRecipe(recipe).map(r -> original).getOrThrow();
   }
}
