package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Wrapper;

public class RecipeComponentValueFunction extends BaseFunction {
   public final KubeRecipe recipe;
   public final RecipeComponentValue<?> componentValue;

   public RecipeComponentValueFunction(KubeRecipe recipe, RecipeComponentValue<?> componentValue) {
      this.recipe = recipe;
      this.componentValue = componentValue;
   }

   public KubeRecipe call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return this.recipe
         .setValue(
            this.componentValue.key, Cast.to(this.componentValue.key.component.wrap(new RecipeScriptContext.Impl(cx, this.recipe), Wrapper.unwrapped(args[0])))
         );
   }
}
