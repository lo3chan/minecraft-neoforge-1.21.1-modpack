package dev.worldgen.lithostitched.api.worldgen.surface;

import dev.worldgen.lithostitched.impl.worldgen.surface.condition.AllOfCondition;
import dev.worldgen.lithostitched.impl.worldgen.surface.condition.AnyOfCondition;
import dev.worldgen.lithostitched.impl.worldgen.surface.condition.BiomeCondition;
import dev.worldgen.lithostitched.impl.worldgen.surface.condition.SampleDensityCondition;
import dev.worldgen.lithostitched.impl.worldgen.surface.condition.SlopeCondition;
import java.util.Arrays;
import net.minecraft.core.HolderSet;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;

public interface LithostitchedSurfaceConditions {
   static ConditionSource allOf(ConditionSource... conditions) {
      return new AllOfCondition(Arrays.asList(conditions));
   }

   static ConditionSource anyOf(ConditionSource... conditions) {
      return new AnyOfCondition(Arrays.asList(conditions));
   }

   static ConditionSource biome(HolderSet<Biome> biomes) {
      return new BiomeCondition(biomes);
   }

   static ConditionSource slope(InclusiveRange<Integer> threshold) {
      return new SlopeCondition(threshold);
   }

   static ConditionSource sampleDensity(DensityFunction densityFunction, InclusiveRange<Double> range) {
      return new SampleDensityCondition(densityFunction, range);
   }
}
