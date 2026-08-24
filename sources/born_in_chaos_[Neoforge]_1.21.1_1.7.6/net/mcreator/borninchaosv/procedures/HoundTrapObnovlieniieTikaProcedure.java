package net.mcreator.borninchaosv.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;

public class HoundTrapObnovlieniieTikaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (!world.getBlockState(BlockPos.containing(x, y - 1.0, z)).canOcclude()) {
         BlockPos _pos = BlockPos.containing(x, y, z);
         Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
         world.destroyBlock(_pos, false);
      }
   }
}
