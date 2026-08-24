package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.SimpleContext;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public record SampleDensityPlacementCondition(Holder<DensityFunction> densityFunction, InclusiveRange<Double> range) implements PlacementCondition {
   public static final MapCodec<SampleDensityPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LithostitchedCodecs.DF_REFERENCE.fieldOf("density_function").forGetter(SampleDensityPlacementCondition::densityFunction),
            Codec.DOUBLE.optionalFieldOf("min_inclusive", 5.0E-324).forGetter(condition -> (Double)condition.range.minInclusive()),
            Codec.DOUBLE.optionalFieldOf("max_inclusive", 1.7976931348623157E308).forGetter(condition -> (Double)condition.range.maxInclusive())
         )
         .apply(instance, SampleDensityPlacementCondition::new)
   );

   public SampleDensityPlacementCondition(Holder<DensityFunction> densityFunction, double minInclusive, double maxInclusive) {
      this(densityFunction, new InclusiveRange(minInclusive, maxInclusive));
   }

   @Override
   public boolean test(PlacementCondition.Context context, BlockPos pos) {
      if (context.generator() instanceof NoiseBasedChunkGenerator chunkGenerator) {
         DensityFunction df = ((DensityFunction)this.densityFunction.value())
            .mapAll(new DensityFunctionWrapper(context, (NoiseGeneratorSettings)chunkGenerator.generatorSettings().value()));
         double density = df.compute(SimpleContext.of(pos));
         return this.range.isValueInRange(density);
      } else {
         return false;
      }
   }

   @Override
   public MapCodec<? extends PlacementCondition> codec() {
      return CODEC;
   }
}
