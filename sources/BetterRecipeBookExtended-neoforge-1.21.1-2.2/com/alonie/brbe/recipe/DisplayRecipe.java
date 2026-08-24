package com.alonie.brbe.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface DisplayRecipe {
   ResourceLocation id();

   ItemStack getResult();

   String getSearchString();

   default boolean matches(ResourceLocation other) {
      return this.id().equals(other);
   }
}
