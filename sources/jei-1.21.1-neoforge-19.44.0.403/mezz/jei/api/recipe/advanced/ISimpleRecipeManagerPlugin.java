package mezz.jei.api.recipe.advanced;

import java.util.List;
import mezz.jei.api.ingredients.ITypedIngredient;

public interface ISimpleRecipeManagerPlugin<T> {
   boolean isHandledInput(ITypedIngredient<?> var1);

   boolean isHandledOutput(ITypedIngredient<?> var1);

   List<T> getRecipesForInput(ITypedIngredient<?> var1);

   List<T> getRecipesForOutput(ITypedIngredient<?> var1);

   List<T> getAllRecipes();
}
