package io.wispforest.owo.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@Deprecated(
   forRemoval = true
)
public final class RegistryAccess {
   private RegistryAccess() {
   }

   /** @deprecated */
   @Nullable
   public static <T> Holder<T> getEntry(Registry<T> registry, ResourceLocation id) {
      return (Holder<T>)registry.getHolder(id).orElse(null);
   }

   /** @deprecated */
   @Nullable
   public static <T> Holder<T> getEntry(Registry<T> registry, T value) {
      return registry.wrapAsHolder(value);
   }
}
