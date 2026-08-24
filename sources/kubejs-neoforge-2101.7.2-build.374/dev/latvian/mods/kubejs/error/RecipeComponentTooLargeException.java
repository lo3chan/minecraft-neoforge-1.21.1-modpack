package dev.latvian.mods.kubejs.error;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class RecipeComponentTooLargeException extends InvalidRecipeComponentValueException {
   public final int max;

   public RecipeComponentTooLargeException(RecipeComponent<?> component, Object value, int size, int max) {
      super("Component '" + component.toString() + "' is too large (" + size + ")! Max size is " + max, component, value);
      this.max = max;
      this.customData("size", size);
      this.customData("max_size", max);
   }
}
