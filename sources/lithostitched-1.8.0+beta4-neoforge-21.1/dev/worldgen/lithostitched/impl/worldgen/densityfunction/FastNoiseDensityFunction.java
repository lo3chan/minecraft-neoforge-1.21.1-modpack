package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.DensityFunction.ContextProvider;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.DensityFunction.Visitor;

public record FastNoiseDensityFunction(
   Holder<FastNoiseConfig> config, double xzScale, double yScale, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ
) implements DensityFunction {
   public static final MapCodec<FastNoiseDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            RegistryFileCodec.create(LithostitchedRegistries.FAST_NOISE_CONFIG, FastNoiseConfig.CODEC, false)
               .fieldOf("config")
               .forGetter(FastNoiseDensityFunction::config),
            Codec.DOUBLE.optionalFieldOf("xz_scale", 1.0).forGetter(FastNoiseDensityFunction::xzScale),
            Codec.DOUBLE.optionalFieldOf("y_scale", 1.0).forGetter(FastNoiseDensityFunction::yScale),
            LithostitchedCodecs.DF_BASE.optionalFieldOf("shift_x", DensityFunctions.zero()).forGetter(FastNoiseDensityFunction::shiftX),
            LithostitchedCodecs.DF_BASE.optionalFieldOf("shift_y", DensityFunctions.zero()).forGetter(FastNoiseDensityFunction::shiftY),
            LithostitchedCodecs.DF_BASE.optionalFieldOf("shift_z", DensityFunctions.zero()).forGetter(FastNoiseDensityFunction::shiftZ)
         )
         .apply(instance, FastNoiseDensityFunction::new)
   );
   public static final KeyDispatchDataCodec<FastNoiseDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

   public double compute(FunctionContext context) {
      return ((FastNoiseConfig)this.config.value())
         .sample(
            context.blockX() * this.xzScale + this.shiftX.compute(context),
            context.blockY() * this.yScale + this.shiftY.compute(context),
            context.blockZ() * this.xzScale + this.shiftZ.compute(context)
         );
   }

   public void fillArray(double[] doubles, ContextProvider contextProvider) {
      contextProvider.fillAllDirectly(doubles, this);
   }

   public DensityFunction mapAll(Visitor visitor) {
      return new FastNoiseDensityFunction(
         this.config, this.xzScale, this.yScale, this.shiftX.mapAll(visitor), this.shiftY.mapAll(visitor), this.shiftZ.mapAll(visitor)
      );
   }

   public double minValue() {
      return -1.0;
   }

   public double maxValue() {
      return 1.0;
   }

   public KeyDispatchDataCodec<? extends DensityFunction> codec() {
      return CODEC;
   }
}
