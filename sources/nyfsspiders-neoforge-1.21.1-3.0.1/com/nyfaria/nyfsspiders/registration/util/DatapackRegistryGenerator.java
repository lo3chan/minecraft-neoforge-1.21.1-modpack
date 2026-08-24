package com.nyfaria.nyfsspiders.registration.util;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.RegistryDataLoader.RegistryData;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DatapackRegistryGenerator implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackOutput output;
   private final CompletableFuture<Provider> registries;
   private final Predicate<RegistryData<?>> predicate;

   public DatapackRegistryGenerator(PackOutput output, CompletableFuture<Provider> lookup, Predicate<RegistryData<?>> predicate) {
      this.registries = lookup;
      this.output = output;
      this.predicate = predicate;
   }

   public CompletableFuture<?> run(CachedOutput output) {
      return this.registries
         .thenCompose(
            lookup -> {
               DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, lookup);
               return CompletableFuture.allOf(
                  RegistryDataLoader.WORLDGEN_REGISTRIES
                     .stream()
                     .filter(this.predicate)
                     .flatMap(data -> this.dumpRegistryCap(output, lookup, ops, data).stream())
                     .toArray(CompletableFuture[]::new)
               );
            }
         );
   }

   private <T> Optional<CompletableFuture<?>> dumpRegistryCap(CachedOutput output, Provider lookup, DynamicOps<JsonElement> ops, RegistryData<T> data) {
      ResourceKey<? extends Registry<T>> registryKey = data.key();
      return lookup.lookup(registryKey)
         .map(
            registry -> {
               PathProvider pathProvider = this.output.createPathProvider(Target.DATA_PACK, registryKey.location().getPath());
               return CompletableFuture.allOf(
                  registry.listElements()
                     .map(value -> dumpValue(pathProvider.json(value.key().location()), output, ops, data.elementCodec(), value.value()))
                     .toArray(CompletableFuture[]::new)
               );
            }
         );
   }

   private static <E> CompletableFuture<?> dumpValue(Path path, CachedOutput output, DynamicOps<JsonElement> ops, Encoder<E> codec, E value) {
      Optional<JsonElement> encoded = codec.encodeStart(ops, value).resultOrPartial(error -> LOGGER.error("Couldn't serialize element {}: {}", path, error));
      return encoded.isPresent() ? DataProvider.saveStable(output, encoded.get(), path) : CompletableFuture.completedFuture(null);
   }

   public String getName() {
      return this.predicate + " registry";
   }
}
