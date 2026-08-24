package net.mcreator.borninchaosv.procedures;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class MissionerPriGibieliSushchnostiProcedure {
   public static void execute(LevelAccessor world, Entity sourceentity) {
      if (sourceentity != null) {
         if (world.getLevelData().isThundering() || world.getLevelData().isRaining()) {
            world.getLevelData().setRaining(false);
         }

         if (sourceentity instanceof Player && sourceentity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:dark_creator"));
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
