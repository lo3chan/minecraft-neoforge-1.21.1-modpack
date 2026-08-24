package com.iafenvoy.origins.util;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public final class LevelUtil {
   public static boolean inSnow(Level world, BlockPos... blockPositions) {
      return Arrays.stream(blockPositions)
         .anyMatch(
            blockPos -> ((Biome)world.getBiome(blockPos).value()).getPrecipitationAt(blockPos) == Precipitation.SNOW && isRainingAndExposed(world, blockPos)
         );
   }

   public static boolean inThunderstorm(Level world, BlockPos... blockPositions) {
      return Arrays.stream(blockPositions).anyMatch(blockPos -> world.isThundering() && isRainingAndExposed(world, blockPos));
   }

   private static boolean isRainingAndExposed(Level world, BlockPos blockPos) {
      return world.isRaining() && world.canSeeSky(blockPos) && world.getHeightmapPos(Types.MOTION_BLOCKING, blockPos).getY() < blockPos.getY();
   }
}
