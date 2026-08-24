package dev.latvian.mods.kubejs.util.registrypredicate;

import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;

public record RegistryNamespacePredicate<T>(String namespace) implements RegistryPredicate<T> {
   public boolean test(Holder<T> holder) {
      return holder instanceof Reference<T> ref
         ? ref.key().location().getNamespace().equals(this.namespace)
         : holder.getKey().location().getNamespace().equals(this.namespace);
   }

   @Override
   public String toString() {
      return "@" + this.namespace;
   }
}
