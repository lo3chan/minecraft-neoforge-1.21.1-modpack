package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.mcreator.borninchaosv.network.BornInChaosV1ModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public class CreepyCookiesWithMilkPriZaviershieniiIspolzovaniiaProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity) {
            _entity.removeAllEffects();
         }

         if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 3.0
            && world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.NAUGHTINESS_MECHANICS)) {
            BornInChaosV1ModVariables.PlayerVariables _vars = (BornInChaosV1ModVariables.PlayerVariables)entity.getData(
               BornInChaosV1ModVariables.PLAYER_VARIABLES
            );
            _vars.naughtiness = ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness - 3.0;
            _vars.syncPlayerVariables(entity);
            if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 35.0) {
               entity.getPersistentData().putBoolean("firstwarning", false);
               entity.getPersistentData().putBoolean("secondwarning", false);
               entity.getPersistentData().putBoolean("finalwarning", false);
            } else if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness > 50.0
               && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 65.0) {
               entity.getPersistentData().putBoolean("secondwarning", false);
               entity.getPersistentData().putBoolean("finalwarning", false);
            } else if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness > 75.0
               && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 85.0) {
               entity.getPersistentData().putBoolean("finalwarning", false);
            }
         }
      }
   }
}
