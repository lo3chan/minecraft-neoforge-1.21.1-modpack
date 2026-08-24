package dev.worldgen.lithostitched.impl.worldgen.surface.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.SimpleContext;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.duck.ContextAccessor;
import dev.worldgen.lithostitched.duck.SeedAccessor;
import dev.worldgen.lithostitched.mixin.common.NoiseChunkAccessor;
import dev.worldgen.lithostitched.mixin.common.RandomStateAccessor;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules.Condition;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;

public record SampleDensityCondition(DensityFunction densityFunction, InclusiveRange<Double> range) implements ConditionSource {
   public static final MapCodec<SampleDensityCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("density_function").forGetter(condition -> condition.densityFunction),
            Codec.DOUBLE.optionalFieldOf("min_inclusive", 5.0E-324).forGetter(condition -> (Double)condition.range.minInclusive()),
            Codec.DOUBLE.optionalFieldOf("max_inclusive", 1.7976931348623157E308).forGetter(condition -> (Double)condition.range.maxInclusive())
         )
         .apply(i, SampleDensityCondition::new)
   );
   public static final KeyDispatchDataCodec<SampleDensityCondition> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);

   public SampleDensityCondition(DensityFunction densityFunction, double minInclusive, double maxInclusive) {
      this(densityFunction, new InclusiveRange(minInclusive, maxInclusive));
   }

   public KeyDispatchDataCodec<? extends ConditionSource> codec() {
      return DATA_CODEC;
   }

   public Condition apply(Context context) {
      ContextAccessor accessor = (ContextAccessor)context;
      NoiseChunk noiseChunk = accessor.getNoiseChunk();
      RandomState randomState = accessor.getRandomState();
      long seed = ((SeedAccessor)randomState).getSeed();
      DensityFunctionWrapper wrapper = new DensityFunctionWrapper(seed, false, randomState, ((RandomStateAccessor)randomState).getRandom());
      DensityFunction df = this.densityFunction.mapAll(wrapper).mapAll(((NoiseChunkAccessor)noiseChunk)::lithostitched$wrap);
      return () -> this.range.isValueInRange(df.compute(SimpleContext.of(accessor.getPos())));
   }
}
