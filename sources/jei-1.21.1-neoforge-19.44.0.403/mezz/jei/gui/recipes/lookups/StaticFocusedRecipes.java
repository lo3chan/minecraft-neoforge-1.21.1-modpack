package mezz.jei.gui.recipes.lookups;

import java.util.List;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.jetbrains.annotations.Unmodifiable;

public record StaticFocusedRecipes<T>(IRecipeCategory<T> recipeCategory, List<T> recipes) implements IFocusedRecipes<T> {
   @Override
   public IRecipeCategory<T> getRecipeCategory() {
      return this.recipeCategory;
   }

   @Unmodifiable
   @Override
   public List<T> getRecipes() {
      return this.recipes;
   }
}
