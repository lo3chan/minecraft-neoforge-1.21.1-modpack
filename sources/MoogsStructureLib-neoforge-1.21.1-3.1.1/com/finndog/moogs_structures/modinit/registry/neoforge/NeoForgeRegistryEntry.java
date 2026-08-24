package com.finndog.moogs_structures.modinit.registry.neoforge;

import com.finndog.moogs_structures.modinit.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public class NeoForgeRegistryEntry<R, T extends R> implements RegistryEntry<T> {
   private final DeferredHolder<R, T> object;

   public NeoForgeRegistryEntry(DeferredHolder<R, T> object) {
      this.object = object;
   }

   @Override
   public T get() {
      return (T)this.object.get();
   }

   @Override
   public ResourceLocation getId() {
      return this.object.getId();
   }
}
