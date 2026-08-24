package com.finndog.moogs_structures.modinit.registry.neoforge;

import com.finndog.moogs_structures.modinit.registry.CustomRegistryLookup;
import com.finndog.moogs_structures.modinit.registry.ResourcefulRegistry;
import com.finndog.moogs_structures.platform.IRegistryPlatform;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.commons.lang3.tuple.Pair;

public class ResourcefulRegistriesImpl implements IRegistryPlatform {
   private static final List<ResourcefulRegistriesImpl.CustomRegistryInfo<?, ?>> CUSTOM_REGISTRIES = new ArrayList<>();

   @Override
   public <T> ResourcefulRegistry<T> create(Registry<T> registry, String id) {
      return new NeoForgeResourcefulRegistry<>(registry, id);
   }

   @Override
   public <T, K extends Registry<T>> Pair<Supplier<CustomRegistryLookup<T>>, ResourcefulRegistry<T>> createCustomRegistryInternal(
      String modId, ResourceKey<K> key, boolean save, boolean sync, boolean allowModification
   ) {
      ResourcefulRegistriesImpl.CustomRegistryInfo<T, T> info = new ResourcefulRegistriesImpl.CustomRegistryInfo<>(
         new ResourcefulRegistriesImpl.LateSupplier<>(), key, save, sync, allowModification
      );
      CUSTOM_REGISTRIES.add(info);
      return Pair.of(info.lookup(), new NeoForgeResourcefulRegistry(key, modId));
   }

   public static void onRegisterForgeRegistries(NewRegistryEvent event) {
      CUSTOM_REGISTRIES.forEach(registry -> registry.build(event));
   }

   public record CustomRegistryInfo<T, K extends T>(
      ResourcefulRegistriesImpl.LateSupplier<CustomRegistryLookup<T>> lookup,
      ResourceKey<? extends Registry<T>> key,
      boolean save,
      boolean sync,
      boolean allowModification
   ) {
      public void build(NewRegistryEvent event) {
         this.lookup.set(new NeoForgeCustomRegistry<>(event.create(this.getBuilder())));
      }

      public RegistryBuilder<T> getBuilder() {
         return new RegistryBuilder(this.key).disableRegistrationCheck().sync(this.sync);
      }
   }

   public static class LateSupplier<T> implements Supplier<T> {
      private T value;
      private boolean initialized = false;

      public void set(T value) {
         this.value = value;
         this.initialized = true;
      }

      @Override
      public T get() {
         if (!this.initialized) {
            throw new IllegalStateException("LateSupplier not initialized");
         } else {
            return this.value;
         }
      }
   }
}
