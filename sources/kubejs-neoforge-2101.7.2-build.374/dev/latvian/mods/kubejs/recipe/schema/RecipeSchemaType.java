package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecipeSchemaType {
   public final RecipeNamespace namespace;
   public final ResourceLocation id;
   public final RecipeSchema schema;
   public final ResourceKey<RecipeSerializer<?>> serializerKey;
   public final String serializerType;
   public RecipeSchemaType parent;
   protected Optional<RecipeSerializer<?>> serializer;

   public RecipeSchemaType(RecipeNamespace namespace, ResourceLocation id, RecipeSchema schema) {
      this.namespace = namespace;
      this.id = id;
      this.schema = schema;
      this.serializerKey = ResourceKey.create(Registries.RECIPE_SERIALIZER, schema.typeOverride == null ? id : schema.typeOverride);
      this.serializerType = this.serializerKey.location().toString();
   }

   public RecipeSerializer<?> getSerializer() {
      if (this.serializer == null) {
         this.serializer = Optional.ofNullable((RecipeSerializer<?>)BuiltInRegistries.RECIPE_SERIALIZER.get(this.serializerKey));
      }

      RecipeSerializer<?> s = this.serializer.orElse(null);
      if (s == null) {
         throw new KubeRuntimeException("Serializer for type " + this.serializerKey.location() + " is not found!");
      } else {
         return s;
      }
   }

   @Override
   public String toString() {
      return this.id.toString();
   }
}
