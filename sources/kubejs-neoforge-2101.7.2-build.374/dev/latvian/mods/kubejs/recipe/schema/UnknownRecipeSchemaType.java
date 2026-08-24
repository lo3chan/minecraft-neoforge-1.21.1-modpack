package dev.latvian.mods.kubejs.recipe.schema;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class UnknownRecipeSchemaType extends RecipeSchemaType {
   public UnknownRecipeSchemaType(RecipeNamespace namespace, ResourceLocation id, RecipeSerializer<?> serializer) {
      super(namespace, id, UnknownRecipeSchema.SCHEMA);
      this.serializer = Optional.ofNullable(serializer);
   }
}
