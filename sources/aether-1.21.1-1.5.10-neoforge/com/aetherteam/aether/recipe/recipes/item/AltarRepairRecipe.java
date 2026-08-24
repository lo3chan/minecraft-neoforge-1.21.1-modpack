package com.aetherteam.aether.recipe.recipes.item;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.recipe.AetherBookCategory;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;

public class AltarRepairRecipe extends AbstractAetherCookingRecipe {
   public final Ingredient ingredient;

   public AltarRepairRecipe(String group, Ingredient ingredient, int repairTime) {
      super(
         (RecipeType<?>)AetherRecipeTypes.ENCHANTING.get(), group, AetherBookCategory.ENCHANTING_REPAIR, ingredient, ingredient.getItems()[0], 0.0F, repairTime
      );
      this.ingredient = ingredient;
   }

   public ItemStack assemble(SingleRecipeInput inventory, Provider provider) {
      return this.ingredient.getItems()[0];
   }

   public ItemStack getResultItem(Provider provider) {
      return this.ingredient.getItems()[0];
   }

   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)AetherBlocks.ALTAR.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AetherRecipeSerializers.REPAIRING.get();
   }

   public static class Serializer implements RecipeSerializer<AltarRepairRecipe> {
      private static final MapCodec<AltarRepairRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.STRING.optionalFieldOf("group", "").forGetter(AbstractCookingRecipe::getGroup),
               Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
               Codec.INT.fieldOf("repairTime").orElse(500).forGetter(recipe -> recipe.cookingTime)
            )
            .apply(instance, AltarRepairRecipe::new)
      );

      public MapCodec<AltarRepairRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, AltarRepairRecipe> streamCodec() {
         return StreamCodec.of(this::toNetwork, this::fromNetwork);
      }

      public AltarRepairRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         String group = buffer.readUtf();
         Ingredient ingredient = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         int cookingTime = buffer.readVarInt();
         return new AltarRepairRecipe(group, ingredient, cookingTime);
      }

      public void toNetwork(RegistryFriendlyByteBuf buffer, AltarRepairRecipe recipe) {
         buffer.writeUtf(recipe.group);
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
         buffer.writeVarInt(recipe.cookingTime);
      }
   }
}
