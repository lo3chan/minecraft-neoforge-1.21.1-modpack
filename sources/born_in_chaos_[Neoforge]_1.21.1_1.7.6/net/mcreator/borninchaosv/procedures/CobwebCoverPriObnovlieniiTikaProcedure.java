package net.mcreator.borninchaosv.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class CobwebCoverPriObnovlieniiTikaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world.getBlockState(BlockPos.containing(x, y, z)) == Blocks.WATER.defaultBlockState()
         || !world.getBlockState(BlockPos.containing(x, y - 1.0, z)).canOcclude()) {
         world.destroyBlock(BlockPos.containing(x, y, z), false);
      }
   }
}
