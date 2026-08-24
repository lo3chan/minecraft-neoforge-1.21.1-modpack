package com.finndog.moogs_structures.world.placements;

import com.finndog.moogs_structures.modinit.MoogsStructuresPlacements;
import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class SnapToLowerNonAirPlacement extends PlacementModifier {
   private static final SnapToLowerNonAirPlacement INSTANCE = new SnapToLowerNonAirPlacement();
   public static final MapCodec<SnapToLowerNonAirPlacement> CODEC = MapCodec.unit(() -> INSTANCE);

   public static SnapToLowerNonAirPlacement snapToLowerNonAir() {
      return INSTANCE;
   }

   public final Stream<BlockPos> getPositions(PlacementContext placementContext, RandomSource random, BlockPos blockPos) {
      MutableBlockPos mutable = new MutableBlockPos().set(blockPos);

      while (placementContext.getBlockState(mutable).isAir() && mutable.getY() > placementContext.getMinGenY()) {
         mutable.move(Direction.DOWN);
      }

      return Stream.of(mutable.immutable());
   }

   public PlacementModifierType<?> type() {
      return MoogsStructuresPlacements.SNAP_TO_LOWER_NON_AIR_PLACEMENT.get();
   }
}
