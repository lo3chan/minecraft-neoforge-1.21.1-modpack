package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SleepwalkingOnEffectActiveTickProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _livEnt0 && _livEnt0.isSleeping()) {
            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(UndeadRevamp2ModMobEffects.SLEEPWALKING);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(UndeadRevamp2ModMobEffects.TRYPANOSOMIASIS);
            }
         }
      }
   }
}
