package com.alonie.brbe.search;

import java.util.Locale;
import net.minecraft.world.item.ItemStack;

public class TagArgument implements SearchArgument {
   private final String tagQuery;

   public TagArgument(String tagQuery) {
      this.tagQuery = tagQuery.toLowerCase(Locale.ROOT);
   }

   @Override
   public boolean matches(ItemStack stack, SearchCache cache) {
      for (String tag : cache.getTags(stack)) {
         if (tag.toLowerCase(Locale.ROOT).contains(this.tagQuery)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isAdvanced() {
      return true;
   }
}
