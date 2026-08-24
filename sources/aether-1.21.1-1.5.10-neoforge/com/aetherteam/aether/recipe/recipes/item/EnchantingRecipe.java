package com.aetherteam.aether.recipe.recipes.item;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.recipe.AetherBookCategory;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.serializer.AetherCookingSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

public class EnchantingRecipe extends AbstractAetherCookingRecipe {
   public EnchantingRecipe(String group, AetherBookCategory category, Ingredient ingredient, ItemStack result, float experience, int enchantingTime) {
      super((RecipeType<?>)AetherRecipeTypes.ENCHANTING.get(), group, category, ingredient, result, experience, enchantingTime);
   }

   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)AetherBlocks.ALTAR.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AetherRecipeSerializers.ENCHANTING.get();
   }

   public static class Serializer extends AetherCookingSerializer<EnchantingRecipe> {
      public Serializer() {
         super(EnchantingRecipe::new, 250);
      }
   }
}
