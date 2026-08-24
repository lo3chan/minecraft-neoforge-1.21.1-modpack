package dev.worldgen.lithostitched.api.worldgen.biomeinjector;

import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.ParameterBuilderImpl;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.ParameterMap;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;

public interface ParameterBuilder {
   static ParameterBuilder create() {
      return new ParameterBuilderImpl();
   }

   ParameterBuilder densityFunctionExactly(Holder<DensityFunction> var1, double var2);

   ParameterBuilder densityFunctionMin(Holder<DensityFunction> var1, double var2);

   ParameterBuilder densityFunctionMax(Holder<DensityFunction> var1, double var2);

   ParameterBuilder densityFunctionRange(Holder<DensityFunction> var1, double var2, double var4);

   ParameterBuilder climateExactly(BiomeInjector.ClimateParameter var1, double var2);

   ParameterBuilder climateMin(BiomeInjector.ClimateParameter var1, double var2);

   ParameterBuilder climateMax(BiomeInjector.ClimateParameter var1, double var2);

   ParameterBuilder climateRange(BiomeInjector.ClimateParameter var1, double var2, double var4);

   ParameterBuilder region(ResourceKey<Region> var1);

   ParameterMap build();
}
