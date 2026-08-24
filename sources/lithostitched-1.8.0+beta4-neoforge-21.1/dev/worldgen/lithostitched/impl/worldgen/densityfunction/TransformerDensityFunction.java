package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.ContextProvider;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;

public abstract class TransformerDensityFunction implements DensityFunction {
   private final DensityFunction argument;

   public TransformerDensityFunction(DensityFunction argument) {
      this.argument = argument;
   }

   public DensityFunction argument() {
      return this.argument;
   }

   public abstract double transform(double var1);

   public double compute(FunctionContext functionContext) {
      return this.transform(this.argument.compute(functionContext));
   }

   public void fillArray(double[] densities, ContextProvider context) {
      this.argument.fillArray(densities, context);

      for (int i = 0; i < densities.length; i++) {
         densities[i] = this.transform(densities[i]);
      }
   }

   public double minValue() {
      return this.transform(this.argument.minValue());
   }

   public double maxValue() {
      return this.transform(this.argument.maxValue());
   }
}
