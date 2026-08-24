package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.RegistryOpsContainer;

public interface KubeRecipeContext extends RecipeLikeContext {
   @Override
   default RegistryAccessContainer registries() {
      return this.recipe().type.event.registries;
   }

   @Override
   default RegistryOpsContainer ops() {
      return this.recipe().type.event.ops;
   }

   KubeRecipe recipe();
}
