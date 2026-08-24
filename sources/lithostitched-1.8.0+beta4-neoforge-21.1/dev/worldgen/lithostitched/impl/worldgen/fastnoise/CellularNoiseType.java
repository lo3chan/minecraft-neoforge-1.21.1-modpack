package dev.worldgen.lithostitched.impl.worldgen.fastnoise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FNL;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import net.minecraft.util.StringRepresentable;

public class CellularNoiseType extends FastNoiseConfig {
   public static final MapCodec<CellularNoiseType> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.FLOAT.fieldOf("frequency").forGetter(FastNoiseConfig::frequency),
            Codec.INT.optionalFieldOf("salt", 0).forGetter(FastNoiseConfig::salt),
            StringRepresentable.fromValues(CellularNoiseType.DistanceFunction::values)
               .fieldOf("distance_function")
               .forGetter(CellularNoiseType::distanceFunction),
            StringRepresentable.fromValues(CellularNoiseType.ReturnType::values).fieldOf("return_type").forGetter(CellularNoiseType::returnType),
            Codec.floatRange(-1.0F, 1.0F).fieldOf("jitter").forGetter(CellularNoiseType::jitter)
         )
         .apply(instance, CellularNoiseType::new)
   );
   private final CellularNoiseType.DistanceFunction distanceFunction;
   private final CellularNoiseType.ReturnType returnType;
   private final float jitter;

   public CellularNoiseType(
      float frequency, int salt, CellularNoiseType.DistanceFunction distanceFunction, CellularNoiseType.ReturnType returnType, float jitter
   ) {
      super(frequency, salt);
      this.distanceFunction = distanceFunction;
      this.returnType = returnType;
      this.jitter = jitter;
      this.fnl.SetNoiseType(FNL.NoiseType.Cellular);
      this.fnl.SetCellularDistanceFunction(distanceFunction.internal);
      this.fnl.SetCellularReturnType(returnType.internal);
      this.fnl.SetCellularJitter(jitter);
   }

   public CellularNoiseType.DistanceFunction distanceFunction() {
      return this.distanceFunction;
   }

   private CellularNoiseType.ReturnType returnType() {
      return this.returnType;
   }

   private float jitter() {
      return this.jitter;
   }

   @Override
   public MapCodec<CellularNoiseType> getCodec() {
      return CODEC;
   }

   public static enum DistanceFunction implements StringRepresentable {
      EUCLIDEAN("euclidean", FNL.CellularDistanceFunction.Euclidean),
      EUCLIDEAN_SQUARED("euclidean_squared", FNL.CellularDistanceFunction.EuclideanSq),
      MANHATTAN("manhattan", FNL.CellularDistanceFunction.Manhattan),
      HYBRID("hybrid", FNL.CellularDistanceFunction.Hybrid);

      private final String id;
      private final FNL.CellularDistanceFunction internal;

      private DistanceFunction(String id, FNL.CellularDistanceFunction internal) {
         this.id = id;
         this.internal = internal;
      }

      public String getSerializedName() {
         return this.id;
      }
   }

   public static enum ReturnType implements StringRepresentable {
      CELL_VALUE("cell_value", FNL.CellularReturnType.CellValue),
      DISTANCE("distance", FNL.CellularReturnType.Distance),
      DISTANCE_2("distance_2", FNL.CellularReturnType.Distance2),
      DISTANCE_2_ADD("distance_2_add", FNL.CellularReturnType.Distance2Add),
      DISTANCE_2_SUB("distance_2_sub", FNL.CellularReturnType.Distance2Sub),
      DISTANCE_2_MUL("distance_2_mul", FNL.CellularReturnType.Distance2Mul),
      DISTANCE_2_DIV("distance_2_div", FNL.CellularReturnType.Distance2Div);

      private final String id;
      private final FNL.CellularReturnType internal;

      private ReturnType(String id, FNL.CellularReturnType internal) {
         this.id = id;
         this.internal = internal;
      }

      public String getSerializedName() {
         return this.id;
      }
   }
}
