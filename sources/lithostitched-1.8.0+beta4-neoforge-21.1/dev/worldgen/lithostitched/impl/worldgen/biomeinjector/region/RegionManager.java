package dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;

public class RegionManager {
   private static final ResourceKey<Region> NO_REGIONS = error("no_regions");
   private static final ResourceKey<Region> NO_REGIONS_IN_RANGE = error("no_regions_in_range");
   private final Optional<DensityFunction> regionFunction;
   private final Map<Holder<Biome>, RegionManager.BiomeRegions> regionsByBiome;

   public RegionManager(
      Optional<DensityFunction> regionFunction, Map<ResourceKey<Region>, Region> regions, DensityFunctionWrapper noiseHelper, Collection<Holder<Biome>> biomes
   ) {
      this.regionFunction = regionFunction.flatMap(df -> Optional.of(df.mapAll(noiseHelper)));
      this.regionsByBiome = new HashMap<>();

      for (Holder<Biome> biome : biomes) {
         TreeMap<Integer, ResourceKey<Region>> biomeMap = new TreeMap<>();
         int weight = 1;

         for (Entry<ResourceKey<Region>, Region> entry : regions.entrySet()
            .stream()
            .sorted(Comparator.comparing(entryx -> ((ResourceKey)entryx.getKey()).location()))
            .toList()) {
            Region region = entry.getValue();
            if (region.biomes().contains(biome)) {
               int regionWeight = region.weight();
               if (regionWeight > 0) {
                  biomeMap.put(weight, entry.getKey());
                  weight += regionWeight;
               }
            }
         }

         this.regionsByBiome.put(biome, new RegionManager.BiomeRegions(biomeMap, weight - 1));
      }
   }

   public ResourceKey<Region> getRegion(FunctionContext context, Holder<Biome> biome) {
      RegionManager.BiomeRegions biomeRegions = this.regionsByBiome.get(biome);
      return biomeRegions == null ? NO_REGIONS : biomeRegions.getRegion(this.regionFunction, context);
   }

   public int getRegionValue(FunctionContext context, Holder<Biome> biome) {
      RegionManager.BiomeRegions biomeRegions = this.regionsByBiome.get(biome);
      return biomeRegions != null && !this.regionFunction.isEmpty() ? biomeRegions.getRegionValue(this.regionFunction.get(), context) : -1;
   }

   private static ResourceKey<Region> error(String message) {
      return Lithostitched.key(LithostitchedRegistries.REGION, "error/" + message);
   }

   private record BiomeRegions(TreeMap<Integer, ResourceKey<Region>> regionsByOutputs, int totalWeight) {
      public int getRegionValue(DensityFunction regionFunction, FunctionContext context) {
         double density = regionFunction.compute(context);
         return (int)(Math.clamp(density, 0.0, 1.0) * this.totalWeight + 1.0);
      }

      public ResourceKey<Region> getRegion(Optional<DensityFunction> regionFunction, FunctionContext context) {
         if (!regionFunction.isEmpty() && !this.regionsByOutputs.isEmpty()) {
            int value = this.getRegionValue(regionFunction.get(), context);
            Entry<Integer, ResourceKey<Region>> entry = this.regionsByOutputs.floorEntry(value);
            return entry == null ? RegionManager.NO_REGIONS_IN_RANGE : entry.getValue();
         } else {
            return RegionManager.NO_REGIONS;
         }
      }
   }
}
