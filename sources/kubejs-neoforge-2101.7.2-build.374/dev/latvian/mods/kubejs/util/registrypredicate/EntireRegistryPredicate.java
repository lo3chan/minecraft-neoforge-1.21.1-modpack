package dev.latvian.mods.kubejs.util.registrypredicate;

import net.minecraft.core.Holder;

public record EntireRegistryPredicate(boolean match) implements RegistryPredicate<Object> {
   public static final EntireRegistryPredicate TRUE = new EntireRegistryPredicate(true);
   public static final EntireRegistryPredicate FALSE = new EntireRegistryPredicate(false);

   public boolean test(Holder<Object> holder) {
      return this.match;
   }

   @Override
   public String toString() {
      return this.match ? "*" : "-";
   }
}
