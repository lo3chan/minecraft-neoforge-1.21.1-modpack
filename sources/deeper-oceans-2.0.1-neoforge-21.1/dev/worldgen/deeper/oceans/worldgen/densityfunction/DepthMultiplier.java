package dev.worldgen.deeper.oceans.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import dev.worldgen.deeper.oceans.DeeperOceans;
import dev.worldgen.deeper.oceans.config.ConfigState;
import java.util.Arrays;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.DensityFunction.ContextProvider;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.DensityFunction.Visitor;

public record DepthMultiplier(double value) implements DensityFunction {
   public static MapCodec<DepthMultiplier> DATA_CODEC = MapCodec.unit(() -> new DepthMultiplier(((ConfigState)DeeperOceans.CONFIG.getState()).depthMultiplier));
   public static KeyDispatchDataCodec<DepthMultiplier> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);

   public double compute(FunctionContext context) {
      return this.value;
   }

   public void fillArray(double[] doubles, ContextProvider contextProvider) {
      Arrays.fill(doubles, this.value);
   }

   public DensityFunction mapAll(Visitor visitor) {
      return DensityFunctions.constant(this.value).mapAll(visitor);
   }

   public double minValue() {
      return this.value;
   }

   public double maxValue() {
      return this.value;
   }

   public KeyDispatchDataCodec<? extends DensityFunction> codec() {
      return CODEC_HOLDER;
   }
}
