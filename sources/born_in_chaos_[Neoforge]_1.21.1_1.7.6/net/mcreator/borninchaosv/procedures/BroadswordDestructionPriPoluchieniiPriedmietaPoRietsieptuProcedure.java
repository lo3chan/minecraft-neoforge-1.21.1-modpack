package net.mcreator.borninchaosv.procedures;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class BroadswordDestructionPriPoluchieniiPriedmietaPoRietsieptuProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (!(
               entity instanceof ServerPlayer _plr0
                  && _plr0.level() instanceof ServerLevel
                  && _plr0.getAdvancements()
                     .getOrStartProgress(_plr0.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:dark_blacksmith")))
                     .isDone()
            )
            && entity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:dark_blacksmith"));
            if (_adv != null) {
               AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _player.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }

         if (!(
               entity instanceof ServerPlayer _plr2
                  && _plr2.level() instanceof ServerLevel
                  && _plr2.getAdvancements()
                     .getOrStartProgress(_plr2.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:my_size")))
                     .isDone()
            )
            && entity instanceof ServerPlayer _playerx) {
            AdvancementHolder _adv = _playerx.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:my_size"));
            if (_adv != null) {
               AdvancementProgress _ap = _playerx.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _playerx.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }
      }
   }
}
