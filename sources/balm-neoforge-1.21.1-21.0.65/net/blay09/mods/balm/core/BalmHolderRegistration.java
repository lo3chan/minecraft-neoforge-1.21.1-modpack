package net.blay09.mods.balm.core;

import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

public interface BalmHolderRegistration<T> {
   default ResourceKey<T> asResourceKey() {
      return (ResourceKey<T>)this.asHolder().unwrapKey().orElseThrow();
   }

   default Supplier<T> asSupplier() {
      return this.asHolder()::value;
   }

   Holder<T> asHolder();
}
