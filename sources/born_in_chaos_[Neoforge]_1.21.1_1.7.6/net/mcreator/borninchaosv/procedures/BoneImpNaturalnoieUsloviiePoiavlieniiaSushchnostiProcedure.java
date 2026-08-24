package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.minecraft.world.level.LevelAccessor;

public class BoneImpNaturalnoieUsloviiePoiavlieniiaSushchnostiProcedure {
   public static boolean execute(LevelAccessor world) {
      return world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.BONEIMPSPAWN);
   }
}
