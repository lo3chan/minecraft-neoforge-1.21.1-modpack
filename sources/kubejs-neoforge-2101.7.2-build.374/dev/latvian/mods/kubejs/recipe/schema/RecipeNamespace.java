package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import net.minecraft.resources.ResourceLocation;

public class RecipeNamespace extends LinkedHashMap<String, RecipeSchemaType> {
   public final RecipeSchemaStorage storage;
   public final String name;

   public RecipeNamespace(RecipeSchemaStorage storage, String name) {
      this.storage = storage;
      this.name = name;
   }

   public RecipeNamespace register(String id, RecipeSchema type) {
      this.put(id, new RecipeSchemaType(this, ResourceLocation.fromNamespaceAndPath(this.name, id), type));
      return this;
   }

   public RecipeNamespace register(String id, RegistryAwareSchema type) {
      return this.register(id, type.create(this.storage.getRegistries()));
   }

   public RecipeNamespace registerBasic(String id, RecipeKey<?>... keys) {
      return this.register(id, new RecipeSchema(keys));
   }

   public RecipeNamespace shaped(String id) {
      return this.withExistingParent(id, ResourceLocation.withDefaultNamespace("shaped"));
   }

   public RecipeNamespace shapeless(String id) {
      return this.withExistingParent(id, ResourceLocation.withDefaultNamespace("shapeless"));
   }

   public RecipeNamespace special(String id) {
      return this.withExistingParent(id, ResourceLocation.withDefaultNamespace("special"));
   }

   public RecipeNamespace withExistingParent(String id, ResourceLocation parent) {
      return this.register(id, this.storage.namespace(parent.getNamespace()).getRegisteredOrThrow(parent.getPath()).schema);
   }

   public RecipeSchemaType getRegisteredOrThrow(String id) {
      RecipeSchemaType value = this.get(id);
      if (value != null) {
         return value;
      } else {
         throw new NoSuchElementException("Required schema %s not found!".formatted(id));
      }
   }

   @Override
   public String toString() {
      return this.name;
   }
}
