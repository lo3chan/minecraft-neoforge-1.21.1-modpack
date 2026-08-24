package com.alonie.brbe.search;

import java.util.regex.Pattern;
import net.minecraft.world.item.ItemStack;

public class RegexArgument implements SearchArgument {
   private final Pattern pattern;

   public RegexArgument(String regex) {
      this.pattern = Pattern.compile(regex, 2);
   }

   @Override
   public boolean matches(ItemStack stack, SearchCache cache) {
      return this.pattern.matcher(stack.getHoverName().getString()).find();
   }

   @Override
   public boolean isAdvanced() {
      return true;
   }
}
