package com.nyfaria.nyfsspiders.registration.registries;

import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataProvider.Factory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface DatapackRegistry<T> {
   static <T> DatapackRegistryBuilder<T> builder(ResourceKey<Registry<T>> key) {
      return DatapackRegistryBuilder$$Factory.INSTANCE.newBuilder(key);
   }

   static <T> DatapackRegistryBuilder<T> builder(ResourceLocation key) {
      return builder(ResourceKey.createRegistryKey(key));
   }

   ResourceKey<Registry<T>> key();

   Factory<DataProvider> bootstrapDataGenerator(CompletableFuture<Provider> var1);

   default Factory<DataProvider> bootstrapDataGenerator() {
      return this.bootstrapDataGenerator(CompletableFuture.supplyAsync(() -> {
         Frozen access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
         RegistrySetBuilder builder = new RegistrySetBuilder();
         this.addToSet(builder);
         return builder.build(access);
      }, Util.backgroundExecutor()));
   }

   void addToSet(RegistrySetBuilder var1);

   Registry<T> get(RegistryAccess var1);
}
