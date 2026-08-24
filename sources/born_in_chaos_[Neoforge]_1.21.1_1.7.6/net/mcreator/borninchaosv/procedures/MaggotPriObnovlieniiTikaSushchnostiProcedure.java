package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.MaggotEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class MaggotPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _livEnt0
            && _livEnt0.hasEffect(MobEffects.DAMAGE_RESISTANCE)
            && entity instanceof LivingEntity _livEnt1
            && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK)) {
            if (entity.getPersistentData().getDouble("appearancem") == 0.0) {
               entity.getPersistentData().putDouble("appearancem", 1.0);
            } else {
               entity.getPersistentData().putDouble("appearancem", entity.getPersistentData().getDouble("appearancem") - 1.0);
            }

            if (entity.getPersistentData().getDouble("appearancem") == 0.0) {
               if (entity instanceof MaggotEntity) {
                  ((MaggotEntity)entity).setAnimation("appearance");
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK);
               }
            }
         }
      }
   }
}
