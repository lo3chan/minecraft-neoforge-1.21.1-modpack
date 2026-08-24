package dev.worldgen.lithostitched.api.worldgen.util;

import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.mixin.common.RandomStateAccessor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.DensityFunction.NoiseHolder;
import net.minecraft.world.level.levelgen.DensityFunction.Visitor;
import net.minecraft.world.level.levelgen.DensityFunctions.EndIslandDensityFunction;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;

public class DensityFunctionWrapper implements Visitor {
   private final Map<DensityFunction, DensityFunction> wrapped = new ConcurrentHashMap<>();
   private final boolean useLegacySource;
   private final long seed;
   final RandomState randomState;
   final PositionalRandomFactory random;

   public DensityFunctionWrapper(PlacementCondition.Context context, NoiseGeneratorSettings settings) {
      this(context.seed(), settings.useLegacyRandomSource(), context.randomState(), ((RandomStateAccessor)context.randomState()).getRandom());
   }

   public DensityFunctionWrapper(long seed, boolean useLegacySource, RandomState randomState, PositionalRandomFactory random) {
      this.seed = seed;
      this.useLegacySource = useLegacySource;
      this.randomState = randomState;
      this.random = random;
   }

   public NoiseHolder visitNoise(NoiseHolder noiseHolder) {
      Holder<NoiseParameters> noiseData = noiseHolder.noiseData();
      if (this.useLegacySource) {
         if (noiseData.is(Noises.TEMPERATURE)) {
            NormalNoise noise = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(0L), new NoiseParameters(-7, 1.0, new double[]{1.0}));
            return new NoiseHolder(noiseData, noise);
         }

         if (noiseData.is(Noises.VEGETATION)) {
            NormalNoise noise = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(1L), new NoiseParameters(-7, 1.0, new double[]{1.0}));
            return new NoiseHolder(noiseData, noise);
         }

         if (noiseData.is(Noises.SHIFT)) {
            NormalNoise noise = NormalNoise.create(this.random.fromHashOf(Noises.SHIFT.location()), new NoiseParameters(0, 0.0, new double[0]));
            return new NoiseHolder(noiseData, noise);
         }
      }

      NormalNoise noise = this.randomState.getOrCreateNoise((ResourceKey)noiseData.unwrapKey().orElseThrow());
      return new NoiseHolder(noiseData, noise);
   }

   public DensityFunction apply(DensityFunction densityFunction) {
      return this.wrapped.computeIfAbsent(densityFunction, this::wrapNew);
   }

   private DensityFunction wrapNew(DensityFunction densityFunction) {
      if (densityFunction instanceof BlendedNoise noise) {
         RandomSource random = this.useLegacySource ? this.newLegacyInstance(0L) : this.random.fromHashOf(ResourceLocation.withDefaultNamespace("terrain"));
         return noise.withNewRandom(random);
      } else {
         return (DensityFunction)(densityFunction instanceof EndIslandDensityFunction ? new EndIslandDensityFunction(this.seed) : densityFunction);
      }
   }

   private RandomSource newLegacyInstance(long noiseSeed) {
      return new LegacyRandomSource(this.seed + noiseSeed);
   }
}
