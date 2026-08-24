package dev.latvian.mods.kubejs.recipe.filter;

public class ModFilter implements RecipeFilter {
   private final String mod;

   public ModFilter(String m) {
      this.mod = m;
   }

   @Override
   public boolean test(RecipeMatchContext cx) {
      return cx.recipe().kjs$getMod().equals(this.mod);
   }

   @Override
   public String toString() {
      return "ModFilter{mod='" + this.mod + "'}";
   }
}
