package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;

public class ShapedKubeRecipe extends KubeRecipe {
   public static final KubeRecipeFactory RECIPE_FACTORY = new KubeRecipeFactory(KubeJS.id("shaped"), ShapedKubeRecipe.class, ShapedKubeRecipe::new);

   @Override
   public RecipeTypeFunction getSerializationTypeFunction() {
      return this.type == this.type.event.shaped
            && this.type.event.shaped != this.type.event.vanillaShaped
            && !this.json.has("kubejs:ingredient_actions")
            && !this.json.has("kubejs:modify_result")
            && !this.json.has("kubejs:mirror")
         ? this.type.event.vanillaShaped
         : super.getSerializationTypeFunction();
   }
}
