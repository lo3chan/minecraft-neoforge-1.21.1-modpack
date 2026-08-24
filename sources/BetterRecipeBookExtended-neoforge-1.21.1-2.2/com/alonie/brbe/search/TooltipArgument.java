package com.alonie.brbe.search;

import java.util.Locale;
import net.minecraft.world.item.ItemStack;

public class TooltipArgument implements SearchArgument {
   private final String tooltipQuery;

   public TooltipArgument(String tooltipQuery) {
      this.tooltipQuery = tooltipQuery.toLowerCase(Locale.ROOT);
   }

   @Override
   public boolean matches(ItemStack stack, SearchCache cache) {
      String tooltip = cache.getTooltipText(stack);
      return tooltip != null && tooltip.toLowerCase(Locale.ROOT).contains(this.tooltipQuery);
   }

   @Override
   public boolean isAdvanced() {
      return true;
   }
}
