package com.aetherteam.aether.recipe.recipes.block;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBiomeParameterRecipe extends AbstractBlockStateRecipe {
   private final Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> biome;

   public AbstractBiomeParameterRecipe(
      RecipeType<?> type,
      Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> biome,
      BlockStateIngredient ingredient,
      BlockPropertyPair result,
      Optional<ResourceLocation> function
   ) {
      super(type, ingredient, result, function);
      this.biome = biome;
   }

   public boolean matches(Level level, BlockPos pos, BlockState state) {
      if (this.biome.isPresent() && this.biome.get().left().isPresent()) {
         return super.matches(level, pos, state) && level.getBiome(pos).is((ResourceKey)this.biome.get().left().get());
      } else {
         return this.biome.isPresent() && this.biome.get().right().isPresent()
            ? super.matches(level, pos, state) && level.getBiome(pos).is((TagKey)this.biome.get().right().get())
            : super.matches(level, pos, state);
      }
   }

   public Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> getBiome() {
      return this.biome;
   }
}
