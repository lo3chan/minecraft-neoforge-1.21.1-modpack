package com.nyfaria.nyfsspiders.registration.neoforge;

import com.google.auto.service.AutoService;
import com.google.common.base.Suppliers;
import com.nyfaria.nyfsspiders.registration.RegistrationProvider;
import com.nyfaria.nyfsspiders.registration.RegistryObject;
import com.nyfaria.nyfsspiders.registration.registries.RegistryBuilder;
import com.nyfaria.nyfsspiders.registration.registries.RegistryFeatureType;
import com.nyfaria.nyfsspiders.registration.specialised.BlockRegistrationProvider;
import com.nyfaria.nyfsspiders.registration.specialised.BlockRegistryObject;
import com.nyfaria.nyfsspiders.registration.specialised.ItemRegistrationProvider;
import com.nyfaria.nyfsspiders.registration.specialised.ItemRegistryObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.ApiStatus.Internal;

@AutoService({RegistrationProvider.Factory.class})
public class NeoForgeRegistrationFactory implements RegistrationProvider.Factory {
   @Override
   public <T> RegistrationProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey, String modId) {
      DeferredRegister<T> register = DeferredRegister.create(resourceKey, modId);
      NeoForgeRegistrationFactory.Provider<T> provider = new NeoForgeRegistrationFactory.Provider<>(modId, register);
      IEventBus bus = getBus(modId);
      register.register(bus);
      bus.addListener(provider::onNewRegistry);
      return provider;
   }

   @Nonnull
   @Internal
   static IEventBus getBus(String modId) {
      if (modId.equals("minecraft")) {
         modId = "forge";
      }

      Optional<? extends ModContainer> containerOpt = ModList.get().getModContainerById(modId);
      if (containerOpt.isEmpty()) {
         throw new NullPointerException("Cannot find mod container for id " + modId);
      } else {
         IEventBus modBus = NeoForgeBusGetter.getBus(containerOpt.get());
         if (modBus == null) {
            throw new NullPointerException("Cannot get the mod event bus for the mod container with the mod id of " + modId);
         } else {
            return modBus;
         }
      }
   }

   @Override
   public ItemRegistrationProvider item(String modId) {
      return new NeoForgeRegistrationFactory.ItemProvider(modId);
   }

   @Override
   public BlockRegistrationProvider block(String modId) {
      return new NeoForgeRegistrationFactory.BlockProvider(modId);
   }

   private static class BlockProvider extends NeoForgeRegistrationFactory.Provider<Block> implements BlockRegistrationProvider {
      private BlockProvider(String modId) {
         super(modId, DeferredRegister.create(BuiltInRegistries.BLOCK, modId));
      }

      @Override
      public <B extends Block> BlockRegistryObject<B> register(String name, Supplier<? extends B> supplier) {
         NeoForgeRegistrationFactory.BlockProvider.BlockRO<B> obj = new NeoForgeRegistrationFactory.BlockProvider.BlockRO<>(
            this.registry.register(name, supplier)
         );
         this.entries.add(obj);
         return obj;
      }

      private class BlockRO<B extends Block> extends NeoForgeRegistrationFactory.Provider<Block>.RO<B> implements BlockRegistryObject<B> {
         protected BlockRO(DeferredHolder<Block, B> holder) {
            super(holder);
         }
      }
   }

   private static class ItemProvider extends NeoForgeRegistrationFactory.Provider<Item> implements ItemRegistrationProvider {
      private ItemProvider(String modId) {
         super(modId, DeferredRegister.create(BuiltInRegistries.ITEM, modId));
      }

      @Override
      public <I extends Item> ItemRegistryObject<I> register(String name, Supplier<? extends I> supplier) {
         NeoForgeRegistrationFactory.ItemProvider.ItemRO<I> obj = new NeoForgeRegistrationFactory.ItemProvider.ItemRO<>(this.registry.register(name, supplier));
         this.entries.add(obj);
         return obj;
      }

      private class ItemRO<I extends Item> extends NeoForgeRegistrationFactory.Provider<Item>.RO<I> implements ItemRegistryObject<I> {
         protected ItemRO(DeferredHolder<Item, I> holder) {
            super(holder);
         }
      }
   }

   private static class Provider<T> implements RegistrationProvider<T> {
      protected final String modId;
      protected final DeferredRegister<T> registry;
      private Registry<T> customRegistry;
      protected final Set<RegistryObject<T, ? extends T>> entries = new HashSet<>();
      private final Set<RegistryObject<T, ? extends T>> entriesView = Collections.unmodifiableSet(this.entries);
      private final Supplier<Registry<T>> registryInstance = Suppliers.memoize(() -> get(BuiltInRegistries.REGISTRY, this.getRegistryKey()));

      private Provider(String modId, DeferredRegister<T> registry) {
         this.modId = modId;
         this.registry = registry;
      }

      private void onNewRegistry(NewRegistryEvent event) {
         if (this.customRegistry != null) {
            event.register(this.customRegistry);
         }
      }

      @Override
      public String getModId() {
         return this.modId;
      }

      @Override
      public ResourceKey<? extends Registry<T>> getRegistryKey() {
         return this.registry.getRegistryKey();
      }

      @Override
      public Registry<T> getRegistry() {
         return this.registryInstance.get();
      }

      private static <T> T get(Registry<T> registry, ResourceKey<?> key) {
         return (T)registry.get(key);
      }

      @Override
      public <I extends T> RegistryObject<T, I> register(String name, Supplier<? extends I> supplier) {
         DeferredHolder<T, I> obj = this.registry.register(name, supplier);
         NeoForgeRegistrationFactory.Provider<T>.RO<I> ro = new NeoForgeRegistrationFactory.Provider.RO<>(obj);
         this.entries.add(ro);
         return ro;
      }

      public Set<RegistryObject<T, ? extends T>> getEntries() {
         return this.entriesView;
      }

      @Override
      public RegistryBuilder<T> registryBuilder() {
         return new NeoForgeRegistrationFactory.Provider.Builder();
      }

      private final class Builder implements RegistryBuilder<T> {
         private final net.neoforged.neoforge.registries.RegistryBuilder<T> builder = new net.neoforged.neoforge.registries.RegistryBuilder(
            Provider.this.getRegistryKey()
         );
         private final Map<RegistryFeatureType<?>, Object> features = new HashMap<>();

         @Override
         public <X> RegistryBuilder<T> withFeature(RegistryFeatureType<X> type, X value) {
            this.features.put(type, value);
            return this;
         }

         @Override
         public RegistryBuilder<T> withFeature(RegistryFeatureType<Void> type) {
            return this.withFeature(type, null);
         }

         @Override
         public RegistryBuilder<T> withDefaultValue(String id, Supplier<T> defaultValueSupplier) {
            Provider.this.register(id, defaultValueSupplier);
            return this.withFeature(RegistryFeatureType.DEFAULTED, ResourceLocation.fromNamespaceAndPath(Provider.this.modId, id));
         }

         @Override
         public Registry<T> build() {
            this.configureBuilder();
            Provider.this.customRegistry = this.builder.create();
            return Provider.this.customRegistry;
         }

         private void configureBuilder() {
            this.builder.sync(this.features.containsKey(RegistryFeatureType.SYNCED));
            if (this.features.containsKey(RegistryFeatureType.DEFAULTED)) {
               this.builder.defaultKey((ResourceLocation)this.features.get(RegistryFeatureType.DEFAULTED));
            }
         }
      }

      protected class RO<I extends T> implements RegistryObject<T, I> {
         private final DeferredHolder<T, I> holder;

         protected RO(DeferredHolder<T, I> holder) {
            this.holder = holder;
         }

         @Override
         public ResourceKey<T> getResourceKey() {
            return this.holder.getKey();
         }

         @Override
         public ResourceLocation getId() {
            return this.holder.getId();
         }

         @Override
         public I get() {
            return (I)this.holder.get();
         }

         @Override
         public Holder<T> asHolder() {
            return this.holder;
         }
      }
   }
}
