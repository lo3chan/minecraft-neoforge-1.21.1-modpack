package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeOptional;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaStorage;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedCollection;
import java.util.SequencedSet;

public final class RecipeKey<T> {
   public final RecipeComponent<T> component;
   public final TypeInfo typeInfo;
   public final Codec<T> codec;
   public final String name;
   public final ComponentRole role;
   public final SequencedSet<String> names;
   public RecipeOptional<T> optional;
   public boolean excluded;
   public List<String> functionNames;
   public boolean alwaysWrite;
   private List<String> validFunctionNames;

   public RecipeKey(RecipeComponent<T> component, String name, ComponentRole role) {
      this.component = component;
      this.typeInfo = component.typeInfo();
      this.codec = component.codec();
      this.name = name;
      this.role = role;
      this.names = new LinkedHashSet<String>(1);
      this.names.add(name);
      this.optional = null;
      this.excluded = false;
      this.functionNames = null;
      this.alwaysWrite = false;
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(this.name);
      sb.append(':');
      sb.append(' ');
      sb.append(this.component.toString());
      if (this.optional != null) {
         sb.append('?');
      }

      return sb.toString();
   }

   public RecipeKey<T> optional(T value) {
      return this.optional(RecipeOptional.unit(value));
   }

   public RecipeKey<T> optional(RecipeOptional<T> value) {
      this.optional = value;
      return this;
   }

   public RecipeKey<T> defaultOptional() {
      this.optional = Cast.to(RecipeOptional.DEFAULT);
      return this;
   }

   public boolean optional() {
      return this.optional != null;
   }

   public RecipeKey<T> alt(String name) {
      this.names.add(name);
      return this;
   }

   public RecipeKey<T> alt(String... names) {
      this.names.addAll(List.of(names));
      return this;
   }

   public RecipeKey<T> exclude() {
      this.excluded = true;
      return this;
   }

   public RecipeKey<T> noFunctions() {
      this.functionNames = List.of();
      return this;
   }

   public RecipeKey<T> functionNames(List<String> names) {
      this.functionNames = names;
      return this;
   }

   public RecipeKey<T> functionNames(String... names) {
      return this.functionNames(List.of(names));
   }

   public RecipeKey<T> alwaysWrite() {
      this.alwaysWrite = true;
      return this;
   }

   public List<String> getValidFunctionNames() {
      if (this.validFunctionNames == null) {
         SequencedCollection<String> list = (SequencedCollection<String>)(this.functionNames == null ? this.names : this.functionNames);
         this.validFunctionNames = new ArrayList<>(list.size());

         for (String n : list) {
            n = StringUtilsWrapper.snakeCaseToCamelCase(n.replace(':', '_').replace('/', '_'));
            if (RecipeFunction.isValidIdentifier(n.toCharArray())) {
               this.validFunctionNames.add(n);
            }
         }

         this.validFunctionNames = List.copyOf(this.validFunctionNames);
      }

      return this.validFunctionNames;
   }

   public String getPrimaryFunctionName() {
      List<String> names = this.getValidFunctionNames();
      return names.isEmpty() ? "" : (String)names.getFirst();
   }

   public JsonObject toJson(RecipeSchemaStorage storage, RecipeSchemaType type, DynamicOps<JsonElement> ops) {
      JsonObject json = new JsonObject();
      json.addProperty("name", this.name);
      if (!this.role.isOther()) {
         json.addProperty("role", this.role.getSerializedName());
      }

      json.add("type", (JsonElement)storage.recipeComponentCodec.encodeStart(ops, this.component).getOrThrow());
      if (this.optional != null) {
         if (this.optional.isDefault()) {
            json.addProperty("default_optional", true);
         } else {
            json.add("optional", (JsonElement)this.codec.encodeStart(ops, this.optional.getDefaultValue(type)).getOrThrow());
         }
      }

      if (this.names.size() > 1) {
         JsonArray a = new JsonArray();

         for (String n : this.names) {
            if (!n.equals(this.name)) {
               a.add(n);
            }
         }

         json.add("alternative_names", a);
      }

      if (this.functionNames != null) {
         JsonArray a = new JsonArray();
         this.functionNames.forEach(a::add);
         json.add("function_names", a);
      }

      if (this.excluded) {
         json.addProperty("excluded", true);
      }

      if (this.alwaysWrite) {
         json.addProperty("always_write", true);
      }

      return json;
   }
}
