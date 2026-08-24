package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class UnknownKubeRecipe extends KubeRecipe {
   public static final KubeRecipeFactory RECIPE_FACTORY = new KubeRecipeFactory(KubeJS.id("unknown"), UnknownKubeRecipe.class, UnknownKubeRecipe::new);

   @Override
   public void deserialize(boolean merge) {
   }

   @Override
   public void serialize() {
   }

   @Override
   public boolean hasInput(RecipeMatchContext cx, ReplacementMatchInfo match) {
      if (CommonProperties.get().matchJsonRecipes && match.match() instanceof ItemMatch m) {
         Recipe<?> original = this.getOriginalRecipe();
         if (original == null) {
            return false;
         }

         NonNullList<Ingredient> arr = original.getIngredients();
         if (arr == null || arr.isEmpty()) {
            return false;
         }

         for (Ingredient ingredient : arr) {
            if (ingredient != null && ingredient != Ingredient.EMPTY && ingredient.kjs$canBeUsedForMatching() && m.matches(cx, ingredient, match.exact())) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean replaceInput(RecipeScriptContext cx, ReplacementMatchInfo match, Object with) {
      return false;
   }

   @Override
   public boolean hasOutput(RecipeMatchContext cx, ReplacementMatchInfo match) {
      if (CommonProperties.get().matchJsonRecipes && match.match() instanceof ItemMatch m) {
         Recipe<?> original = this.getOriginalRecipe();
         if (original == null) {
            return false;
         } else {
            ItemStack result = original.getResultItem(this.type.event.registries.access());
            return result != null && result != ItemStack.EMPTY && !result.isEmpty() && m.matches(cx, result, match.exact());
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean replaceOutput(RecipeScriptContext cx, ReplacementMatchInfo match, Object with) {
      return false;
   }
}
