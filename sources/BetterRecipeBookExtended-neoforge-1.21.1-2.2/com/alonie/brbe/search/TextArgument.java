package com.alonie.brbe.search;

import java.util.Locale;
import net.minecraft.world.item.ItemStack;

public class TextArgument implements SearchArgument {
   private final String searchText;

   public TextArgument(String searchText) {
      this.searchText = searchText.toLowerCase(Locale.ROOT);
   }

   @Override
   public boolean matches(ItemStack stack, SearchCache cache) {
      return stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains(this.searchText);
   }

   @Override
   public boolean isAdvanced() {
      return false;
   }

   public String getSearchText() {
      return this.searchText;
   }
}
