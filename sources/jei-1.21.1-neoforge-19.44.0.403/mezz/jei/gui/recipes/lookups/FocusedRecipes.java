package mezz.jei.gui.recipes.lookups;

import java.util.List;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class FocusedRecipes<T> implements IFocusedRecipes<T> {
   private final IRecipeManager recipeManager;
   private final IRecipeCategory<T> recipeCategory;
   private final IFocusGroup focuses;
   @Nullable
   private List<T> recipes;

   public static <T> IFocusedRecipes<T> create(IFocusGroup focuses, IRecipeManager recipeManager, IRecipeCategory<T> recipeCategory) {
      return new FocusedRecipes<>(focuses, recipeManager, recipeCategory);
   }

   private FocusedRecipes(IFocusGroup focuses, IRecipeManager recipeManager, IRecipeCategory<T> recipeCategory) {
      this.focuses = focuses;
      this.recipeManager = recipeManager;
      this.recipeCategory = recipeCategory;
      this.recipes = null;
   }

   @Override
   public IRecipeCategory<T> getRecipeCategory() {
      return this.recipeCategory;
   }

   @Unmodifiable
   @Override
   public List<T> getRecipes() {
      if (this.recipes == null) {
         this.recipes = this.recipeManager.createRecipeLookup(this.recipeCategory.getRecipeType()).limitFocus(this.focuses.getAllFocuses()).get().toList();
      }

      return this.recipes;
   }
}
