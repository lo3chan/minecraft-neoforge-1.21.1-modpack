package com.yungnickyoung.minecraft.yungsapi.world.util;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class SurfaceHelper {
   private SurfaceHelper() {
   }

   public static int getSurfaceHeight(ChunkAccess chunk, ColumnPos pos) {
      int maxY = chunk.getMaxBuildHeight() - 1;
      MutableBlockPos blockPos = new MutableBlockPos(pos.x(), maxY, pos.z());
      if (chunk.getBlockState(blockPos) != Blocks.AIR.defaultBlockState()) {
         return maxY;
      } else {
         for (int y = maxY; y >= 0; y--) {
            BlockState blockState = chunk.getBlockState(blockPos);
            if (blockState != Blocks.AIR.defaultBlockState()) {
               return y;
            }

            blockPos.move(Direction.DOWN);
         }

         return 1;
      }
   }
}
