package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.network.BornInChaosV1ModVariables;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class KrampusPriGibieliSushchnostiProcedure {
   public static void execute(Entity sourceentity) {
      if (sourceentity != null) {
         BornInChaosV1ModVariables.PlayerVariables _vars = (BornInChaosV1ModVariables.PlayerVariables)sourceentity.getData(
            BornInChaosV1ModVariables.PLAYER_VARIABLES
         );
         _vars.naughtiness = 15.0;
         _vars.syncPlayerVariables(sourceentity);
         if (sourceentity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:wrong_santa"));
            if (_adv != null) {
               AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _player.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }
      }
   }
}
