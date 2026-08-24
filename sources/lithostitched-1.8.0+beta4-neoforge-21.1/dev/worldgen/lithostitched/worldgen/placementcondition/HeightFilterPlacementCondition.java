package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public record HeightFilterPlacementCondition(
   HeightFilterPlacementCondition.RangeType rangeType, Optional<Types> heightmap, InclusiveRange<Integer> permittedRange
) implements PlacementCondition {
   public static final MapCodec<HeightFilterPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               HeightFilterPlacementCondition.RangeType.CODEC.fieldOf("range_type").forGetter(HeightFilterPlacementCondition::rangeType),
               Types.CODEC.optionalFieldOf("heightmap").forGetter(HeightFilterPlacementCondition::heightmap),
               LithostitchedCodecs.INT_RANGE.fieldOf("permitted_range").forGetter(HeightFilterPlacementCondition::permittedRange)
            )
            .apply(instance, HeightFilterPlacementCondition::new)
      )
      .validate(HeightFilterPlacementCondition::validate);

   private DataResult<HeightFilterPlacementCondition> validate() {
      return this.rangeType == HeightFilterPlacementCondition.RangeType.HEIGHTMAP_RELATIVE && this.heightmap.isEmpty()
         ? DataResult.error(() -> "Heightmap relative range type must be used with a heightmap")
         : DataResult.success(this);
   }

   @Override
   public boolean test(PlacementCondition.Context context, BlockPos pos) {
      if (this.heightmap.isEmpty()) {
         return this.permittedRange.isValueInRange(pos.getY());
      } else {
         int heightmapY = context.generator().getFirstFreeHeight(pos.getX(), pos.getZ(), this.heightmap.get(), context.heightAccessor(), context.randomState());
         int y = this.rangeType == HeightFilterPlacementCondition.RangeType.ABSOLUTE ? heightmapY : pos.getY() - heightmapY;
         return this.permittedRange.isValueInRange(y);
      }
   }

   @Override
   public MapCodec<? extends PlacementCondition> codec() {
      return CODEC;
   }

   public static enum RangeType implements StringRepresentable {
      ABSOLUTE("absolute"),
      HEIGHTMAP_RELATIVE("heightmap_relative");

      public static final Codec<HeightFilterPlacementCondition.RangeType> CODEC = StringRepresentable.fromEnum(HeightFilterPlacementCondition.RangeType::values);
      private final String name;

      private RangeType(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
