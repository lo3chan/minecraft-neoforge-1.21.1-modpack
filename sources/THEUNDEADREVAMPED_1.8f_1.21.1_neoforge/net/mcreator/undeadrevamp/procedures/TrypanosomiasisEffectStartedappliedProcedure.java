package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class TrypanosomiasisEffectStartedappliedProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
         }

         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20, 0));
         }

         if (entity instanceof LivingEntity _livEnt2 && _livEnt2.isSleeping()) {
            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(MobEffects.DARKNESS);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(UndeadRevamp2ModMobEffects.TRYPANOSOMIASIS);
            }
         }
      }
   }
}
