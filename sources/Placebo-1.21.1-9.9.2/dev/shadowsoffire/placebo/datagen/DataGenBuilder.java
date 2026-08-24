package dev.shadowsoffire.placebo.datagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder.RegistryBootstrap;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.DataProvider.Factory;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenBuilder {
   protected final Set<String> registrySetModids;
   protected final RegistrySetBuilder registrySet = new RegistrySetBuilder();
   protected final List<DataGenBuilder.DataProviderFactory<?>> providers = new ArrayList<>();
   protected final Map<ResourceKey<?>, List<ICondition>> conditions = new IdentityHashMap<>();

   public static DataGenBuilder create(String... modids) {
      return new DataGenBuilder(modids);
   }

   protected DataGenBuilder(String... modids) {
      this.registrySetModids = Set.of(modids);
   }

   public <R> DataGenBuilder registry(ResourceKey<? extends Registry<R>> key, RegistryBootstrap<R> bootstrap) {
      this.registrySet.add(key, bootstrap);
      return this;
   }

   public DataGenBuilder conditions(ResourceKey<?> key, List<ICondition> conditions) {
      List<ICondition> existing = this.conditions.computeIfAbsent(key, k -> new ArrayList<>());
      existing.addAll(conditions);
      return this;
   }

   public DataGenBuilder conditions(ResourceKey<?> key, ICondition... conditions) {
      return this.conditions(key, Arrays.asList(conditions));
   }

   public DataGenBuilder conditions(Map<ResourceKey<?>, List<ICondition>> conditions) {
      for (Entry<ResourceKey<?>, List<ICondition>> entry : conditions.entrySet()) {
         this.conditions(entry.getKey(), entry.getValue());
      }

      return this;
   }

   public <T extends DataProvider> DataGenBuilder provider(DataGenBuilder.DataProviderFactory<T> factory) {
      this.providers.add(factory);
      return this;
   }

   public <T extends DataProvider> DataGenBuilder provider(BiFunction<PackOutput, CompletableFuture<Provider>, T> factory) {
      return this.provider((DataGenBuilder.DataProviderFactory<DataProvider>)((output, registries, fileHelper) -> factory.apply(output, registries)));
   }

   public <T extends DataProvider> DataGenBuilder provider(Factory<T> factory) {
      return this.provider((DataGenBuilder.DataProviderFactory<DataProvider>)((output, registries, fileHelper) -> factory.create(output)));
   }

   public void build(GatherDataEvent event) {
      this.registerDataProviders(event);
   }

   protected void registerDataProviders(GatherDataEvent event) {
      PackOutput output = event.getGenerator().getPackOutput();
      DatapackBuiltinEntriesProvider datapackProvider = new DatapackBuiltinEntriesProvider(
         output, event.getLookupProvider(), this.registrySet, this.conditions, this.registrySetModids
      );
      CompletableFuture<Provider> registries = datapackProvider.getRegistryProvider();
      DataGenerator generator = event.getGenerator();
      generator.addProvider(true, datapackProvider);

      for (DataGenBuilder.DataProviderFactory<?> factory : this.providers) {
         generator.addProvider(true, factory.create(output, registries, event.getExistingFileHelper()));
      }
   }

   @FunctionalInterface
   public interface DataProviderFactory<T extends DataProvider> {
      T create(PackOutput var1, CompletableFuture<Provider> var2, ExistingFileHelper var3);
   }
}
