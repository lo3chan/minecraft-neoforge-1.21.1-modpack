package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.Visitor;

public class CeilDensityFunction extends TransformerDensityFunction {
   public static final MapCodec<CeilDensityFunction> DATA_CODEC = LithostitchedCodecs.DF_BASE
      .fieldOf("argument")
      .xmap(CeilDensityFunction::new, TransformerDensityFunction::argument);
   public static KeyDispatchDataCodec<CeilDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);

   public CeilDensityFunction(DensityFunction argument) {
      super(argument);
   }

   @Override
   public double transform(double value) {
      return Math.ceil(value);
   }

   public DensityFunction mapAll(Visitor visitor) {
      return new CeilDensityFunction(this.argument().mapAll(visitor));
   }

   public KeyDispatchDataCodec<? extends DensityFunction> codec() {
      return CODEC_HOLDER;
   }
}
