package com.finndog.moogs_structures.world.placements;

import com.finndog.moogs_structures.modinit.MoogsStructuresPlacements;
import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class MinusEightPlacement extends PlacementModifier {
   private static final MinusEightPlacement INSTANCE = new MinusEightPlacement();
   public static final MapCodec<MinusEightPlacement> CODEC = MapCodec.unit(() -> INSTANCE);

   public static MinusEightPlacement subtractedEight() {
      return INSTANCE;
   }

   public Stream<BlockPos> getPositions(PlacementContext placementContext, RandomSource random, BlockPos blockPos) {
      return Stream.of(new BlockPos(blockPos.getX() - 8, blockPos.getY(), blockPos.getZ() - 8));
   }

   public PlacementModifierType<?> type() {
      return MoogsStructuresPlacements.MINUS_EIGHT_PLACEMENT.get();
   }
}
