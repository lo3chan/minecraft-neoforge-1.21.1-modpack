package com.alonie.brbe.search;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public class AlternativeArgument implements SearchArgument {
   private final List<SearchArgument> children;

   public AlternativeArgument(List<SearchArgument> children) {
      this.children = children;
   }

   @Override
   public boolean matches(ItemStack stack, SearchCache cache) {
      if (this.children.isEmpty()) {
         return true;
      } else {
         for (SearchArgument child : this.children) {
            if (child.matches(stack, cache)) {
               return true;
            }
         }

         return false;
      }
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
