package com.yungnickyoung.minecraft.yungsbridges.world.placement;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsbridges.module.PlacementModifierTypeModule;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RngInitializerPlacement extends PlacementModifier {
   private static final RngInitializerPlacement INSTANCE = new RngInitializerPlacement();
   public static final MapCodec<RngInitializerPlacement> CODEC = MapCodec.unit(() -> INSTANCE);

   public static RngInitializerPlacement randomized() {
      return INSTANCE;
   }

   public Stream<BlockPos> getPositions(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos) {
      long a = randomSource.nextLong() | 1L;
      long b = randomSource.nextLong() | 1L;
      randomSource.setSeed((blockPos.getX() * a * 951873395712L + 12132586L) * (blockPos.getZ() * b * 132899567841L + 9789717L) ^ 313281234L);
      return Stream.of(blockPos);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierTypeModule.RNG_INITIALIZER_PLACEMENT;
   }
}
