package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BabySpiderControlledPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.POISON);
         }

         if (entity.getPersistentData().getBoolean("attack_target")) {
            if (entity.getPersistentData().getDouble("target") == 0.0) {
               entity.getPersistentData().putDouble("target", 10.0);
            } else {
               entity.getPersistentData().putDouble("target", entity.getPersistentData().getDouble("target") - 1.0);
            }

            if (entity.getPersistentData().getDouble("target") == 0.0) {
               entity.getPersistentData().putBoolean("attack_target", false);
            }
         }
      }
   }
}
