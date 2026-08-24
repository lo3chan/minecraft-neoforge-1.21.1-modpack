package com.alonie.brbe.generic.pins;

import java.util.List;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class PinnableRecipeCollection extends RecipeCollection implements Pinnable {
   public PinnableRecipeCollection(RegistryAccess registryAccess, List<RecipeHolder<?>> list) {
      super(registryAccess, list);
   }

   public static PinnableRecipeCollection of(RecipeCollection collection) {
      return new PinnableRecipeCollection(collection.registryAccess(), collection.getRecipes());
   }

   @Override
   public boolean has(ResourceLocation resourceLocation) {
      for (RecipeHolder<?> recipe : this.getRecipes()) {
         if (recipe.id().equals(resourceLocation)) {
            return true;
         }
      }

      return false;
   }
}
