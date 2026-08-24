package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class StimulatingBombPoslieIspolzovaniiaSnariadaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof Player _player) {
            _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.STIMULATING_BOMB.get(), 25);
         }
      }
   }
}
