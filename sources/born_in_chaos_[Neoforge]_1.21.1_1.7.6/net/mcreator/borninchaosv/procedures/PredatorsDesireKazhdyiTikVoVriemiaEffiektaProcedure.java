package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class PredatorsDesireKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof Player _player) {
            _player.causeFoodExhaustion(0.02F);
         }
      }
   }
}
