package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.DataResult.Success;
import dev.latvian.mods.kubejs.error.EmptyRecipeComponentException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.IntBounds;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.kubejs.util.TinyMap;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

public interface RecipeComponent<T> {
   static CustomObjectRecipeComponent builder(List<CustomObjectRecipeComponent.Key> keys) {
      return new CustomObjectRecipeComponent(keys);
   }

   static CustomObjectRecipeComponent builder(CustomObjectRecipeComponent.Key... keys) {
      return new CustomObjectRecipeComponent(List.of(keys));
   }

   default RecipeKey<T> key(String name, ComponentRole role) {
      return new RecipeKey<>(this, name, role);
   }

   default RecipeKey<T> inputKey(String name) {
      return this.key(name, ComponentRole.INPUT);
   }

   default RecipeKey<T> outputKey(String name) {
      return this.key(name, ComponentRole.OUTPUT);
   }

   default RecipeKey<T> otherKey(String name) {
      return this.key(name, ComponentRole.OTHER);
   }

   RecipeComponentType<?> type();

   Codec<T> codec();

   TypeInfo typeInfo();

   default boolean hasPriority(RecipeMatchContext cx, Object from) {
      return false;
   }

   default T wrap(RecipeScriptContext cx, Object from) {
      return (T)cx.cx().jsToJava(from, this.typeInfo());
   }

   default void writeToJson(KubeRecipe recipe, RecipeComponentValue<T> cv, JsonObject json) {
      if (cv.key.names.size() >= 2) {
         for (String k : cv.key.names) {
            json.remove(k);
         }
      }

      try {
         DataResult<JsonElement> result = cv.key.codec.encodeStart(recipe.type.event.ops.json(), cv.value);
         switch (result) {
            case Success<JsonElement> r:
               json.add(cv.key.name, (JsonElement)r.value());
               break;
            case Error<JsonElement> rx:
               ConsoleJS.SERVER
                  .error(
                     "Failed to encode " + cv.key.name + " for recipe " + recipe.id + " from value" + cv.value + ": " + rx.message(),
                     recipe.sourceLine,
                     null,
                     RecipesKubeEvent.POST_SKIP_ERROR
                  );
               break;
            default:
               throw new MatchException(null, null);
         }
      } catch (Exception var9) {
         ConsoleJS.SERVER
            .error(
               "Failed to encode " + cv.key.name + " for recipe " + recipe.id + " from value" + cv.value + ": " + var9,
               recipe.sourceLine,
               var9,
               RecipesKubeEvent.POST_SKIP_ERROR
            );
      }
   }

   default void readFromJson(KubeRecipe recipe, RecipeComponentValue<T> cv, JsonObject json) {
      JsonElement v = json.get(cv.key.name);
      if (v != null) {
         cv.value = (T)cv.key.codec.parse(recipe.type.event.ops.json(), v).getOrThrow();
      } else if (cv.key.names.size() >= 2) {
         for (String alt : cv.key.names) {
            v = json.get(alt);
            if (v != null) {
               cv.value = (T)cv.key.codec.parse(recipe.type.event.ops.json(), v).getOrThrow();
               return;
            }
         }
      }
   }

   default boolean matches(RecipeMatchContext cx, T value, ReplacementMatchInfo match) {
      return false;
   }

   default T replace(RecipeScriptContext cx, T original, ReplacementMatchInfo match, Object with) {
      return original instanceof Replaceable r && this.matches(cx, original, match) ? this.wrap(cx, r.replaceThisWith(cx, with)) : original;
   }

   default boolean allowEmpty() {
      return false;
   }

   default void validate(RecipeValidationContext ctx, T value) {
      if (!this.allowEmpty() && this.isEmpty(value)) {
         throw new EmptyRecipeComponentException(this, value);
      }
   }

   default boolean isEmpty(T value) {
      return false;
   }

   default void buildUniqueId(UniqueIdBuilder builder, T value) {
      builder.append(value.toString());
   }

   default String toString(OpsContainer ops, T value) {
      return value.toString();
   }

   default ListRecipeComponent<T> asList() {
      return ListRecipeComponent.create(this, false, false);
   }

   default ListRecipeComponent<T> asListOrSelf() {
      return ListRecipeComponent.create(this, true, false);
   }

   default ListRecipeComponent<T> asConditionalList() {
      return ListRecipeComponent.create(this, false, true);
   }

   default ListRecipeComponent<T> asConditionalListOrSelf() {
      return ListRecipeComponent.create(this, true, true);
   }

   default RecipeComponent<T> orSelf() {
      return this;
   }

   default <K> RecipeComponent<TinyMap<K, T>> asMap(RecipeComponent<K> key) {
      return MapRecipeComponent.of(key, this, IntBounds.DEFAULT);
   }

   default RecipeComponent<TinyMap<Character, T>> asPatternKey() {
      return MapRecipeComponent.patternOf(this, IntBounds.DEFAULT);
   }

   default <O> EitherRecipeComponent<T, O> or(RecipeComponent<O> other) {
      return new EitherRecipeComponent<>(this, other);
   }

   default RecipeComponent<T> withCodec(Codec<T> codec) {
      return new RecipeComponentWithCodec<>(this, codec);
   }

   @Nullable
   @Experimental
   default RecipeComponentBuilder createBuilder() {
      return null;
   }

   default List<?> spread(T value) {
      return List.of(value);
   }

   default boolean isIgnored() {
      return false;
   }
}
