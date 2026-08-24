package net.mcreator.borninchaosv.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public class SpidersMotherAtackCodisionProcedure {
   public static boolean execute(LevelAccessor world, double x, double y, double z) {
      return world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 9;
   }
}
