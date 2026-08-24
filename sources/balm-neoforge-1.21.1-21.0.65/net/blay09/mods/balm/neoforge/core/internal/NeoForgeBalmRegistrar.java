package net.blay09.mods.balm.neoforge.core.internal;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import java.util.function.Function;
import net.blay09.mods.balm.core.AbstractCustomRegistryBuilder;
import net.blay09.mods.balm.core.AbstractDynamicRegistryBuilder;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.core.CustomRegistryBuilder;
import net.blay09.mods.balm.core.DynamicRegistryBuilder;
import net.blay09.mods.balm.neoforge.DeferredRegisters;
import net.blay09.mods.balm.neoforge.ModBusEventRegisters;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class NeoForgeBalmRegistrar implements BalmRegistrar {
   @Override
   public <T> Registry<T> createCustomRegistry(ResourceKey<? extends Registry<T>> registryKey, Consumer<CustomRegistryBuilder<T>> builderConsumer) {
      NeoForgeBalmRegistrar.NeoForgeCustomRegistryBuilder<T> builder = new NeoForgeBalmRegistrar.NeoForgeCustomRegistryBuilder<>(registryKey);
      builderConsumer.accept(builder);
      Registry<T> registry = builder.build();
      ModBusEventRegisters.getRegistrations(registryKey.location().getNamespace(), NeoForgeCustomRegistryRegistrar.class).add(registry);
      return registry;
   }

   @Override
   public <T> void createDynamicRegistry(ResourceKey<? extends Registry<T>> registryKey, Codec<T> codec, Consumer<DynamicRegistryBuilder<T>> builderConsumer) {
      var builder = new AbstractDynamicRegistryBuilder<T>() {};
      builderConsumer.accept(builder);
      ModBusEventRegisters.getRegistrations(registryKey.location().getNamespace(), NeoForgeDynamicRegistryRegistrar.class).add(registryKey, codec, builder);
   }

   @Override
   public <T> Holder<T> register(ResourceKey<T> resourceKey, Function<ResourceLocation, T> resourceFunction) {
      DeferredRegister<T> deferredRegister = DeferredRegisters.get(resourceKey.registryKey(), resourceKey.location().getNamespace());
      return deferredRegister.register(resourceKey.location().getPath(), () -> resourceFunction.apply(resourceKey.location()));
   }

   @Override
   public <T> void addAlias(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation oldId, ResourceLocation newId) {
      DeferredRegisters.get(registryKey, newId.getNamespace()).addAlias(oldId, newId);
   }

   @Override
   public <T> BalmRegistrar.Scoped<T> scoped(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
      return new NeoForgeBalmRegistrar.Scoped<>(registryKey, namespace);
   }

   private static class NeoForgeCustomRegistryBuilder<T> extends AbstractCustomRegistryBuilder<T> {
      private final ResourceKey<? extends Registry<T>> registryKey;

      public NeoForgeCustomRegistryBuilder(ResourceKey<? extends Registry<T>> registryKey) {
         this.registryKey = registryKey;
      }

      public Registry<T> build() {
         RegistryBuilder<T> builder = new RegistryBuilder(this.registryKey);
         ResourceLocation defaultKey = this.getDefaultKey();
         if (defaultKey != null) {
            builder.defaultKey(defaultKey);
         }

         if (this.shouldSync()) {
            builder.sync(true);
         }

         return builder.create();
      }
   }

   public static class Scoped<T> implements BalmRegistrar.Scoped<T> {
      private final ResourceKey<? extends Registry<T>> registryKey;
      private final String namespace;

      public Scoped(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
         this.registryKey = registryKey;
         this.namespace = namespace;
      }

      @Override
      public Holder<T> register(String name, Function<ResourceLocation, T> resourceFunction) {
         DeferredRegister<T> deferredRegister = DeferredRegisters.get(this.registryKey, this.namespace);
         return deferredRegister.register(name, resourceFunction);
      }

      @Override
      public void addAlias(String oldName, String newName) {
         this.addAlias(ResourceLocation.fromNamespaceAndPath(this.namespace, oldName), ResourceLocation.fromNamespaceAndPath(this.namespace, newName));
      }

      @Override
      public void addAlias(ResourceLocation oldId, ResourceLocation newId) {
         DeferredRegister<T> deferredRegister = DeferredRegisters.get(this.registryKey, this.namespace);
         deferredRegister.addAlias(oldId, newId);
      }
   }
}
