package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.Visitor;

public class CosDensityFunction extends TransformerDensityFunction {
   public static final MapCodec<CosDensityFunction> DATA_CODEC = LithostitchedCodecs.DF_BASE
      .fieldOf("argument")
      .xmap(CosDensityFunction::new, TransformerDensityFunction::argument);
   public static KeyDispatchDataCodec<CosDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);

   public CosDensityFunction(DensityFunction argument) {
      super(argument);
   }

   @Override
   public double transform(double value) {
      return Math.cos(value);
   }

   @Override
   public double minValue() {
      return -1.0;
   }

   @Override
   public double maxValue() {
      return 1.0;
   }

   public DensityFunction mapAll(Visitor visitor) {
      return new CosDensityFunction(this.argument().mapAll(visitor));
   }

   public KeyDispatchDataCodec<? extends DensityFunction> codec() {
      return CODEC_HOLDER;
   }
}
