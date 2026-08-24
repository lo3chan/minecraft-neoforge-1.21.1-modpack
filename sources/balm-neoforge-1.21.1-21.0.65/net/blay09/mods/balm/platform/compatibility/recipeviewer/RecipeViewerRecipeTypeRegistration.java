package net.blay09.mods.balm.platform.compatibility.recipeviewer;

import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface RecipeViewerRecipeTypeRegistration<T> {
   default RecipeViewerRecipeTypeRegistration<T> withCraftingStation(ItemLike itemLike) {
      return this.withCraftingStation(new ItemStack(itemLike));
   }

   RecipeViewerRecipeTypeRegistration<T> withCraftingStation(ItemStack var1);

   RecipeViewerRecipeTypeRegistration<T> withRecipe(T var1);

   RecipeViewerRecipeTypeRegistration<T> withRecipes(Collection<T> var1);

   void buildDisplay(Consumer<RecipeViewerDisplayBuilder<T>> var1);
}
