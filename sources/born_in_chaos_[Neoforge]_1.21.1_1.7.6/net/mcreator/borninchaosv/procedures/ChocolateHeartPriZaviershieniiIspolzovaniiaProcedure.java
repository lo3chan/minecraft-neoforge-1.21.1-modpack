package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ChocolateHeartPriZaviershieniiIspolzovaniiaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity) {
            _entity.setHealth((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) + 2.0F);
         }
      }
   }
}
