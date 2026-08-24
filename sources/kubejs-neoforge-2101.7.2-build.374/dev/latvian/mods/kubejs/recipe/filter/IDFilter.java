package dev.latvian.mods.kubejs.recipe.filter;

import net.minecraft.resources.ResourceLocation;

public class IDFilter implements RecipeFilter {
   public final ResourceLocation id;

   public IDFilter(ResourceLocation i) {
      this.id = i;
   }

   @Override
   public boolean test(RecipeMatchContext cx) {
      return cx.recipe().kjs$getOrCreateId().equals(this.id);
   }

   @Override
   public String toString() {
      return "IDFilter{id=" + this.id + "}";
   }
}
