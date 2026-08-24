package dev.worldgen.lithostitched.worldgen.feature;

import dev.worldgen.lithostitched.worldgen.feature.config.WeightedSelectorConfig;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WeightedSelectorFeature extends Feature<WeightedSelectorConfig> {
   public static final WeightedSelectorFeature FEATURE = new WeightedSelectorFeature();

   public WeightedSelectorFeature() {
      super(WeightedSelectorConfig.CODEC);
   }

   public boolean place(FeaturePlaceContext<WeightedSelectorConfig> context) {
      WeightedSelectorConfig config = (WeightedSelectorConfig)context.config();
      WorldGenLevel level = context.level();
      ChunkGenerator generator = context.chunkGenerator();
      RandomSource random = context.random();
      BlockPos origin = context.origin();
      Optional<Holder<PlacedFeature>> feature = config.features().getRandom(random);
      return feature.<Boolean>map(placedFeatureHolder -> ((PlacedFeature)placedFeatureHolder.value()).place(level, generator, random, origin)).orElse(false);
   }
}
