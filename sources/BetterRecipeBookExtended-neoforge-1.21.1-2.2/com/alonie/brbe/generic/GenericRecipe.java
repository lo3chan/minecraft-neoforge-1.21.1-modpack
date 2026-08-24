package com.alonie.brbe.generic;

import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.generic.pins.Pinnable;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface GenericRecipe extends Pinnable {
   ResourceLocation id();

   @Override
   default boolean has(ResourceLocation identifier) {
      return this.id().equals(identifier);
   }

   ItemStack getResult(RegistryAccess var1, BRBBookCategories.Category var2);

   String getSearchString(BRBBookCategories.Category var1);
}
