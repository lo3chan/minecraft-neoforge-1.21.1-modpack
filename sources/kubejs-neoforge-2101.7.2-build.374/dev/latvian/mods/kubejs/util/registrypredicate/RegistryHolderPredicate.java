package dev.latvian.mods.kubejs.util.registrypredicate;

import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;

public record RegistryHolderPredicate<T>(Holder<T> value) implements RegistryPredicate<T> {
   public boolean test(Holder<T> holder) {
      return holder.value() == this.value.value();
   }

   @Override
   public String toString() {
      try {
         return this.value instanceof Reference<T> ref ? ref.key().location().toString() : this.value.getKey().location().toString();
      } catch (Exception var3) {
         return String.valueOf(this.value.value());
      }
   }
}
