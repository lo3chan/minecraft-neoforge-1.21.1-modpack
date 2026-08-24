package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class RecipeFactoryRegistry {
   private final RecipeSchemaStorage storage;

   public RecipeFactoryRegistry(RecipeSchemaStorage storage) {
      this.storage = storage;
   }

   public void register(KubeRecipeFactory type) {
      this.storage.recipeTypes.put(type.id(), type);
   }

   public void register(ResourceLocation id, Class<?> typeClass, Supplier<? extends KubeRecipe> factory) {
      this.register(new KubeRecipeFactory(id, typeClass, factory));
   }
}
