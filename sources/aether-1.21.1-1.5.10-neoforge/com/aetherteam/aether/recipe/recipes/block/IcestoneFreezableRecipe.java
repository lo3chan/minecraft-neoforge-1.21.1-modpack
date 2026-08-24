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

public class IcestoneFreezableRecipe extends AbstractBlockStateRecipe {
   public IcestoneFreezableRecipe(BlockStateIngredient ingredient, BlockPropertyPair result, Optional<ResourceLocation> function) {
      super((RecipeType)AetherRecipeTypes.ICESTONE_FREEZABLE.get(), ingredient, result, function);
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AetherRecipeSerializers.ICESTONE_FREEZABLE.get();
   }

   public static class Serializer extends BlockStateRecipeSerializer<IcestoneFreezableRecipe> {
      public Serializer() {
         super(IcestoneFreezableRecipe::new);
      }
   }
}
