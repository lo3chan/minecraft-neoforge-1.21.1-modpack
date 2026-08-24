package dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker;

import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.DensityFunction.SimpleFunction;

public interface MarkerFunction extends SimpleFunction {
   default double compute(FunctionContext context) {
      throw new IllegalStateException("Marker density function should never be computed!");
   }

   default double minValue() {
      return 0.0;
   }

   default double maxValue() {
      return 0.0;
   }
}
