package dev.latvian.mods.kubejs.recipe.filter;

public record NotFilter(RecipeFilter original) implements RecipeFilter {
   @Override
   public boolean test(RecipeMatchContext cx) {
      return !this.original.test(cx);
   }

   @Override
   public String toString() {
      return "NotFilter{" + this.original + "}";
   }
}
