package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IJeiRuntime;

public class JEIRemoveEntriesKubeEvent implements RemoveEntriesKubeEvent {
   private final IJeiRuntime runtime;
   private final RecipeViewerEntryType type;
   private final IIngredientType ingredientType;
   private final Collection<Object> hidden;
   private final List<Object> allIngredients;

   public JEIRemoveEntriesKubeEvent(IJeiRuntime r, RecipeViewerEntryType type, IIngredientType<?> t) {
      this.runtime = r;
      this.type = type;
      this.ingredientType = t;
      this.hidden = new HashSet<>();
      this.allIngredients = List.copyOf(this.runtime.getIngredientManager().getAllIngredients(this.ingredientType));
   }

   @Override
   public void remove(Context cx, Object filter) {
      Predicate predicate = (Predicate)this.type.wrapPredicate(cx, filter);

      for (Object value : this.allIngredients) {
         if (predicate.test(value)) {
            this.hidden.add(value);
         }
      }
   }

   @Override
   public void afterPosted(EventResult result) {
      if (!this.hidden.isEmpty()) {
         this.runtime.getIngredientManager().removeIngredientsAtRuntime(this.ingredientType, this.hidden);
      }
   }
}
