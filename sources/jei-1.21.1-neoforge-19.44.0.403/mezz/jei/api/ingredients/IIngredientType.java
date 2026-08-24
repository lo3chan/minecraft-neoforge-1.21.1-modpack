package mezz.jei.api.ingredients;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IIngredientType<T> {
   Class<? extends T> getIngredientClass();

   default String getUid() {
      Class<? extends T> ingredientClass = this.getIngredientClass();
      return ingredientClass.getName();
   }

   default Optional<T> castIngredient(@Nullable Object ingredient) {
      Class<? extends T> ingredientClass = this.getIngredientClass();
      return ingredientClass.isInstance(ingredient) ? Optional.of((T)ingredientClass.cast(ingredient)) : Optional.empty();
   }

   @Nullable
   default T getCastIngredient(@Nullable Object ingredient) {
      Class<? extends T> ingredientClass = this.getIngredientClass();
      return (T)(ingredientClass.isInstance(ingredient) ? ingredientClass.cast(ingredient) : null);
   }
}
