package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.level.LevelAccessor;

public class RainPProcedure {
   public static boolean execute(LevelAccessor world) {
      return !world.getLevelData().isThundering() && !world.getLevelData().isRaining();
   }
}
