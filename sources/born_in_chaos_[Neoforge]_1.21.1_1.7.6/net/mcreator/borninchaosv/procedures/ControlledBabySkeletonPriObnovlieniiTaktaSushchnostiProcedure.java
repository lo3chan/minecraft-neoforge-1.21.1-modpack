package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

public class ControlledBabySkeletonPriObnovlieniiTaktaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(MobEffects.DAMAGE_RESISTANCE))) {
            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles(ParticleTypes.POOF, x, y, z, 5, 0.5, 0.5, 0.5, 0.1);
            }
         }

         if (entity instanceof LivingEntity _livEnt3 && _livEnt3.isBaby()) {
            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_BABY_SKELETON.get())
                  .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(entity.getYRot());
                  entityToSpawn.setYBodyRot(entity.getYRot());
                  entityToSpawn.setYHeadRot(entity.getYRot());
                  entityToSpawn.setXRot(entity.getXRot());
               }
            }

            if (!entity.level().isClientSide()) {
               entity.discard();
            }
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
