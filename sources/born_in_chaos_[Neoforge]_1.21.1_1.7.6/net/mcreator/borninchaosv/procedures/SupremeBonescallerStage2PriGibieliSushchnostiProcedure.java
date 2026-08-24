package net.mcreator.borninchaosv.procedures;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class SupremeBonescallerStage2PriGibieliSushchnostiProcedure {
   public static void execute(Entity sourceentity) {
      if (sourceentity != null) {
         if (sourceentity instanceof Player
            && !(
               sourceentity instanceof ServerPlayer _plr1
                  && _plr1.level() instanceof ServerLevel
                  && _plr1.getAdvancements()
                     .getOrStartProgress(_plr1.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:dismantledto_bones")))
                     .isDone()
            )
            && sourceentity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:dismantledto_bones"));
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
