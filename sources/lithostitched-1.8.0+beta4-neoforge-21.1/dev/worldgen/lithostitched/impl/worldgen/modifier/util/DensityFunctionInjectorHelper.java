package dev.worldgen.lithostitched.impl.worldgen.modifier.util;

import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.MarkerFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.MergedDensityFunction;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.marker.OriginalMarkerDensityFunction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions.HolderHolder;

public class DensityFunctionInjectorHelper {
   public static DensityFunction wrap(DensityFunction wrapped, DensityFunction wrapper) {
      if (wrapped instanceof MergedDensityFunction merged) {
         DensityFunction original = merged.original();
         return new MergedDensityFunction(original, wrapped, wrapper.mapAll(value -> {
            if (isMarker(value)) {
               return value instanceof OriginalMarkerDensityFunction ? original : wrapped;
            } else {
               return value;
            }
         }));
      } else {
         return new MergedDensityFunction(wrapped, wrapped, wrapper.mapAll(value -> isMarker(value) ? wrapped : value));
      }
   }

   private static boolean isMarker(DensityFunction df) {
      if (df instanceof HolderHolder var1) {
         HolderHolder var10000 = var1;

         try {
            var5 = var10000.function();
         } catch (Throwable var4) {
            throw new MatchException(var4.toString(), var4);
         }

         Holder var3 = var5;
         df = (DensityFunction)var3.value();
      }

      return df instanceof MarkerFunction;
   }
}
