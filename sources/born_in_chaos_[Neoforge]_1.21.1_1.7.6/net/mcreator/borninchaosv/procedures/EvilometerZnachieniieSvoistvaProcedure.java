package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.network.BornInChaosV1ModVariables;
import net.minecraft.world.entity.Entity;

public class EvilometerZnachieniieSvoistvaProcedure {
   public static double execute(Entity entity) {
      if (entity == null) {
         return 0.0;
      } else {
         return ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness >= 25.0
               && ((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 45.0
            ? 1.0
            : 0.0;
      }
   }
}
