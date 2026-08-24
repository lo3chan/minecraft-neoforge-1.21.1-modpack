package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.Visitor;

public class FloorDensityFunction extends TransformerDensityFunction {
   public static final MapCodec<FloorDensityFunction> DATA_CODEC = LithostitchedCodecs.DF_BASE
      .fieldOf("argument")
      .xmap(FloorDensityFunction::new, TransformerDensityFunction::argument);
   public static KeyDispatchDataCodec<FloorDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);

   public FloorDensityFunction(DensityFunction argument) {
      super(argument);
   }

   @Override
   public double transform(double value) {
      return Math.floor(value);
   }

   public DensityFunction mapAll(Visitor visitor) {
      return new FloorDensityFunction(this.argument().mapAll(visitor));
   }

   public KeyDispatchDataCodec<? extends DensityFunction> codec() {
      return CODEC_HOLDER;
   }
}
