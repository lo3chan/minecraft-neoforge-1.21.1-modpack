package net.astralya.hexalia.util;

import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class InfusedFarmlandPlacementHelper {
   private InfusedFarmlandPlacementHelper() {
   }

   public static boolean isInfusedFarmland(BlockState state) {
      return state.is((Block)ModBlocks.INFUSED_FARMLAND.get());
   }

   public static boolean hasInfusedFarmlandBelow(LevelReader level, BlockPos pos) {
      return isInfusedFarmland(level.getBlockState(pos.below()));
   }
}
