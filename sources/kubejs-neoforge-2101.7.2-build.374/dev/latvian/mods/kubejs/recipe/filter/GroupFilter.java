package dev.latvian.mods.kubejs.recipe.filter;

public class GroupFilter implements RecipeFilter {
   private final String group;

   public GroupFilter(String g) {
      this.group = g;
   }

   @Override
   public boolean test(RecipeMatchContext cx) {
      return cx.recipe().kjs$getGroup().equals(this.group);
   }

   @Override
   public String toString() {
      return "GroupFilter{group='" + this.group + "'}";
   }
}
