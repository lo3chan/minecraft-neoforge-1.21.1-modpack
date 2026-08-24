package mezz.jei.gui.recipes.lookups;

import java.util.List;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.util.MathUtil;

public class SingleCategoryLookupState implements ILookupState {
   private final IFocusedRecipes<?> focusedRecipes;
   private final IFocusGroup focusGroup;
   private int recipesPerPage = 1;
   private int recipeIndex;

   public SingleCategoryLookupState(IFocusedRecipes<?> focusedRecipes, IFocusGroup focusGroup) {
      this.focusedRecipes = focusedRecipes;
      this.focusGroup = focusGroup;
   }

   @Override
   public List<IRecipeCategory<?>> getRecipeCategories() {
      return List.of(this.focusedRecipes.getRecipeCategory());
   }

   @Override
   public boolean moveToRecipeCategory(IRecipeCategory<?> recipeCategory) {
      RecipeType<?> recipeType = this.focusedRecipes.getRecipeCategory().getRecipeType();
      return recipeCategory.getRecipeType().equals(recipeType);
   }

   @Override
   public int getRecipesPerPage() {
      return this.recipesPerPage;
   }

   @Override
   public void setRecipesPerPage(int recipesPerPage) {
      this.recipesPerPage = recipesPerPage;
   }

   @Override
   public int getRecipeIndex() {
      return this.recipeIndex;
   }

   @Override
   public IFocusGroup getFocuses() {
      return this.focusGroup;
   }

   @Override
   public IFocusedRecipes<?> getFocusedRecipes() {
      return this.focusedRecipes;
   }

   @Override
   public boolean nextRecipeCategory() {
      return false;
   }

   @Override
   public boolean previousRecipeCategory() {
      return false;
   }

   @Override
   public void goToFirstPage() {
      this.recipeIndex = 0;
   }

   @Override
   public boolean nextPage() {
      int oldIndex = this.recipeIndex;
      int recipeCount = this.recipeCount();
      this.recipeIndex = this.recipeIndex + this.recipesPerPage;
      if (this.recipeIndex >= recipeCount) {
         this.recipeIndex = 0;
      }

      return this.recipeIndex != oldIndex;
   }

   @Override
   public boolean previousPage() {
      int oldIndex = this.recipeIndex;
      this.recipeIndex = this.recipeIndex - this.recipesPerPage;
      if (this.recipeIndex < 0) {
         int pageCount = this.pageCount();
         this.recipeIndex = (pageCount - 1) * this.recipesPerPage;
      }

      return this.recipeIndex != oldIndex;
   }

   public int recipeCount() {
      return this.getFocusedRecipes().getRecipes().size();
   }

   @Override
   public int pageCount() {
      int recipeCount = this.recipeCount();
      return recipeCount <= 1 ? 1 : MathUtil.divideCeil(recipeCount, this.recipesPerPage);
   }
}
