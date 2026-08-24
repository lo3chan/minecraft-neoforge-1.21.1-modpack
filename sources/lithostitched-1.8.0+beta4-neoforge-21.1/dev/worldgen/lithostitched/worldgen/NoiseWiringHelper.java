package dev.worldgen.lithostitched.worldgen;

import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

@Deprecated(
   forRemoval = true
)
public class NoiseWiringHelper extends DensityFunctionWrapper {
   public NoiseWiringHelper(PlacementCondition.Context context, NoiseGeneratorSettings settings) {
      super(context, settings);
   }

   public NoiseWiringHelper(long seed, boolean useLegacySource, RandomState randomState, PositionalRandomFactory random) {
      super(seed, useLegacySource, randomState, random);
   }
}
