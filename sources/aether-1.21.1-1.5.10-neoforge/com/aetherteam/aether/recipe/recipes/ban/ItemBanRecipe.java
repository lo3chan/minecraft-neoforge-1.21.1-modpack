package com.aetherteam.aether.recipe.recipes.ban;

import com.aetherteam.aether.event.AetherEventDispatch;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.serializer.PlacementBanRecipeSerializer;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class ItemBanRecipe extends AbstractPlacementBanRecipe<ItemStack, Ingredient, SingleRecipeInput> {
   public ItemBanRecipe(Either<ResourceKey<Biome>, TagKey<Biome>> biome, Optional<BlockStateIngredient> bypassBlock, Ingredient ingredient) {
      super((RecipeType<?>)AetherRecipeTypes.ITEM_PLACEMENT_BAN.get(), biome, bypassBlock, ingredient);
   }

   public ItemBanRecipe(Either<ResourceKey<Biome>, TagKey<Biome>> biome, Optional<BlockStateIngredient> bypassBlock) {
      this(biome, bypassBlock, Ingredient.EMPTY);
   }

   public boolean banItem(Level level, BlockPos pos, Direction direction, ItemStack stack, boolean spawnParticles) {
      if (this.matches(level, pos, stack) && AetherEventDispatch.isItemPlacementBanned(level, pos, stack)) {
         if (spawnParticles) {
            AetherEventDispatch.onPlacementSpawnParticles(level, pos, direction, stack, null);
         }

         return true;
      } else {
         return false;
      }
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AetherRecipeSerializers.ITEM_PLACEMENT_BAN.get();
   }

   public static class Serializer extends PlacementBanRecipeSerializer<ItemStack, Ingredient, SingleRecipeInput, ItemBanRecipe> {
      public Serializer() {
         super(ItemBanRecipe::new);
      }

      public MapCodec<ItemBanRecipe> codec() {
         return RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                  BlockStateRecipeUtil.KEY_CODEC.fieldOf("biome").forGetter(AbstractPlacementBanRecipe::getBiome),
                  BlockStateIngredient.CODEC.optionalFieldOf("bypass").forGetter(AbstractPlacementBanRecipe::getBypassBlock),
                  Ingredient.CODEC.fieldOf("ingredient").forGetter(AbstractPlacementBanRecipe::getIngredient)
               )
               .apply(inst, this.getFactory())
         );
      }

      public StreamCodec<RegistryFriendlyByteBuf, ItemBanRecipe> streamCodec() {
         return StreamCodec.of(this::toNetwork, this::fromNetwork);
      }

      public ItemBanRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         Either<ResourceKey<Biome>, TagKey<Biome>> biome = (Either<ResourceKey<Biome>, TagKey<Biome>>)BlockStateRecipeUtil.STREAM_CODEC.decode(buffer);
         Optional<BlockStateIngredient> bypassBlock = buffer.readOptional(
            buf -> (BlockStateIngredient)BlockStateIngredient.CONTENTS_STREAM_CODEC.decode(buffer)
         );
         Ingredient ingredient = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         return new ItemBanRecipe(biome, bypassBlock, ingredient);
      }

      public void toNetwork(RegistryFriendlyByteBuf buffer, ItemBanRecipe recipe) {
         super.toNetwork(buffer, recipe);
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getIngredient());
      }
   }
}
