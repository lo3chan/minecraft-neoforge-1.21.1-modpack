package dev.latvian.mods.kubejs.core;

import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface RecipeManagerKJS extends ReloadableServerResourceHolderKJS {
   default void kjs$replaceRecipes(Map<ResourceLocation, RecipeHolder<?>> byName) {
      throw new NoMixinException();
   }

   default Map<ResourceLocation, RecipeHolder<?>> kjs$getRecipeIdMap() {
      throw new NoMixinException();
   }
}
