package com.nyfaria.nyfsspiders.registration.neoforge;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import com.nyfaria.nyfsspiders.registration.registries.DatapackRegistry;
import com.nyfaria.nyfsspiders.registration.registries.DatapackRegistryBuilder;
import com.nyfaria.nyfsspiders.registration.registries.DatapackRegistryBuilder$$Factory;
import com.nyfaria.nyfsspiders.registration.util.DatapackRegistryGenerator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder.RegistryBootstrap;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
public class NeoForgeDatapackRegistryBuilder<T> implements DatapackRegistryBuilder<T> {
   private final ResourceKey<Registry<T>> key;
   private Codec<T> elementCodec;
   @Nullable
   private Codec<T> networkCodec;
   @Nullable
   private RegistryBootstrap<T> bootstrap;

   private NeoForgeDatapackRegistryBuilder(ResourceKey<Registry<T>> key) {
      this.key = Objects.requireNonNull(key, "registry key must not be null");
   }

   @Override
   public DatapackRegistryBuilder<T> withElementCodec(@NotNull Codec<T> codec) {
      this.elementCodec = Objects.requireNonNull(codec, "element codec must not be null");
      return this;
   }

   @Override
   public DatapackRegistryBuilder<T> withNetworkCodec(@Nullable Codec<T> codec) {
      this.networkCodec = codec;
      return this;
   }

   @Override
   public DatapackRegistryBuilder<T> withBootstrap(@Nullable RegistryBootstrap<T> bootstrap) {
      this.bootstrap = bootstrap;
      return this;
   }

   @Override
   public DatapackRegistry<T> build() {
      IEventBus bus = NeoForgeRegistrationFactory.getBus(this.key.location().getNamespace());
      bus.addListener(event -> event.dataPackRegistry(this.key, Objects.requireNonNull(this.elementCodec, "element codec must not be null"), this.networkCodec));
      return new DatapackRegistry<T>() {
         @Override
         public ResourceKey<Registry<T>> key() {
            return NeoForgeDatapackRegistryBuilder.this.key;
         }

         @Override
         public net.minecraft.data.DataProvider.Factory<DataProvider> bootstrapDataGenerator(CompletableFuture<Provider> lookupProvider) {
            return out -> new DatapackRegistryGenerator(out, lookupProvider, registryData -> registryData.key() == this.key());
         }

         @Override
         public void addToSet(RegistrySetBuilder builder) {
            builder.add(
               NeoForgeDatapackRegistryBuilder.this.key,
               NeoForgeDatapackRegistryBuilder.this.bootstrap == null ? ctx -> {} : NeoForgeDatapackRegistryBuilder.this.bootstrap
            );
         }

         @Override
         public Registry<T> get(RegistryAccess registryAccess) {
            return registryAccess.registryOrThrow(NeoForgeDatapackRegistryBuilder.this.key);
         }
      };
   }

   @AutoService({DatapackRegistryBuilder$$Factory.class})
   public static final class Factory implements DatapackRegistryBuilder$$Factory {
      @Override
      public <T> DatapackRegistryBuilder<T> newBuilder(ResourceKey<Registry<T>> key) {
         return new NeoForgeDatapackRegistryBuilder<>(key);
      }
   }
}
