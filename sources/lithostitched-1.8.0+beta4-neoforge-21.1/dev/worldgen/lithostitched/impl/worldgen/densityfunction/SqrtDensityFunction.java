package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.Visitor;

public class SqrtDensityFunction extends TransformerDensityFunction {
   public static final MapCodec<SqrtDensityFunction> DATA_CODEC = LithostitchedCodecs.DF_BASE
      .fieldOf("argument")
      .xmap(SqrtDensityFunction::new, TransformerDensityFunction::argument);
   public static KeyDispatchDataCodec<SqrtDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);

   public SqrtDensityFunction(DensityFunction argument) {
      super(argument);
   }

   @Override
   public double transform(double value) {
      if (value == 0.0) {
         return 0.0;
      } else {
         return value > 0.0 ? Math.sqrt(value) : -Math.sqrt(-value);
      }
   }

   public DensityFunction mapAll(Visitor visitor) {
      return new SqrtDensityFunction(this.argument().mapAll(visitor));
   }

   public KeyDispatchDataCodec<? extends DensityFunction> codec() {
      return CODEC_HOLDER;
   }
}
