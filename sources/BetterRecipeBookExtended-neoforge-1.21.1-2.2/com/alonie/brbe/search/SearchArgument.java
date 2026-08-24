package com.alonie.brbe.search;

import net.minecraft.world.item.ItemStack;

public interface SearchArgument {
   boolean matches(ItemStack var1, SearchCache var2);

   default boolean isAdvanced() {
      return false;
   }
}
