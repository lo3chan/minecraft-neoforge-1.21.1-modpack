package com.alonie.brbe.search;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public class CompoundArgument implements SearchArgument {
   private final List<SearchArgument> children;

   public CompoundArgument(List<SearchArgument> children) {
      this.children = children;
   }

   @Override
   public boolean matches(ItemStack stack, SearchCache cache) {
      for (SearchArgument child : this.children) {
         if (!child.matches(stack, cache)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean isAdvanced() {
      for (SearchArgument child : this.children) {
         if (child.isAdvanced()) {
            return true;
         }
      }

      return false;
   }
}
