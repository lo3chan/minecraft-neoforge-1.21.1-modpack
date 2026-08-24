package dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;

public final class ParameterMap {
   public static final MapCodec<ParameterMap> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Codec.unboundedMap(Codec.either(BiomeInjector.ClimateParameter.CODEC, LithostitchedCodecs.DF_BASE), LithostitchedCodecs.DOUBLE_RANGE)
               .fieldOf("parameters")
               .forGetter(map -> map.parameters),
            Region.KEY_CODEC.optionalFieldOf("region").forGetter(map -> map.region)
         )
         .apply(i, ParameterMap::new)
   );
   private Map<Either<BiomeInjector.ClimateParameter, DensityFunction>, InclusiveRange<Double>> parameters;
   private Optional<ResourceKey<Region>> region;

   public ParameterMap(Map<Either<BiomeInjector.ClimateParameter, DensityFunction>, InclusiveRange<Double>> parameters, Optional<ResourceKey<Region>> region) {
      this.parameters = new HashMap<>(parameters);
      this.region = region;
   }

   public void mapAll(DensityFunctionWrapper noiseHelper) {
      Map<Either<BiomeInjector.ClimateParameter, DensityFunction>, InclusiveRange<Double>> mappedParameters = new HashMap<>();

      for (Entry<Either<BiomeInjector.ClimateParameter, DensityFunction>, InclusiveRange<Double>> entry : this.parameters.entrySet()) {
         Either<BiomeInjector.ClimateParameter, DensityFunction> either = entry.getKey();
         mappedParameters.put(either.mapRight(df -> df.mapAll(noiseHelper)), entry.getValue());
      }

      this.parameters.clear();
      this.parameters.putAll(mappedParameters);
   }

   public boolean matches(FunctionContext context, TargetPoint point, HashMap<DensityFunction, Double> densities, ResourceKey<Region> currentRegion) {
      if (!this.region.map(currentRegion::equals).orElse(true)) {
         return false;
      } else {
         for (Entry<Either<BiomeInjector.ClimateParameter, DensityFunction>, InclusiveRange<Double>> entry : this.parameters.entrySet()) {
            double density = (Double)entry.getKey()
               .map(reserved -> reserved.getter.apply(point).longValue() / 10000.0, df -> densities.computeIfAbsent(df, __ -> df.compute(context)));
            if (!entry.getValue().isValueInRange(density)) {
               return false;
            }
         }

         return true;
      }
   }
}
