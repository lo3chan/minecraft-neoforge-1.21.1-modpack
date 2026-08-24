package com.alonie.brbe.generic;

import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.RecipeBookType;

public class GenericClientRecipeBook extends RecipeBook {
   private boolean filteringCraftable;

   public boolean isFilteringCraftable() {
      return this.filteringCraftable;
   }

   public boolean isFiltering(RecipeBookType category) {
      return this.filteringCraftable;
   }

   public void setFilteringCraftable(boolean filteringCraftable) {
      this.filteringCraftable = filteringCraftable;
   }
}
