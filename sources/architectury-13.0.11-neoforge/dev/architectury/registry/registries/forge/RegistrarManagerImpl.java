package dev.architectury.registry.registries.forge;

import com.google.common.base.Objects;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.architectury.impl.RegistrySupplierImpl;
import dev.architectury.platform.hooks.EventBusesHooks;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarBuilder;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.registry.registries.options.DefaultIdRegistrarOption;
import dev.architectury.registry.registries.options.RegistrarOption;
import dev.architectury.registry.registries.options.StandardRegistrarOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class RegistrarManagerImpl {
   private static final Logger LOGGER = LogManager.getLogger(RegistrarManagerImpl.class);
   private static final Multimap<RegistrarManagerImpl.RegistryEntryId<?>, Consumer<?>> LISTENERS = HashMultimap.create();

   private static void listen(ResourceKey<?> resourceKey, ResourceLocation id, Consumer<?> listener) {
      LISTENERS.put(new RegistrarManagerImpl.RegistryEntryId(resourceKey, id), listener);
   }

   public static RegistrarManager.RegistryProvider _get(String modId) {
      return new RegistrarManagerImpl.RegistryProviderImpl(modId);
   }

   public static class Data<T> {
      private boolean registered = false;
      private final Map<ResourceLocation, Supplier<? extends T>> objects = new LinkedHashMap<>();

      public void register(Registry<T> registry, ResourceLocation location, Mutable<T> object, Supplier<? extends T> reference) {
         if (!this.registered) {
            this.objects.put(location, () -> {
               T valuex = (T)reference.get();
               object.setValue(valuex);
               return valuex;
            });
         } else {
            ResourceKey<? extends Registry<T>> resourceKey = registry.key();
            T value = (T)reference.get();
            Registry.register(registry, location, value);
            object.setValue(value);
            RegistrarManagerImpl.RegistryEntryId<?> registryEntryId = new RegistrarManagerImpl.RegistryEntryId((ResourceKey<T>)resourceKey, location);

            for (Consumer<?> consumer : RegistrarManagerImpl.LISTENERS.get(registryEntryId)) {
               ((Consumer<T>)consumer).accept(value);
            }

            RegistrarManagerImpl.LISTENERS.removeAll(registryEntryId);
         }
      }
   }

   public static class RegistrarImpl<T> implements Registrar<T> {
      private final String modId;
      private final Registry<T> delegate;
      private final Map<ResourceKey<? extends Registry<?>>, RegistrarManagerImpl.Data<?>> registry;

      public RegistrarImpl(String modId, Map<ResourceKey<? extends Registry<?>>, RegistrarManagerImpl.Data<?>> registry, Registry<T> delegate) {
         this.modId = modId;
         this.registry = registry;
         this.delegate = delegate;
      }

      @Override
      public RegistrySupplier<T> delegate(ResourceLocation id) {
         Supplier<T> value = Suppliers.memoize(() -> this.get(id));
         return this.asSupplier(id, this, () -> this.contains(id), value);
      }

      @Override
      public <E extends T> RegistrySupplier<E> register(ResourceLocation id, Supplier<E> supplier) {
         RegistrarManagerImpl.Data<T> data = (RegistrarManagerImpl.Data<T>)this.registry.computeIfAbsent(this.key(), type -> new RegistrarManagerImpl.Data());
         Mutable<T> object = new MutableObject();
         data.register(this.delegate, id, object, supplier);
         return this.asSupplier(id, this, () -> object.getValue() != null, object::getValue);
      }

      private <E extends T> RegistrySupplier<E> asSupplier(ResourceLocation id, Registrar<E> registrar, BooleanSupplier isPresent, Supplier<T> object) {
         return new RegistrySupplierImpl<E>() {
            @Nullable
            Holder<E> holder = null;

            @Nullable
            @Override
            public Holder<E> getHolder() {
               return this.holder != null ? this.holder : (this.holder = registrar.getHolder(this.getId()));
            }

            @Override
            public ResourceKey<E> getKey() {
               return RegistrySupplierImpl.super.getKey();
            }

            @Override
            public RegistrarManager getRegistrarManager() {
               return RegistrarManager.get(RegistrarImpl.this.modId);
            }

            @Override
            public Registrar<E> getRegistrar() {
               return registrar;
            }

            @Override
            public ResourceLocation getRegistryId() {
               return RegistrarImpl.this.delegate.key().location();
            }

            @Override
            public ResourceLocation getId() {
               return id;
            }

            @Override
            public boolean isPresent() {
               return isPresent.getAsBoolean();
            }

            @Override
            public E get() {
               E value = (E)object.get();
               if (value == null) {
                  throw new NullPointerException("Value missing: " + this.getId() + "@" + this.getRegistryId());
               } else {
                  return value;
               }
            }

            @Override
            public int hashCode() {
               return Objects.hashCode(new Object[]{this.getRegistryId(), this.getId()});
            }

            @Override
            public boolean equals(Object obj) {
               if (this == obj) {
                  return true;
               } else {
                  return !(obj instanceof RegistrySupplier<?> other)
                     ? false
                     : other.getRegistryId().equals(this.getRegistryId()) && other.getId().equals(this.getId());
               }
            }

            @Override
            public String toString() {
               return this.getRegistryId() + "@" + id.toString();
            }
         };
      }

      @Nullable
      @Override
      public ResourceLocation getId(T obj) {
         return this.delegate.getKey(obj);
      }

      @Override
      public int getRawId(T obj) {
         return this.delegate.getId(obj);
      }

      @Override
      public Optional<ResourceKey<T>> getKey(T t) {
         return this.delegate.getResourceKey(t);
      }

      @Nullable
      @Override
      public T get(ResourceLocation id) {
         return (T)this.delegate.get(id);
      }

      @Override
      public T byRawId(int rawId) {
         return (T)this.delegate.byId(rawId);
      }

      @Override
      public boolean contains(ResourceLocation resourceLocation) {
         return this.delegate.keySet().contains(resourceLocation);
      }

      @Override
      public boolean containsValue(T t) {
         return this.delegate.getResourceKey(t).isPresent();
      }

      @Override
      public Set<ResourceLocation> getIds() {
         return this.delegate.keySet();
      }

      @Override
      public Set<Entry<ResourceKey<T>, T>> entrySet() {
         return this.delegate.entrySet();
      }

      @Override
      public ResourceKey<? extends Registry<T>> key() {
         return this.delegate.key();
      }

      @Nullable
      @Override
      public Holder<T> getHolder(ResourceKey<T> key) {
         return (Holder<T>)this.delegate.getHolder(key).orElse(null);
      }

      @Override
      public Iterator<T> iterator() {
         return this.delegate.iterator();
      }

      @Override
      public void listen(ResourceLocation id, Consumer<T> callback) {
         if (this.contains(id)) {
            callback.accept(this.get(id));
         } else {
            RegistrarManagerImpl.listen(this.key(), id, callback);
         }
      }
   }

   public static class RegistryBuilderWrapper<T> implements RegistrarBuilder<T> {
      private final RegistrarManagerImpl.RegistryProviderImpl provider;
      private final RegistryBuilder<T> builder;
      private boolean syncToClients = false;

      public RegistryBuilderWrapper(RegistrarManagerImpl.RegistryProviderImpl provider, RegistryBuilder<T> builder) {
         this.provider = provider;
         this.builder = builder;
      }

      @Override
      public Registrar<T> build() {
         this.builder.sync(this.syncToClients);
         if (this.provider.newRegistries == null) {
            throw new IllegalStateException("Cannot create registries when registries are already aggregated!");
         } else {
            Registry<T> registry = this.builder.create();
            Registrar<T> registrar = this.provider.get(registry);
            this.provider.newRegistries.add(registry);
            RegistrarManagerImpl.RegistryProviderImpl.CUSTOM_REGS.put((ResourceKey<Registry<?>>)registrar.key(), registrar);
            return registrar;
         }
      }

      @Override
      public RegistrarBuilder<T> option(RegistrarOption option) {
         if (option == StandardRegistrarOption.SYNC_TO_CLIENTS) {
            this.syncToClients = true;
         } else if (option instanceof DefaultIdRegistrarOption opt) {
            this.builder.defaultKey(opt.defaultId());
         }

         return this;
      }
   }

   public record RegistryEntryId<T>(ResourceKey<T> registryKey, ResourceLocation id) {
      @Override
      public String toString() {
         return "Registry Entry [%s / %s]".formatted(this.registryKey.location(), this.id);
      }
   }

   public static class RegistryProviderImpl implements RegistrarManager.RegistryProvider {
      private static final Map<ResourceKey<Registry<?>>, Registrar<?>> CUSTOM_REGS = new HashMap<>();
      private final String modId;
      private final Map<ResourceKey<? extends Registry<?>>, RegistrarManagerImpl.Data<?>> registry = new HashMap<>();
      private final Multimap<ResourceKey<Registry<?>>, Consumer<Registrar<?>>> listeners = HashMultimap.create();
      @Nullable
      private List<Registry<?>> newRegistries = new ArrayList<>();

      public RegistryProviderImpl(String modId) {
         this.modId = modId;
         EventBusesHooks.getModEventBus(modId).get().register(new RegistrarManagerImpl.RegistryProviderImpl.EventListener());
      }

      @Override
      public <T> Registrar<T> get(ResourceKey<Registry<T>> registryKey) {
         Registry<T> registry = (Registry<T>)BuiltInRegistries.REGISTRY.get(registryKey.location());
         if (registry != null) {
            return this.get(registry);
         } else {
            Registrar<?> customReg = CUSTOM_REGS.get(registryKey);
            if (customReg != null) {
               return (Registrar<T>)customReg;
            } else {
               throw new IllegalArgumentException("Registry " + registryKey + " does not exist!");
            }
         }
      }

      @Override
      public <T> Registrar<T> get(Registry<T> registry) {
         return new RegistrarManagerImpl.RegistrarImpl<>(this.modId, this.registry, registry);
      }

      @Override
      public <T> void forRegistry(ResourceKey<Registry<T>> key, Consumer<Registrar<T>> consumer) {
         this.listeners.put(key, consumer);
      }

      @Override
      public <T> RegistrarBuilder<T> builder(Class<T> type, ResourceLocation registryId) {
         return new RegistrarManagerImpl.RegistryBuilderWrapper<>(this, new RegistryBuilder(ResourceKey.createRegistryKey(registryId)));
      }

      public class EventListener {
         @SubscribeEvent
         public void handleEvent(RegisterEvent event) {
            for (Entry<ResourceKey<? extends Registry<?>>, RegistrarManagerImpl.Data<?>> typeDataEntry : RegistryProviderImpl.this.registry.entrySet()) {
               if (typeDataEntry.getKey().equals(event.getRegistryKey())) {
                  this.registerFor(event, typeDataEntry.getKey(), typeDataEntry.getValue());
               }
            }
         }

         public <T> void registerFor(RegisterEvent event, ResourceKey<? extends Registry<T>> resourceKey, RegistrarManagerImpl.Data<T> data) {
            event.register(resourceKey, registry -> {
               data.registered = true;

               for (Entry<ResourceLocation, Supplier<? extends T>> entry : data.objects.entrySet()) {
                  ResourceLocation location = entry.getKey();
                  T value = (T)entry.getValue().get();
                  registry.register(location, value);
                  RegistrarManagerImpl.RegistryEntryId<?> registryEntryId = new RegistrarManagerImpl.RegistryEntryId((ResourceKey<T>)resourceKey, location);

                  for (Consumer<?> consumer : RegistrarManagerImpl.LISTENERS.get(registryEntryId)) {
                     ((Consumer<T>)consumer).accept(value);
                  }

                  RegistrarManagerImpl.LISTENERS.removeAll(registryEntryId);
               }

               data.objects.clear();
               Registrar<?> archRegistry = RegistryProviderImpl.this.get(event.getRegistry());

               for (Entry<ResourceKey<Registry<?>>, Consumer<Registrar<?>>> entry : RegistryProviderImpl.this.listeners.entries()) {
                  if (entry.getKey().location().equals(resourceKey.location())) {
                     entry.getValue().accept(archRegistry);
                  }
               }
            });
         }

         @SubscribeEvent(
            priority = EventPriority.LOWEST
         )
         public void handleEventPost(RegisterEvent event) {
            Registrar<?> archRegistry = RegistryProviderImpl.this.get(event.getRegistry());
            List<RegistrarManagerImpl.RegistryEntryId<?>> toRemove = new ArrayList<>();

            for (Entry<RegistrarManagerImpl.RegistryEntryId<?>, Collection<Consumer<?>>> entry : RegistrarManagerImpl.LISTENERS.asMap().entrySet()) {
               if (entry.getKey().registryKey.equals(event.getRegistryKey())) {
                  if (archRegistry.contains(entry.getKey().id)) {
                     Object value = archRegistry.get(entry.getKey().id);

                     for (Consumer<?> consumer : entry.getValue()) {
                        ((Consumer<Object>)consumer).accept(value);
                     }

                     toRemove.add(entry.getKey());
                  } else {
                     RegistrarManagerImpl.LOGGER.warn("Registry entry listened {} was not realized!", entry.getKey());
                  }
               }
            }

            for (RegistrarManagerImpl.RegistryEntryId<?> id : toRemove) {
               RegistrarManagerImpl.LISTENERS.removeAll(id);
            }
         }

         @SubscribeEvent
         public void handleEvent(NewRegistryEvent event) {
            if (RegistryProviderImpl.this.newRegistries != null) {
               for (Registry<?> registry : RegistryProviderImpl.this.newRegistries) {
                  event.register(registry);
               }

               RegistryProviderImpl.this.newRegistries = null;
            }
         }
      }
   }
}
