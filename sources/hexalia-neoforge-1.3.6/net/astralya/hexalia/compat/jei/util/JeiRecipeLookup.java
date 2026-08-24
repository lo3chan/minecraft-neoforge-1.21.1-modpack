package net.astralya.hexalia.compat.jei.util;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public final class JeiRecipeLookup {
   private JeiRecipeLookup() {
   }

   public static <I extends RecipeInput, T extends Recipe<I>> List<T> getRecipes(RecipeType<T> recipeType) {
      Level level = Minecraft.getInstance().level;
      return level == null ? List.of() : getRecipes(level.getRecipeManager(), recipeType);
   }

   public static <I extends RecipeInput, T extends Recipe<I>> List<T> getRecipes(RecipeManager recipeManager, RecipeType<T> recipeType) {
      return recipeManager.getAllRecipesFor(recipeType).stream().<T>map(RecipeHolder::value).toList();
   }
}
