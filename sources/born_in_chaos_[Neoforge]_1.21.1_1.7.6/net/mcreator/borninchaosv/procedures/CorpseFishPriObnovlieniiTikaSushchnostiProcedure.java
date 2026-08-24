package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CorpseFishPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (!entity.isInWater() && !(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.FISH_BREATH))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.FISH_BREATH, 300, 0, false, false));
            }
         } else if (entity.isInWater()) {
            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(MobEffects.WITHER);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.FISH_BREATH);
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 40, 0, false, false));
            }
         }

         if ((
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.FISH_BREATH)
                     ? _livEnt.getEffect(BornInChaosV1ModMobEffects.FISH_BREATH).getDuration()
                     : 0
               )
               == 20
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 300, 1, false, false));
         }
      }
   }
}
