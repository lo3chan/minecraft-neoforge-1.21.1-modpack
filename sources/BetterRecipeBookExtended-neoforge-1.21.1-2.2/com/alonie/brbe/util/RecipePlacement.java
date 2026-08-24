package com.alonie.brbe.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipePlacement implements PlaceRecipe<Ingredient> {
   protected List<List<Ingredient>> placement = new ArrayList<>();

   public RecipePlacement(int size) {
      for (int i = 0; i < size; i++) {
         this.placement.add(new ArrayList<>());
      }
   }

   public void addItemToSlot(Ingredient ingredient, int menuSlot, int j, int k, int l) {
      if (!ingredient.isEmpty() && menuSlot < this.placement.size()) {
         this.placement.get(menuSlot).add(ingredient);
      }
   }

   public List<List<Ingredient>> getPlacement() {
      return this.placement;
   }

   public static List<List<Ingredient>> create(RecipeHolder<?> recipe, int gridWidth, int gridHeight) {
      RecipePlacement placer = new RecipePlacement(gridWidth * gridHeight);
      placer.placeRecipe(gridWidth, gridHeight, -1, recipe, recipe.value().getIngredients().iterator(), 0);
      return placer.getPlacement();
   }
}
