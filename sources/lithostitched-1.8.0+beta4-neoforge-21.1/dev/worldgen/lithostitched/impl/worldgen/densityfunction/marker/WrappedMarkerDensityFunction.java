package dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public class WrappedMarkerDensityFunction implements MarkerFunction {
   public static final KeyDispatchDataCodec<WrappedMarkerDensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(new WrappedMarkerDensityFunction()));

   @NotNull
   public KeyDispatchDataCodec<? extends DensityFunction> codec() {
      return CODEC;
   }
}
