package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public class BigsuckerNaturalEntitySpawningConditionProcedure {
   public static boolean execute(LevelAccessor world, double x, double y, double z) {
      return (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))
            == Level.OVERWORLD
         && world.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z) < 5
         && world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 7;
   }
}
