package com.github.alexthe666.citadel.server.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;

public class CitadelRecipes {
   private static final List<RecipeHolder<SmithingRecipe>> smithingRecipes = new ArrayList<>();

   public static void registerSmithingRecipe(RecipeHolder<SmithingRecipe> recipe) {
      smithingRecipes.add(recipe);
   }

   public static List<RecipeHolder<SmithingRecipe>> getSmithingRecipes() {
      return smithingRecipes;
   }
}
