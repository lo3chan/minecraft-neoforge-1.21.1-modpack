package com.aetherteam.aether.recipe.recipes.block;

import com.aetherteam.aether.event.AetherEventDispatch;
import com.aetherteam.aether.event.PlacementConvertEvent;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.serializer.BiomeParameterRecipeSerializer;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

public class PlacementConversionRecipe extends AbstractBiomeParameterRecipe {
   public PlacementConversionRecipe(
      Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> biome, BlockStateIngredient ingredient, BlockPropertyPair result, Optional<ResourceLocation> function
   ) {
      super((RecipeType<?>)AetherRecipeTypes.PLACEMENT_CONVERSION.get(), biome, ingredient, result, function);
   }

   public PlacementConversionRecipe(BlockStateIngredient ingredient, BlockPropertyPair result, Optional<ResourceLocation> function) {
      this(Optional.empty(), ingredient, result, function);
   }

   public boolean convert(Level level, BlockPos pos, BlockState oldState) {
      if (this.matches(level, pos, oldState)) {
         BlockState newState = this.getResultState(oldState);
         PlacementConvertEvent event = AetherEventDispatch.onPlacementConvert(level, pos, oldState, newState);
         if (!event.isCanceled()) {
            level.setBlockAndUpdate(pos, newState);
            return true;
         }
      }

      return false;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AetherRecipeSerializers.PLACEMENT_CONVERSION.get();
   }

   public static class Serializer extends BiomeParameterRecipeSerializer<PlacementConversionRecipe> {
      public Serializer() {
         super(PlacementConversionRecipe::new, PlacementConversionRecipe::new);
      }
   }
}
