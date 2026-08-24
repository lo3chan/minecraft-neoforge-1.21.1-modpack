package dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.event.AddBiomeInjectorsEvent;
import dev.worldgen.lithostitched.api.event.AddRegionsEvent;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.tag.LithostitchedBiomeSourceTags;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import dev.worldgen.lithostitched.mixin.common.BiomeSourceInvoker;
import dev.worldgen.lithostitched.mixin.common.ChunkGeneratorAccessor;
import dev.worldgen.lithostitched.mixin.common.RandomStateAccessor;
import dev.worldgen.lithostitched.platform.LithostitchedPlatform;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;

public class BiomeInjectorManager {
   public static void applyBiomeInjectors(RegistryAccess registries, Registry<LevelStem> dimensions, long seed) {
      for (Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
         ResourceKey<LevelStem> dimension = entry.getKey();
         Map<ResourceLocation, BiomeInjector> injectors = new HashMap<>();
         registries.lookupOrThrow(LithostitchedRegistries.BIOME_INJECTOR).listElements().forEach(holder -> {
            if (((BiomeInjector)holder.value()).dimension().equals(dimension)) {
               injectors.put(holder.key().location(), (BiomeInjector)holder.value());
            }
         });
         AddBiomeInjectorsEvent.EVENT.invoker().addInjectors(registries, (id, injectorx) -> {
            if (!injectors.containsKey(id) && injectorx.dimension().equals(dimension)) {
               injectors.put(id, injectorx);
            }
         });
         if (!injectors.isEmpty()) {
            ChunkGenerator generator = entry.getValue().generator();
            if (generator instanceof NoiseBasedChunkGenerator noiseGenerator) {
               RandomState randomState = RandomState.create(
                  (NoiseGeneratorSettings)noiseGenerator.generatorSettings().value(), registries.lookupOrThrow(Registries.NOISE), seed
               );
               DensityFunctionWrapper noiseHelper = new DensityFunctionWrapper(
                  seed,
                  ((NoiseGeneratorSettings)noiseGenerator.generatorSettings().value()).useLegacyRandomSource(),
                  randomState,
                  ((RandomStateAccessor)randomState).getRandom()
               );
               Map<ResourceKey<Region>, Region> regions = new HashMap<>();
               registries.lookupOrThrow(LithostitchedRegistries.REGION)
                  .listElements()
                  .filter(holder -> ((Region)holder.value()).dimension().equals(dimension))
                  .forEach(reference -> regions.put(reference.key(), (Region)reference.value()));
               AddRegionsEvent.EVENT.invoker().addRegions(registries, (key, level, biomes, weight) -> {
                  Region region = Region.create(key, level, biomes, weight);
                  if (!injectors.containsKey(key) && region.dimension().equals(dimension)) {
                     regions.put(key, region);
                  }
               });
               ResourceKey<DensityFunction> regionKey = ResourceKey.create(Registries.DENSITY_FUNCTION, createRegionId(dimension));
               Optional<DensityFunction> regionFunction = Lithostitched.registry(registries, Registries.DENSITY_FUNCTION).getOptional(regionKey);
               ChunkGeneratorAccessor accessor = (ChunkGeneratorAccessor)generator;
               BiomeSource currentSource = accessor.getBiomeSource();
               boolean canInject = true;

               for (Holder<MapCodec<? extends BiomeSource>> codec : BuiltInRegistries.BIOME_SOURCE
                  .getTagOrEmpty(LithostitchedBiomeSourceTags.CANNOT_INJECT_INTO)) {
                  if (((MapCodec)codec.value()).equals(((BiomeSourceInvoker)currentSource).getCodec())) {
                     canInject = false;
                     break;
                  }
               }

               if (canInject) {
                  InjectorBiomeSource injectorSource = currentSource instanceof InjectorBiomeSource injector
                     ? injector
                     : new InjectorBiomeSource(accessor.getBiomeSource());
                  injectorSource.applyInjectors(injectors, regionFunction, regions, noiseHelper);
                  accessor.setBiomeSource(injectorSource);
                  accessor.setFeaturesPerStep(
                     LithostitchedPlatform.memoize(
                        () -> FeatureSorter.buildFeaturesPerStep(
                           List.copyOf(injectorSource.possibleBiomes()), biome -> accessor.getGetter().apply(biome).features(), true
                        )
                     )
                  );
                  Lithostitched.debug("Applying {} biome injections for dimension {}", injectors.size(), dimension.location());
               }
            }
         }
      }
   }

   private static ResourceLocation createRegionId(ResourceKey<LevelStem> dimension) {
      return Lithostitched.vanillaToLithostitched(dimension.location()).withPrefix("region/");
   }
}
