package com.aetherteam.aether.recipe.recipes.ban;

import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public abstract class AbstractPlacementBanRecipe<T, S extends Predicate<T>, R extends RecipeInput> implements Recipe<R> {
   protected final RecipeType<?> type;
   private final Either<ResourceKey<Biome>, TagKey<Biome>> biome;
   protected final Optional<BlockStateIngredient> bypassBlock;
   protected final S ingredient;

   public AbstractPlacementBanRecipe(
      RecipeType<?> type, Either<ResourceKey<Biome>, TagKey<Biome>> biome, Optional<BlockStateIngredient> bypassBlock, S ingredient
   ) {
      this.type = type;
      this.biome = biome;
      this.bypassBlock = bypassBlock;
      this.ingredient = ingredient;
   }

   public boolean matches(Level level, BlockPos pos, T object) {
      if (!this.bypassBlock.isEmpty() && !this.bypassBlock.get().isEmpty() && this.bypassBlock.get().test(level.getBlockState(pos))) {
         return false;
      } else if (this.biome.left().isPresent()) {
         return this.getIngredient().test(object) && level.getBiome(pos).is((ResourceKey)this.biome.left().get());
      } else {
         return !this.biome.right().isPresent()
            ? this.getIngredient().test(object)
            : this.getIngredient().test(object) && level.getBiome(pos).is((TagKey)this.biome.right().get());
      }
   }

   public Either<ResourceKey<Biome>, TagKey<Biome>> getBiome() {
      return this.biome;
   }

   public Optional<BlockStateIngredient> getBypassBlock() {
      return this.bypassBlock;
   }

   public S getIngredient() {
      return this.ingredient;
   }

   public RecipeType<?> getType() {
      return this.type;
   }

   public boolean matches(R container, Level level) {
      return false;
   }

   public boolean canCraftInDimensions(int pWidth, int pHeight) {
      return false;
   }

   public ItemStack assemble(R container, Provider provider) {
      return ItemStack.EMPTY;
   }

   public ItemStack getResultItem(Provider provider) {
      return ItemStack.EMPTY;
   }
}
