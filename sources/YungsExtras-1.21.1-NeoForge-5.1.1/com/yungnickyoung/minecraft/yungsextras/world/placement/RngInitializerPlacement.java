package com.yungnickyoung.minecraft.yungsextras.world.placement;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsextras.module.PlacementModifierTypeModule;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RngInitializerPlacement extends PlacementModifier {
   private static final RngInitializerPlacement INSTANCE = new RngInitializerPlacement();
   public static final MapCodec<PlacementModifier> CODEC = MapCodec.unit(() -> INSTANCE);

   public static RngInitializerPlacement randomize() {
      return INSTANCE;
   }

   public Stream<BlockPos> getPositions(PlacementContext placementContext, RandomSource random, BlockPos pos) {
      long a = random.nextLong() | 1L;
      long b = random.nextLong() | 1L;
      random.setSeed((pos.getX() * a * 341873128712L + 12412146L) * (pos.getZ() * b * 132897987541L + 5813717L) ^ 423487234L);
      return Stream.of(pos);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierTypeModule.RNG_INITIALIZER;
   }
}
