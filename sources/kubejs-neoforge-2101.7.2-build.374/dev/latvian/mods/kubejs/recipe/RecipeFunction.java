package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValueFunction;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeFunctionInstance;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeSchemaJSFunction;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeFunction extends NativeJavaObject {
   public final KubeRecipe recipe;
   public final Map<String, BaseFunction> builderFunctions;

   public static boolean isValidIdentifier(char[] name) {
      if (name.length != 0 && Character.isJavaIdentifierStart(name[0])) {
         for (int i = 1; i < name.length; i++) {
            if (!Character.isJavaIdentifierPart(name[i])) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public RecipeFunction(Context cx, Scriptable scope, TypeInfo staticType, KubeRecipe recipe) {
      super(scope, recipe, staticType, cx);
      this.recipe = recipe;
      this.builderFunctions = new HashMap<>();

      for (RecipeComponentValue<?> value : recipe.getRecipeComponentValues()) {
         List<String> names = value.key.getValidFunctionNames();
         if (!names.isEmpty()) {
            RecipeComponentValueFunction func = new RecipeComponentValueFunction(recipe, value);

            for (String name : names) {
               this.builderFunctions.put(name, func);
            }
         }
      }

      for (RecipeFunctionInstance func : recipe.type.schemaType.schema.functions.values()) {
         if (isValidIdentifier(func.name().toCharArray())) {
            this.builderFunctions
               .put(
                  func.name(),
                  new RecipeSchemaJSFunction(recipe, func.arguments().stream().map(RecipeComponent::typeInfo).toArray(TypeInfo[]::new), func.function())
               );
         }
      }
   }

   public Object get(Context cx, String name, Scriptable start) {
      Object s = super.get(cx, name, start);
      if (s == Scriptable.NOT_FOUND) {
         BaseFunction r = this.builderFunctions.get(name);
         if (r != null) {
            return r;
         }
      }

      return s;
   }
}
