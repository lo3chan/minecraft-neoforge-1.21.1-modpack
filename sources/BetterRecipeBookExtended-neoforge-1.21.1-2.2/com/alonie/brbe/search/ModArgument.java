package com.alonie.brbe.search;

import java.util.Locale;
import net.minecraft.world.item.ItemStack;

public class ModArgument implements SearchArgument {
   private final String modQuery;

   public ModArgument(String modQuery) {
      this.modQuery = modQuery.toLowerCase(Locale.ROOT);
   }

   @Override
   public boolean matches(ItemStack stack, SearchCache cache) {
      String namespace = cache.getModNamespace(stack);
      if (namespace.toLowerCase(Locale.ROOT).contains(this.modQuery)) {
         return true;
      } else {
         String modName = cache.getModName(stack);
         return modName.toLowerCase(Locale.ROOT).contains(this.modQuery);
      }
   }

   @Override
   public boolean isAdvanced() {
      return true;
   }
}
