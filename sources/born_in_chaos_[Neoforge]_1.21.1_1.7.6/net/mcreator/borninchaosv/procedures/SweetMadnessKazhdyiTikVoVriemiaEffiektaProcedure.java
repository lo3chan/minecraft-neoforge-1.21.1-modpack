package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SweetMadnessKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity.getPersistentData().getBoolean("protection")) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 0));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.WITHER_RESISTANCE, 100, 0));
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.SWEET_MADNESS);
            }
         } else if (entity.getPersistentData().getBoolean("treatment")) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30, 0));
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.setHealth((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) + 2.0F);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.SWEET_MADNESS);
            }
         } else if (entity.getPersistentData().getBoolean("energy")) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.STIMULATION, 100, 1));
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.SWEET_MADNESS);
            }
         } else if (entity.getPersistentData().getBoolean("power")) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.LIGHT_RAMPAGE, 100, 0));
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.SWEET_MADNESS);
            }
         } else if (entity.getPersistentData().getBoolean("hot")) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.INSECT_PROTECTION, 100, 0));
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.SWEET_MADNESS);
            }
         } else if (entity.getPersistentData().getBoolean("creepy")) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.VAMPIRIC_TOUCH, 100, 0));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_BARRIER, 100, 0));
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.SWEET_MADNESS);
            }
         } else if (entity.getPersistentData().getBoolean("queasiness")) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0, false, false));
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.SWEET_MADNESS);
            }
         }
      }
   }
}
