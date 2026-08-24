package net.mehvahdjukaar.moonlight.api.misc;

import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface RegSupplier<T> extends Supplier<T>, Holder<T> {
   @Override
   T get();

   ResourceLocation getId();

   ResourceKey<T> getKey();

   @Deprecated(
      forRemoval = true
   )
   default Holder<T> getHolder() {
      return this;
   }

   default boolean is(T other) {
      return this.get() == other;
   }
}
