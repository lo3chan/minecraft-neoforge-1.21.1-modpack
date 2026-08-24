package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public class ProballtickingProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (Math.random() < 0.15) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BOMBTICKING, 15, 0, false, false));
            }
         } else if (Math.random() < 0.13) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BOMBTICKING, 20, 0, false, false));
            }
         } else if (Math.random() < 0.1) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BOMBTICKING, 25, 0, false, false));
            }
         } else if (Math.random() < 0.12) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BOMBTICKING, 30, 0, false, false));
            }
         } else if (Math.random() < 0.19) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BOMBTICKING, 30, 0, false, false));
            }
         } else if (Math.random() < 0.1) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BOMBTICKING, 35, 0, false, false));
            }
         } else if (Math.random() < 0.1) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BOMBTICKING, 40, 0, false, false));
            }
         } else if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BOMBTICKING, 70, 0, false, false));
         }

         UndeadRevamp2Mod.queueServerWork(30, () -> {
            if (entity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
               _entityx.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 30, false, false));
            }
         });
      }
   }
}
