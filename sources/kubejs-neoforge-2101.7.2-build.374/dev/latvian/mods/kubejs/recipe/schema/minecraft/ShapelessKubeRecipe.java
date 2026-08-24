package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;

public class ShapelessKubeRecipe extends KubeRecipe {
   public static final KubeRecipeFactory RECIPE_FACTORY = new KubeRecipeFactory(KubeJS.id("shapeless"), ShapelessKubeRecipe.class, ShapelessKubeRecipe::new);

   @Override
   public RecipeTypeFunction getSerializationTypeFunction() {
      return this.type == this.type.event.shapeless
            && this.type.event.shapeless != this.type.event.vanillaShapeless
            && !this.json.has("kubejs:ingredient_actions")
            && !this.json.has("kubejs:modify_result")
         ? this.type.event.vanillaShapeless
         : super.getSerializationTypeFunction();
   }
}
