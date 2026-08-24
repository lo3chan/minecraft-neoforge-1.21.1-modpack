package com.yungnickyoung.minecraft.betterendisland.world.util;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WorldgenUtils {
   public static int getLowestBlockPosAt(Level level, int x, int z) {
      MutableBlockPos mutable = new MutableBlockPos();

      for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
         mutable.set(x, y, z);
         if (level.getBlockState(mutable).is(Blocks.END_STONE)) {
            return y;
         }
      }

      return 255;
   }

   public static int getSurfacePosAt(Level level, int x, int z) {
      MutableBlockPos mutable = new MutableBlockPos();

      for (int y = level.getMaxBuildHeight(); y > level.getMinBuildHeight(); y--) {
         mutable.set(x, y, z);
         if (level.getBlockState(mutable).is(Blocks.END_STONE)) {
            return y;
         }
      }

      return -1;
   }

   public static double distSqr(double x1, double z1, double x2, double z2) {
      double xDist = x2 - x1;
      double zDist = z2 - z1;
      return xDist * xDist + zDist * zDist;
   }
}
