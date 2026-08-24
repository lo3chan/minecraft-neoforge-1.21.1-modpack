package dev.latvian.mods.kubejs.recipe.schema.function;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import java.util.List;
import java.util.stream.Collectors;

public record RecipeFunctionInstance(String name, List<RecipeComponent<?>> arguments, ResolvedRecipeSchemaFunction function) {
   public RecipeFunctionInstance(String name, ResolvedRecipeSchemaFunction function) {
      this(name, function.arguments(), function);
   }

   @Override
   public String toString() {
      return this.arguments.stream().<CharSequence>map(RecipeComponent::toString).collect(Collectors.joining(", ", this.name + "(", ")"));
   }
}
