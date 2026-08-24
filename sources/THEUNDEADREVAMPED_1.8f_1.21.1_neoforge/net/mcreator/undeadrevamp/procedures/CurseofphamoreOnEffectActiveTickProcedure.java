package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CurseofphamoreOnEffectActiveTickProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (Math.random() < 0.2) {
            if (entity instanceof Player _player) {
               _player.getFoodData().setSaturation(entity instanceof Player _plr ? _plr.getFoodData().getSaturationLevel() : 0.0F);
            }

            if (entity instanceof Player _player) {
               _player.getFoodData().setFoodLevel((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) - 1);
            }
         }
      }
   }
}
