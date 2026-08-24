package dev.architectury.extensions.injected;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface InjectedRegistryEntryExtension<T> {
   Holder<T> arch$holder();

   @Nullable
   default ResourceLocation arch$registryName() {
      return this.arch$holder().unwrapKey().<ResourceLocation>map(ResourceKey::location).orElse(null);
   }
}
