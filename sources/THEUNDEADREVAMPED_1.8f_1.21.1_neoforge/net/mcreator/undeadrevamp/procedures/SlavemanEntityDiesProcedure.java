package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThehunterEntity;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;

public class SlavemanEntityDiesProcedure {
   public static void execute(Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:thineded"));
            if (_adv != null) {
               AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _player.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }

         if (sourceentity instanceof Player
            && sourceentity.getVehicle() instanceof Horse
            && entity.getVehicle() instanceof ThehunterEntity
            && sourceentity instanceof ServerPlayer _playerx) {
            AdvancementHolder _adv = _playerx.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:huntflight"));
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
