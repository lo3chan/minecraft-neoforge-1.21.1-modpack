package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.KrampusHenchmanEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class KrampusHenchmanPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(BornInChaosV1ModMobEffects.BONE_CHILLING);
         }

         if (entity instanceof LivingEntity _livEnt1
            && _livEnt1.hasEffect(MobEffects.DAMAGE_RESISTANCE)
            && entity instanceof LivingEntity _livEnt2
            && _livEnt2.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK)) {
            if (entity.getPersistentData().getDouble("appearance") == 0.0) {
               entity.getPersistentData().putDouble("appearance", 1.0);
            } else {
               entity.getPersistentData().putDouble("appearance", entity.getPersistentData().getDouble("appearance") - 1.0);
            }

            if (entity.getPersistentData().getDouble("appearance") == 0.0) {
               if (entity instanceof KrampusHenchmanEntity) {
                  ((KrampusHenchmanEntity)entity).setAnimation("appearance");
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
