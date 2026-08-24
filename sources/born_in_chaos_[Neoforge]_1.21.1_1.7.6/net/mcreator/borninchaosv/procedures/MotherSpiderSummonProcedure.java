package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class MotherSpiderSummonProcedure {
   public static void execute(LevelAccessor world, double y, Entity entity) {
      if (entity != null) {
         if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK))
            && !entity.isInLava()
            && (entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F)
               < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1.0F)) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 0, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 100, 0, false, false));
            }

            if (!world.getBlockState(BlockPos.containing(entity.getX() - 2.0, y + 1.0, entity.getZ() + 0.5)).canOcclude()
               || world.getBlockState(BlockPos.containing(entity.getX() - 2.0, y + 1.0, entity.getZ() + 0.5)).getBlock() == Blocks.SNOW) {
               if (world instanceof ServerLevel _level) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BABY_SPIDER.get())
                     .spawn(_level, BlockPos.containing(entity.getX() - 2.0, y + 1.0, entity.getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles(ParticleTypes.POOF, entity.getX() - 2.0, y + 1.0, entity.getZ() + 0.5, 3, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.WEB_SPLASH.get(),
                     entity.getX() - 2.0,
                     y + 1.0,
                     entity.getZ() + 0.5,
                     6,
                     0.3,
                     0.3,
                     0.3,
                     0.1
                  );
               }
            }

            if (!world.getBlockState(BlockPos.containing(entity.getX() + 2.0, y + 1.0, entity.getZ() + 0.5)).canOcclude()
               || world.getBlockState(BlockPos.containing(entity.getX() + 2.0, y + 1.0, entity.getZ() + 0.5)).getBlock() == Blocks.SNOW) {
               if (world instanceof ServerLevel _levelx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BABY_SPIDER.get())
                     .spawn(_levelx, BlockPos.containing(entity.getX() + 2.0, y + 1.0, entity.getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(entity.getYRot());
                     entityToSpawn.setYBodyRot(entity.getYRot());
                     entityToSpawn.setYHeadRot(entity.getYRot());
                     entityToSpawn.setXRot(entity.getXRot());
                  }
               }

               if (world instanceof ServerLevel _levelxx) {
                  _levelxx.sendParticles(ParticleTypes.POOF, entity.getX() + 2.0, y + 1.0, entity.getZ() + 0.5, 3, 0.3, 0.3, 0.3, 0.1);
               }

               if (world instanceof ServerLevel _levelxx) {
                  _levelxx.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.WEB_SPLASH.get(),
                     entity.getX() - 2.0,
                     y + 1.0,
                     entity.getZ() + 0.5,
                     6,
                     0.3,
                     0.3,
                     0.3,
                     0.1
                  );
               }
            }
         }
      }
   }
}
