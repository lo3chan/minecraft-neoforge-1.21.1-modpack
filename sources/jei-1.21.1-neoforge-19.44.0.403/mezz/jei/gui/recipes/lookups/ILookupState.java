package mezz.jei.gui.recipes.lookups;

import java.util.List;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import mezz.jei.gui.recipes.layouts.IRecipeLayoutList;

public interface ILookupState {
   List<IRecipeCategory<?>> getRecipeCategories();

   boolean moveToRecipeCategory(IRecipeCategory<?> var1);

   int getRecipesPerPage();

   void setRecipesPerPage(int var1);

   int getRecipeIndex();

   IFocusGroup getFocuses();

   IFocusedRecipes<?> getFocusedRecipes();

   boolean nextRecipeCategory();

   boolean previousRecipeCategory();

   void goToFirstPage();

   boolean nextPage();

   boolean previousPage();

   int pageCount();

   default List<IRecipeLayoutWithButtons<?>> getVisible(IRecipeLayoutList recipes) {
      int recipesPerPage = this.getRecipesPerPage();
      int firstRecipeIndex = this.getRecipeIndex() - this.getRecipeIndex() % recipesPerPage;
      int maxIndex = Math.min(recipes.size(), firstRecipeIndex + recipesPerPage);
      return firstRecipeIndex >= maxIndex ? List.of() : recipes.subList(firstRecipeIndex, maxIndex);
   }
}
