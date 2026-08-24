package dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker;

import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.ContextProvider;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.DensityFunction.Visitor;
import net.minecraft.world.level.levelgen.DensityFunctions.HolderHolder;

public record MergedDensityFunction(DensityFunction original, DensityFunction wrapped, DensityFunction full) implements DensityFunction {
   public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(LithostitchedCodecs.DF_BASE.xmap(df -> {
      DensityFunction var6;
      if (df instanceof HolderHolder $b$0) {
         HolderHolder var10000 = $b$0;

         try {
            var5 = var10000.function();
         } catch (Throwable var4) {
            throw new MatchException(var4.toString(), var4);
         }

         Holder patt1$temp = var5;
         var6 = (DensityFunction)patt1$temp.value();
      } else {
         var6 = df;
      }

      return var6;
   }, MergedDensityFunction::unwrappedOriginal).fieldOf("original"));

   private static DensityFunction unwrappedOriginal(DensityFunction df) {
      return df instanceof MergedDensityFunction merged ? unwrappedOriginal(merged.original()) : df;
   }

   public double compute(FunctionContext context) {
      return this.full.compute(context);
   }

   public void fillArray(double[] doubles, ContextProvider contextProvider) {
      this.full.fillArray(doubles, contextProvider);
   }

   public DensityFunction mapAll(Visitor visitor) {
      return this.full.mapAll(visitor);
   }

   public double minValue() {
      return this.full.minValue();
   }

   public double maxValue() {
      return this.full.maxValue();
   }

   public KeyDispatchDataCodec<? extends DensityFunction> codec() {
      return CODEC;
   }
}
