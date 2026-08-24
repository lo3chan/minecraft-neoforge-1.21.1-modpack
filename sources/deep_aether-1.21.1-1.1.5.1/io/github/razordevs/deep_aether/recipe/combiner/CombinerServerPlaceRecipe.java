package io.github.razordevs.deep_aether.recipe.combiner;

import java.util.Iterator;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.RecipeHolder;

public class CombinerServerPlaceRecipe extends ServerPlaceRecipe<CombinerRecipeInput, CombinerRecipe> {
   public CombinerServerPlaceRecipe(RecipeBookMenu<CombinerRecipeInput, CombinerRecipe> recipeBookMenu) {
      super(recipeBookMenu);
   }

   public void placeRecipe(int width, int height, int result, RecipeHolder<?> p_301225_, Iterator<Integer> iterator, int idk) {
      int k1 = 0;
      if (k1 == result) {
         k1++;
      }

      for (int slot = 0; slot < 3; slot++) {
         if (!iterator.hasNext()) {
            return;
         }

         this.addItemToSlot(iterator.next(), k1, idk, slot, 0);
         k1++;
      }
   }
}
