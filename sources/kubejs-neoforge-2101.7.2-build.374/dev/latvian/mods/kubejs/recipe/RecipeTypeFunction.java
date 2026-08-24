package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.recipe.schema.RecipeConstructor;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.ErrorStack;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Wrapper;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecipeTypeFunction extends BaseFunction implements WrappedJS {
   public static final Pattern SKIP_ERROR = ConsoleJS.methodPattern(RecipeTypeFunction.class, "call");
   public final RecipesKubeEvent event;
   public final ResourceKey<RecipeSerializer<?>> serializerKey;
   public final ResourceLocation id;
   public final String idString;
   public final RecipeSchemaType schemaType;

   public RecipeTypeFunction(RecipesKubeEvent event, RecipeSchemaType schemaType) {
      this.event = event;
      this.serializerKey = schemaType.serializerKey;
      this.id = schemaType.id;
      this.idString = this.id.toString();
      this.schemaType = schemaType;
   }

   public KubeRecipe call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args0) {
      SourceLine sourceLine = SourceLine.of(cx);
      ErrorStack stack = new ErrorStack();

      try {
         return this.createRecipe(cx, sourceLine, stack, args0);
      } catch (Throwable var9) {
         KubeRecipe r = this.schemaType.schema.recipeFactory.create(this, sourceLine, true);
         r.creationError = true;
         this.event.failedCount++;
         ConsoleJS.SERVER
            .error(
               "Failed to create a '" + this.idString + "' recipe" + stack.atString() + " from args " + Arrays.toString(args0), sourceLine, var9, SKIP_ERROR
            );
         r.json = new JsonObject();
         r.json.addProperty("type", this.idString);
         r.newRecipe = true;
         return r;
      }
   }

   public KubeRecipe createRecipe(Context cx, SourceLine sourceLine, ErrorStack stack, Object[] args) {
      try {
         for (int i = 0; i < args.length; i++) {
            args[i] = Wrapper.unwrapped(args[i]);
         }

         this.schemaType.getSerializer();
         RecipeConstructor constructor = (RecipeConstructor)this.schemaType.schema.constructors().get(args.length);
         if (constructor == null) {
            if (args.length != 1 || !(args[0] instanceof Map) && !(args[0] instanceof JsonObject)) {
               throw new KubeRuntimeException("Constructor for " + this.id + " with " + args.length + " arguments not found!").source(sourceLine);
            } else {
               KubeRecipe recipe = this.schemaType.schema.deserialize(sourceLine, this, null, JsonUtils.objectOf(cx, args[0]));
               recipe.afterLoaded(stack);
               return this.event.addRecipe(recipe, true);
            }
         } else {
            ComponentValueMap argMap = new ComponentValueMap(args.length);
            int index = 0;

            for (RecipeKey<?> key : constructor.keys) {
               argMap.put(key, Wrapper.unwrapped(args[index++]));
            }

            KubeRecipe recipe = constructor.create(cx, sourceLine, this, this.schemaType, argMap);
            recipe.afterLoaded(stack);
            return this.event.addRecipe(recipe, false);
         }
      } catch (KubeRuntimeException var10) {
         throw var10.source(sourceLine);
      } catch (Throwable var11) {
         throw new KubeRuntimeException(
               "Failed to create a recipe for type '"
                  + this.id
                  + "'"
                  + stack.atString()
                  + " with args "
                  + Arrays.stream(args).map(o -> o == null ? "null" : o + ": " + o.getClass().getSimpleName()).collect(Collectors.joining(", ", "[", "]")),
               var11
            )
            .source(sourceLine);
      }
   }

   @Override
   public String toString() {
      return this.idString;
   }

   @Override
   public int hashCode() {
      return this.idString.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return this.idString.equals(obj.toString());
   }
}
