package mezz.jei.api.ingredients;

import java.util.List;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IIngredientSupplier {
   @Unmodifiable
   List<ITypedIngredient<?>> getIngredients(RecipeIngredientRole var1);
}
