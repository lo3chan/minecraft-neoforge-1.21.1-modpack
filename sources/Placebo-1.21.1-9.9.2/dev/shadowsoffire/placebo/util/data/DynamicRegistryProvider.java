package dev.shadowsoffire.placebo.util.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import dev.shadowsoffire.placebo.datagen.DataGenBuilder;
import dev.shadowsoffire.placebo.reload.DynamicRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.DataProvider.Factory;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public abstract class DynamicRegistryProvider<R extends CodecProvider<R>> implements DataProvider {
   protected final CompletableFuture<Provider> lookupProvider;
   protected final PathProvider pathProvider;
   protected final DynamicRegistry<R> registry;
   protected final List<CompletableFuture<?>> futures = new ArrayList<>();
   private CachedOutput cachedOutput;
   private DynamicRegistry.DataGenPopulator<R> populator;
   boolean skipGeneration = false;

   public DynamicRegistryProvider(PackOutput output, CompletableFuture<Provider> registries, DynamicRegistry<R> registry) {
      this.lookupProvider = registries;
      this.pathProvider = output.createPathProvider(Target.DATA_PACK, registry.getPath());
      this.registry = registry;
   }

   @Deprecated(
      forRemoval = true
   )
   public DynamicRegistryProvider(GatherDataEvent event, DynamicRegistry<R> registry) {
      this.lookupProvider = event.getLookupProvider();
      this.pathProvider = event.getGenerator().getPackOutput().createPathProvider(Target.DATA_PACK, registry.getPath());
      this.registry = registry;
   }

   public final CompletableFuture<?> run(CachedOutput pOutput) {
      this.cachedOutput = pOutput;
      DynamicRegistry.DataGenPopulator.runScoped(this.registry, populator -> {
         this.populator = populator;
         this.generate();
         this.populator = null;
      });
      return CompletableFuture.allOf(this.futures.toArray(CompletableFuture[]::new));
   }

   protected final void add(ResourceLocation id, R object) {
      this.populator.register(id, object);
      if (!this.skipGeneration) {
         this.futures
            .add(
               this.lookupProvider
                  .thenCompose(
                     regs -> {
                        DynamicOps<JsonElement> ops = regs.createSerializationContext(JsonOps.INSTANCE);
                        return DataProvider.saveStable(
                           this.cachedOutput, (JsonElement)this.registry.elementCodec().encodeStart(ops, object).getOrThrow(), this.pathProvider.json(id)
                        );
                     }
                  )
            );
      }
   }

   protected final void addConditionally(ResourceLocation id, R object, ICondition... conditions) {
      this.populator.register(id, object);
      Codec<Optional<WithConditions<R>>> conditionalCodec = ConditionalOps.createConditionalCodecWithConditions(this.registry.elementCodec());
      if (!this.skipGeneration) {
         this.futures
            .add(
               this.lookupProvider
                  .thenCompose(
                     regs -> {
                        DynamicOps<JsonElement> ops = regs.createSerializationContext(JsonOps.INSTANCE);
                        Optional<WithConditions<R>> withConds = Optional.of(new WithConditions(Arrays.asList(conditions), object));
                        return DataProvider.saveStable(
                           this.cachedOutput, (JsonElement)conditionalCodec.encodeStart(ops, withConds).getOrThrow(), this.pathProvider.json(id)
                        );
                     }
                  )
            );
      }
   }

   public abstract void generate();

   public static <R extends CodecProvider<R>, T extends DynamicRegistryProvider<R>> DataGenBuilder.DataProviderFactory<T> runSilently(
      DataGenBuilder.DataProviderFactory<T> factory
   ) {
      return (output, registries, fileHelper) -> {
         T provider = factory.create(output, registries, fileHelper);
         provider.skipGeneration = true;
         return provider;
      };
   }

   public static <R extends CodecProvider<R>, T extends DynamicRegistryProvider<R>> DataGenBuilder.DataProviderFactory<T> runSilently(
      BiFunction<PackOutput, CompletableFuture<Provider>, T> factory
   ) {
      return (output, registries, fileHelper) -> {
         T provider = factory.apply(output, registries);
         provider.skipGeneration = true;
         return provider;
      };
   }

   public static <R extends CodecProvider<R>, T extends DynamicRegistryProvider<R>> DataGenBuilder.DataProviderFactory<T> runSilently(Factory<T> factory) {
      return (output, registries, fileHelper) -> {
         T provider = (T)factory.create(output);
         provider.skipGeneration = true;
         return provider;
      };
   }
}
