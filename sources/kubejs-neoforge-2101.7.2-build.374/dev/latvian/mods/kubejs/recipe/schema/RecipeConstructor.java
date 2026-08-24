package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ErrorStack;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class RecipeConstructor {
   public final List<RecipeKey<?>> keys;
   public Map<RecipeKey<?>, RecipeOptional<?>> overrides;
   public Map<RecipeKey<?>, RecipeOptional<?>> defaultValues;

   public RecipeConstructor(List<RecipeKey<?>> keys) {
      this.keys = keys;
      this.overrides = Map.of();
      this.defaultValues = Map.of();
   }

   public RecipeConstructor(RecipeKey<?>... keys) {
      this(List.of(keys));
   }

   public <T> RecipeConstructor override(RecipeKey<T> key, RecipeOptional<T> value) {
      if (this.overrides.isEmpty()) {
         this.overrides = new Reference2ObjectLinkedOpenHashMap(1);
      }

      this.overrides.put(key, value);
      return this;
   }

   public <T> RecipeConstructor overrideValue(RecipeKey<T> key, T value) {
      return this.override(key, RecipeOptional.unit(value));
   }

   public <T> RecipeConstructor defaultValue(RecipeKey<T> key, RecipeOptional<T> value) {
      if (this.defaultValues.isEmpty()) {
         this.defaultValues = new Reference2ObjectLinkedOpenHashMap(1);
      }

      this.defaultValues.put(key, value);
      return this;
   }

   @Override
   public String toString() {
      return this.toString(RegistryAccessContainer.current);
   }

   public String toString(OpsContainer ops) {
      String str = this.keys.stream().map(RecipeKey::toString).collect(Collectors.joining(", ", "(", ")"));
      if (!this.overrides.isEmpty() || !this.defaultValues.isEmpty()) {
         LinkedHashMap<RecipeKey<?>, RecipeOptional<?>> map = new LinkedHashMap<>();
         map.putAll(this.defaultValues);
         map.putAll(this.overrides);
         str = str + map.entrySet().stream().map(e -> {
            RecipeKey<?> k = e.getKey();

            try {
               Object v = e.getValue().getInformativeValue();
               return v == null ? k.name + " = ?" : k.name + " = " + ((RecipeComponent<Object>)k.component).toString(ops, Cast.to(v));
            } catch (Throwable var4) {
               return k.name + " = ?";
            }
         }).collect(Collectors.joining(", ", " [", "]"));
      }

      return str;
   }

   public KubeRecipe create(Context cx, SourceLine sourceLine, RecipeTypeFunction type, RecipeSchemaType schemaType, ComponentValueMap from) {
      KubeRecipe recipe = schemaType.schema.recipeFactory.create(type, sourceLine, true);
      recipe.json = new JsonObject();
      recipe.json.addProperty("type", type.idString);
      recipe.newRecipe = true;
      this.setValues(new RecipeScriptContext.Impl(cx, recipe, new ErrorStack()), schemaType, from);
      return recipe;
   }

   public void setValues(RecipeScriptContext cx, RecipeSchemaType schemaType, ComponentValueMap from) {
      KubeRecipe recipe = cx.recipe();
      cx.errors().push("keys");

      for (RecipeKey<?> key : this.keys) {
         cx.errors().setKey(key.name);
         recipe.setValue(key, Cast.to(from.getValue(cx, key)));
      }

      cx.errors().pop();
      cx.errors().push("overrides");

      for (Entry<RecipeKey<?>, RecipeOptional<?>> entry : this.overrides.entrySet()) {
         cx.errors().setKey(entry.getKey().name);
         recipe.setValue(entry.getKey(), Cast.to(entry.getValue().getDefaultValue(schemaType)));
      }

      cx.errors().pop();
      cx.errors().push("key_overrides");

      for (Entry<RecipeKey<?>, RecipeOptional<?>> entry : schemaType.schema.keyOverrides.entrySet()) {
         cx.errors().setKey(entry.getKey().name);
         recipe.setValue(entry.getKey(), Cast.to(entry.getValue().getDefaultValue(schemaType)));
      }

      cx.errors().pop();
   }

   public JsonObject toJson(RecipeSchemaType type, DynamicOps<JsonElement> ops) {
      JsonObject json = new JsonObject();
      JsonArray k = new JsonArray(this.keys.size());

      for (RecipeKey<?> key : this.keys) {
         k.add(key.name);
      }

      json.add("keys", k);
      if (!this.overrides.isEmpty()) {
         JsonObject o = new JsonObject();

         for (Entry<RecipeKey<?>, RecipeOptional<?>> entry : this.overrides.entrySet()) {
            o.add(entry.getKey().name, (JsonElement)entry.getKey().codec.encodeStart(ops, Cast.to(entry.getValue().getDefaultValue(type))).getOrThrow());
         }

         json.add("overrides", o);
      }

      return json;
   }
}
