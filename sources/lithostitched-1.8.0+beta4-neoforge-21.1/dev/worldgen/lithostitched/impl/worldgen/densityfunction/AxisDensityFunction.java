package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.ContextProvider;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.DensityFunction.Visitor;

public record AxisDensityFunction(Axis axis) implements DensityFunction {
   public static final MapCodec<AxisDensityFunction> DATA_CODEC = Axis.CODEC.fieldOf("axis").xmap(AxisDensityFunction::new, AxisDensityFunction::axis);
   public static KeyDispatchDataCodec<AxisDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);

   public double compute(FunctionContext context) {
      return switch (this.axis) {
         case X -> context.blockX();
         case Y -> context.blockY();
         case Z -> context.blockZ();
         default -> throw new MatchException(null, null);
      };
   }

   public void fillArray(double[] densities, ContextProvider context) {
      context.fillAllDirectly(densities, this);
   }

   public DensityFunction mapAll(Visitor visitor) {
      return this;
   }

   public double minValue() {
      return this.axis == Axis.Y ? -4064.0 : 5.0E-324;
   }

   public double maxValue() {
      return this.axis == Axis.Y ? 4064.0 : 1.7976931348623157E308;
   }

   public KeyDispatchDataCodec<? extends DensityFunction> codec() {
      return CODEC_HOLDER;
   }
}
