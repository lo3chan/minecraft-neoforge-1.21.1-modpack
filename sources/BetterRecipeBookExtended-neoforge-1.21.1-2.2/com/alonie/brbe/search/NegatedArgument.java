package com.alonie.brbe.search;

import net.minecraft.world.item.ItemStack;

public class NegatedArgument implements SearchArgument {
   private final SearchArgument child;

   public NegatedArgument(SearchArgument child) {
      this.child = child;
   }

   @Override
   public boolean matches(ItemStack stack, SearchCache cache) {
      return !this.child.matches(stack, cache);
   }

   @Override
   public boolean isAdvanced() {
      return true;
   }
}
