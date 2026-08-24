package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import java.util.List;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.ContextProvider;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.DensityFunction.Visitor;
import org.jetbrains.annotations.NotNull;

public record SelectDensityFunction(DensityFunction input, DensityFunction fallback, List<SelectDensityFunction.Selection> selections, double min, double max)
   implements DensityFunction {
   public static final MapCodec<SelectDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            LithostitchedCodecs.DF_BASE.fieldOf("input").forGetter(SelectDensityFunction::input),
            LithostitchedCodecs.DF_BASE.fieldOf("fallback").forGetter(SelectDensityFunction::fallback),
            SelectDensityFunction.Selection.CODEC.listOf(1, 2147483647).fieldOf("selections").forGetter(SelectDensityFunction::selections)
         )
         .apply(i, SelectDensityFunction::create)
   );
   public static final KeyDispatchDataCodec<SelectDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

   public static SelectDensityFunction create(DensityFunction input, DensityFunction fallback, List<SelectDensityFunction.Selection> selections) {
      double min = 5.0E-324;
      double max = 1.7976931348623157E308;

      for (SelectDensityFunction.Selection selection : selections) {
         min = Math.min(min, selection.function.minValue());
         max = Math.max(max, selection.function.maxValue());
      }

      return new SelectDensityFunction(input, fallback, selections, min, max);
   }

   public double compute(FunctionContext context) {
      double value = this.input.compute(context);

      for (SelectDensityFunction.Selection selection : this.selections) {
         if (selection.range.isValueInRange(value)) {
            return selection.function.compute(context);
         }
      }

      return this.fallback.compute(context);
   }

   public void fillArray(double[] densities, ContextProvider applier) {
      applier.fillAllDirectly(densities, this);
   }

   @NotNull
   public DensityFunction mapAll(Visitor visitor) {
      return new SelectDensityFunction(
         this.input.mapAll(visitor),
         this.fallback.mapAll(visitor),
         this.selections.stream().map(selection -> selection.mapAll(visitor)).toList(),
         this.min,
         this.max
      );
   }

   public double minValue() {
      return this.min;
   }

   public double maxValue() {
      return this.max;
   }

   @NotNull
   public KeyDispatchDataCodec<? extends DensityFunction> codec() {
      return CODEC;
   }

   public record Selection(InclusiveRange<Double> range, DensityFunction function) {
      public static final Codec<SelectDensityFunction.Selection> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               LithostitchedCodecs.DOUBLE_RANGE.fieldOf("range").forGetter(SelectDensityFunction.Selection::range),
               LithostitchedCodecs.DF_BASE.fieldOf("function").forGetter(SelectDensityFunction.Selection::function)
            )
            .apply(instance, SelectDensityFunction.Selection::new)
      );

      public SelectDensityFunction.Selection mapAll(Visitor visitor) {
         return new SelectDensityFunction.Selection(this.range, this.function.mapAll(visitor));
      }

      public static SelectDensityFunction.Selection create(Pair<InclusiveRange<Double>, DensityFunction> pair) {
         return new SelectDensityFunction.Selection((InclusiveRange<Double>)pair.getFirst(), (DensityFunction)pair.getSecond());
      }
   }
}
