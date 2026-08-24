package mezz.jei.api.runtime;

import java.util.Collection;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IIngredientVisibility {
   <V> boolean isIngredientVisible(IIngredientType<V> var1, V var2);

   <V> boolean isIngredientVisible(ITypedIngredient<V> var1);

   void registerListener(IIngredientVisibility.IListener var1);

   public interface IListener {
      <V> void onIngredientVisibilityChanged(ITypedIngredient<V> var1, boolean var2);

      default <V> void onIngredientsVisibilityChanged(Collection<ITypedIngredient<V>> ingredients, boolean visible) {
         for (ITypedIngredient<V> ingredient : ingredients) {
            this.onIngredientVisibilityChanged(ingredient, visible);
         }
      }
   }
}
