package mezz.jei.gui.recipes.lookups;

import java.util.List;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.jetbrains.annotations.Unmodifiable;

public interface IFocusedRecipes<T> {
   IRecipeCategory<T> getRecipeCategory();

   @Unmodifiable
   List<T> getRecipes();
}
