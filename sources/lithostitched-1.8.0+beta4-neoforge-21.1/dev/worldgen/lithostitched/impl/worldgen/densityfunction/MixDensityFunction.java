package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.ContextProvider;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.DensityFunction.Visitor;
import org.jetbrains.annotations.NotNull;

public record MixDensityFunction(DensityFunction input, DensityFunction argument1, DensityFunction argument2) implements DensityFunction {
   public static final MapCodec<MixDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            LithostitchedCodecs.DF_BASE.fieldOf("input").forGetter(MixDensityFunction::input),
            LithostitchedCodecs.DF_BASE.fieldOf("argument1").forGetter(MixDensityFunction::argument1),
            LithostitchedCodecs.DF_BASE.fieldOf("argument2").forGetter(MixDensityFunction::argument2)
         )
         .apply(i, MixDensityFunction::new)
   );
   public static final KeyDispatchDataCodec<MixDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

   public double compute(FunctionContext context) {
      double input = this.input.compute(context);
      if (input <= 0.0) {
         return this.argument1.compute(context);
      } else if (input >= 1.0) {
         return this.argument2.compute(context);
      } else {
         double argument1 = this.argument1.compute(context);
         double argument2 = this.argument2.compute(context);
         return argument1 * (1.0 - input) + argument2 * input;
      }
   }

   public void fillArray(double[] densities, ContextProvider applier) {
      applier.fillAllDirectly(densities, this);
   }

   @NotNull
   public DensityFunction mapAll(Visitor visitor) {
      return new MixDensityFunction(this.input.mapAll(visitor), this.argument1.mapAll(visitor), this.argument2.mapAll(visitor));
   }

   public double minValue() {
      return Math.min(this.argument1.minValue(), this.argument2.minValue());
   }

   public double maxValue() {
      return Math.min(this.argument1.maxValue(), this.argument2.maxValue());
   }

   @NotNull
   public KeyDispatchDataCodec<? extends DensityFunction> codec() {
      return CODEC;
   }
}
