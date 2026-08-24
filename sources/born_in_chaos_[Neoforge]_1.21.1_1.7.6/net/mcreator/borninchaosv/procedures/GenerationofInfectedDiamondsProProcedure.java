package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.minecraft.world.level.LevelAccessor;

public class GenerationofInfectedDiamondsProProcedure {
   public static boolean execute(LevelAccessor world) {
      return world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.GENERATIONOFINFECTEDDIAMONDS);
   }
}
