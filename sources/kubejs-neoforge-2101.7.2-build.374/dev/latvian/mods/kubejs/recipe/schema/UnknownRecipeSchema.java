package dev.latvian.mods.kubejs.recipe.schema;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class UnknownRecipeSchema extends RecipeSchema {
   public static final RecipeSchema SCHEMA = new UnknownRecipeSchema().factory(UnknownKubeRecipe.RECIPE_FACTORY);

   private UnknownRecipeSchema() {
      super(Map.of(), List.of());
   }
}
