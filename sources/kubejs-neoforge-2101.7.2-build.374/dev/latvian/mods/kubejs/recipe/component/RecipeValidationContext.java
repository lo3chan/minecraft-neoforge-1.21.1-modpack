package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.KubeRecipeContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.util.ErrorStack;

public interface RecipeValidationContext extends KubeRecipeContext, RecipeMatchContext {
   ErrorStack errors();

   public record Impl(KubeRecipe recipe, ErrorStack errors) implements RecipeValidationContext {
   }
}
