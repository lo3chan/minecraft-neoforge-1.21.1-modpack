package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.recipe.viewer.AddEntriesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IJeiRuntime;

public class JEIAddEntriesKubeEvent implements AddEntriesKubeEvent {
   private final IJeiRuntime runtime;
   private final RecipeViewerEntryType type;
   private final IIngredientType ingredientType;
   private final List<Object> added;

   public JEIAddEntriesKubeEvent(IJeiRuntime r, RecipeViewerEntryType type, IIngredientType<?> t) {
      this.runtime = r;
      this.type = type;
      this.ingredientType = t;
      this.added = new ArrayList<>();
   }

   @Override
   public void add(Context cx, Object[] items) {
      for (Object o : items) {
         this.added.add(this.type.wrapEntry(cx, o));
      }
   }

   @Override
   public void afterPosted(EventResult result) {
      if (!this.added.isEmpty()) {
         this.runtime.getIngredientManager().addIngredientsAtRuntime(this.ingredientType, this.added);
      }
   }
}
