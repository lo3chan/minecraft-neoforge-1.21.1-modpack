package com.aetherteam.aether.recipe.recipes.block;

import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import com.aetherteam.nitrogen.recipe.serializer.BlockStateRecipeSerializer;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class AccessoryFreezableRecipe extends AbstractBlockStateRecipe {
   public AccessoryFreezableRecipe(BlockStateIngredient ingredient, BlockPropertyPair result, Optional<ResourceLocation> function) {
      super((RecipeType)AetherRecipeTypes.ACCESSORY_FREEZABLE.get(), ingredient, result, function);
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AetherRecipeSerializers.ACCESSORY_FREEZABLE.get();
   }

   public static class Serializer extends BlockStateRecipeSerializer<AccessoryFreezableRecipe> {
      public Serializer() {
         super(AccessoryFreezableRecipe::new);
      }
   }
}
