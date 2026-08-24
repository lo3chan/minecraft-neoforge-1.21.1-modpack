package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DebuffresistanceOnEffectActiveTickProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(UndeadRevamp2ModMobEffects.ACIDDECAY);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(UndeadRevamp2ModMobEffects.GOOED);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.POISON);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.WITHER);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.WEAKNESS);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.DIG_SLOWDOWN);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(UndeadRevamp2ModMobEffects.SLEEPWALKING);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(UndeadRevamp2ModMobEffects.TRYPANOSOMIASIS);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.BLINDNESS);
         }
      }
   }
}
