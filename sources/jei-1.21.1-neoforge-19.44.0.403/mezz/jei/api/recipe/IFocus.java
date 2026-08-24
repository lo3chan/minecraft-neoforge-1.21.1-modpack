package mezz.jei.api.recipe;

import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IFocus<V> {
   ITypedIngredient<V> getTypedValue();

   RecipeIngredientRole getRole();

   <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> var1);
}
