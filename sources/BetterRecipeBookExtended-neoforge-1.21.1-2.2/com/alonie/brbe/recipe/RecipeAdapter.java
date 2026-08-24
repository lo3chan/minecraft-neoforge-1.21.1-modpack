package com.alonie.brbe.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class RecipeAdapter {
   private RecipeAdapter() {
   }

   public static DisplayRecipe fromHolder(RecipeHolder<?> holder) {
      ItemStack result = holder.value().getResultItem(null);
      return new RecipeAdapter.SimpleDisplayRecipe(holder.id(), result, result.getHoverName().getString());
   }

   public static DisplayRecipe of(ResourceLocation id, ItemStack result, String searchString) {
      return new RecipeAdapter.SimpleDisplayRecipe(id, result, searchString);
   }

   private record SimpleDisplayRecipe(ResourceLocation id, ItemStack result, String searchString) implements DisplayRecipe {
      @Override
      public ItemStack getResult() {
         return this.result.copy();
      }

      @Override
      public String getSearchString() {
         return this.searchString;
      }
   }
}
