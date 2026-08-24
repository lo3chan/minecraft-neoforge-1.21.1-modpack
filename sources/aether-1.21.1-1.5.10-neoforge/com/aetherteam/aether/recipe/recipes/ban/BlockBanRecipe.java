package com.aetherteam.aether.recipe.recipes.ban;

import com.aetherteam.aether.event.AetherEventDispatch;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.serializer.PlacementBanRecipeSerializer;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.aetherteam.nitrogen.recipe.input.BlockStateRecipeInput;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBanRecipe extends AbstractPlacementBanRecipe<BlockState, BlockStateIngredient, BlockStateRecipeInput> {
   public BlockBanRecipe(Either<ResourceKey<Biome>, TagKey<Biome>> biome, Optional<BlockStateIngredient> bypassBlock, BlockStateIngredient ingredient) {
      super((RecipeType<?>)AetherRecipeTypes.BLOCK_PLACEMENT_BAN.get(), biome, bypassBlock, ingredient);
   }

   public BlockBanRecipe(Either<ResourceKey<Biome>, TagKey<Biome>> biome, Optional<BlockStateIngredient> bypassBlock) {
      this(biome, bypassBlock, BlockStateIngredient.EMPTY);
   }

   public boolean banBlock(Level level, BlockPos pos, BlockState state) {
      if (this.matches(level, pos.below(), state) && AetherEventDispatch.isBlockPlacementBanned(level, pos, state)) {
         AetherEventDispatch.onPlacementSpawnParticles(level, pos, null, null, state);
         return true;
      } else {
         return false;
      }
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AetherRecipeSerializers.BLOCK_PLACEMENT_BAN.get();
   }

   public static class Serializer extends PlacementBanRecipeSerializer<BlockState, BlockStateIngredient, BlockStateRecipeInput, BlockBanRecipe> {
      public Serializer() {
         super(BlockBanRecipe::new);
      }

      public MapCodec<BlockBanRecipe> codec() {
         return RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                  BlockStateRecipeUtil.KEY_CODEC.fieldOf("biome").forGetter(AbstractPlacementBanRecipe::getBiome),
                  BlockStateIngredient.CODEC.optionalFieldOf("bypass").forGetter(AbstractPlacementBanRecipe::getBypassBlock),
                  BlockStateIngredient.CODEC.fieldOf("ingredient").forGetter(AbstractPlacementBanRecipe::getIngredient)
               )
               .apply(inst, this.getFactory())
         );
      }

      public StreamCodec<RegistryFriendlyByteBuf, BlockBanRecipe> streamCodec() {
         return StreamCodec.of(this::toNetwork, this::fromNetwork);
      }

      public BlockBanRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         Either<ResourceKey<Biome>, TagKey<Biome>> biome = (Either<ResourceKey<Biome>, TagKey<Biome>>)BlockStateRecipeUtil.STREAM_CODEC.decode(buffer);
         Optional<BlockStateIngredient> bypassBlock = buffer.readOptional(
            buf -> (BlockStateIngredient)BlockStateIngredient.CONTENTS_STREAM_CODEC.decode(buffer)
         );
         BlockStateIngredient ingredient = (BlockStateIngredient)BlockStateIngredient.CONTENTS_STREAM_CODEC.decode(buffer);
         return new BlockBanRecipe(biome, bypassBlock, ingredient);
      }

      public void toNetwork(RegistryFriendlyByteBuf buffer, BlockBanRecipe recipe) {
         super.toNetwork(buffer, recipe);
         BlockStateIngredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getIngredient());
      }
   }
}
