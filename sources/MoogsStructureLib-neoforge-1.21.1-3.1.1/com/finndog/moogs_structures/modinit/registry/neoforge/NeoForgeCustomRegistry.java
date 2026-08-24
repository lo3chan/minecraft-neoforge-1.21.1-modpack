package com.finndog.moogs_structures.modinit.registry.neoforge;

import com.finndog.moogs_structures.modinit.registry.CustomRegistryLookup;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NeoForgeCustomRegistry<T, K extends T> implements CustomRegistryLookup<T> {
   private final Registry<T> registry;

   public NeoForgeCustomRegistry(Registry<T> registry) {
      this.registry = registry;
   }

   @Override
   public boolean containsKey(ResourceLocation id) {
      return this.registry.containsKey(id);
   }

   @Nullable
   @Override
   public T get(ResourceLocation id) {
      return (T)this.registry.get(id);
   }

   @Override
   public Collection<T> getValues() {
      return this.registry.stream().collect(Collectors.toList());
   }

   @Override
   public Collection<ResourceLocation> getKeys() {
      return this.registry.keySet();
   }

   @Nullable
   @Override
   public ResourceLocation getKey(Object value) {
      return this.registry.getKey(value);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.registry.containsValue(value);
   }

   @NotNull
   @Override
   public Iterator iterator() {
      return this.registry.iterator();
   }
}
