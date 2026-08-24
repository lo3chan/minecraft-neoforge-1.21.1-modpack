package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class MintIceCreamPriZaviershieniiIspolzovaniiaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.clearFire();
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(BornInChaosV1ModMobEffects.INFERNAL_FLAME);
         }
      }
   }
}
