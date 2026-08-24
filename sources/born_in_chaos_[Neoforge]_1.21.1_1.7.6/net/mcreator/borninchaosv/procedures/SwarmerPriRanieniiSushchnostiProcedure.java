package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

public class SwarmerPriRanieniiSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLI.get(), x, y + 1.5, z, 5, 0.5, 0.5, 0.5, 0.1);
         }

         if (!(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK))
            && Math.random() < 0.8
            && !entity.isOnFire()) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 80, 0, false, false));
            }

            if (!world.getBlockState(BlockPos.containing(x + 1.0, y, z)).canOcclude()
               && !world.getBlockState(BlockPos.containing(x + 1.0, y + 1.0, z)).canOcclude()) {
               if (world instanceof ServerLevel _level) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
                     .spawn(_level, BlockPos.containing(x + 1.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                  }
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles(ParticleTypes.POOF, x + 1.5, y + 1.0, z + 0.5, 4, 0.3, 0.3, 0.3, 0.1);
               }
            } else if (!world.getBlockState(BlockPos.containing(x - 1.0, y, z)).canOcclude()
               && !world.getBlockState(BlockPos.containing(x - 1.0, y + 1.0, z)).canOcclude()) {
               if (world instanceof ServerLevel _levelx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
                     .spawn(_levelx, BlockPos.containing(x - 1.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                  }
               }

               if (world instanceof ServerLevel _levelxx) {
                  _levelxx.sendParticles(ParticleTypes.POOF, x - 1.5, y + 1.0, z + 0.5, 4, 0.3, 0.3, 0.3, 0.1);
               }
            } else if (!world.getBlockState(BlockPos.containing(x, y, z - 1.0)).canOcclude()
               && !world.getBlockState(BlockPos.containing(x, y + 1.0, z - 1.0)).canOcclude()) {
               if (world instanceof ServerLevel _levelxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
                     .spawn(_levelxx, BlockPos.containing(x + 0.5, y, z - 1.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                  }
               }

               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 1.0, z - 1.5, 4, 0.3, 0.3, 0.3, 0.1);
               }
            } else if (!world.getBlockState(BlockPos.containing(x, y, z + 1.0)).canOcclude()
               && !world.getBlockState(BlockPos.containing(x, y + 1.0, z + 1.0)).canOcclude()) {
               if (world instanceof ServerLevel _levelxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
                     .spawn(_levelxxx, BlockPos.containing(x + 0.5, y, z + 1.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                  }
               }

               if (world instanceof ServerLevel _levelxxxx) {
                  _levelxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 1.0, z + 1.5, 4, 0.3, 0.3, 0.3, 0.1);
               }
            }
         }
      }
   }
}
