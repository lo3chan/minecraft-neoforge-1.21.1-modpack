package dev.latvian.mods.kubejs.recipe.schema.function;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.ArrayList;
import java.util.List;

public class RecipeSchemaJSFunction extends BaseFunction {
   public final KubeRecipe recipe;
   public final TypeInfo[] argTypes;
   public final ResolvedRecipeSchemaFunction func;

   public RecipeSchemaJSFunction(KubeRecipe recipe, TypeInfo[] argTypes, ResolvedRecipeSchemaFunction func) {
      this.recipe = recipe;
      this.argTypes = argTypes;
      this.func = func;
   }

   public KubeRecipe call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      if (this.argTypes.length == 0) {
         this.func.execute(new RecipeScriptContext.Impl(cx, this.recipe), List.of());
         return this.recipe;
      } else {
         ArrayList<Object> argList = new ArrayList<>(this.argTypes.length);

         for (int i = 0; i < args.length; i++) {
            argList.add(cx.jsToJava(args[i], this.argTypes[i]));
         }

         this.func.execute(new RecipeScriptContext.Impl(cx, this.recipe), argList);
         return this.recipe;
      }
   }
}
