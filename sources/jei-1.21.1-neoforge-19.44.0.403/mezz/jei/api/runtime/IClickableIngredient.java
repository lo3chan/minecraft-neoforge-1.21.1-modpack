package mezz.jei.api.runtime;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;

public interface IClickableIngredient<T> {
   ITypedIngredient<T> getTypedIngredient();

   @Deprecated(
      since = "19.23.0",
      forRemoval = true
   )
   default IIngredientType<T> getIngredientType() {
      return this.getTypedIngredient().getType();
   }

   @Deprecated(
      since = "19.23.0",
      forRemoval = true
   )
   default T getIngredient() {
      ITypedIngredient<T> typedIngredient = this.getTypedIngredient();
      return typedIngredient.getIngredient();
   }

   Rect2i getArea();
}
