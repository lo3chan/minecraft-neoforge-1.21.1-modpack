package mezz.jei.common.ingredients;

import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;

@FunctionalInterface
public interface ITypedIngredientFactory {
   <T> Optional<ITypedIngredient<T>> createTypedIngredient(IIngredientType<T> var1, T var2, boolean var3);
}
