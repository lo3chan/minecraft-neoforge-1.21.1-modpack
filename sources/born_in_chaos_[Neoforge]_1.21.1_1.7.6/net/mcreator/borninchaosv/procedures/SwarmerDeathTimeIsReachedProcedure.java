package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

public class SwarmerDeathTimeIsReachedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLESHSPLASH.get(), x, y + 1.0, z, 12, 1.0, 1.0, 1.0, 0.1);
      }

      if (world instanceof ServerLevel _level) {
         _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPLASHOFFLESH.get(), x, y + 0.5, z, 10, 0.5, 0.5, 0.5, 0.5);
      }

      if (world instanceof ServerLevel _level) {
         _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLI.get(), x, y + 1.0, z, 14, 0.7, 0.7, 0.7, 0.1);
      }

      if (world instanceof ServerLevel _level) {
         _level.sendParticles(ParticleTypes.POOF, x, y + 1.0, z, 8, 0.5, 0.5, 0.5, 0.1);
      }

      if (world instanceof ServerLevel _level) {
         Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
            .spawn(_level, BlockPos.containing(x + 0.7, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (world instanceof ServerLevel _levelx) {
         Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
            .spawn(_levelx, BlockPos.containing(x - 0.7, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (world instanceof ServerLevel _levelxx) {
         Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
            .spawn(_levelxx, BlockPos.containing(x + 0.5, y, z + 0.7), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (world instanceof ServerLevel _levelxxx) {
         Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MAGGOT.get())
            .spawn(_levelxxx, BlockPos.containing(x + 0.7, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (world instanceof ServerLevel _levelxxxx) {
         Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MAGGOT.get())
            .spawn(_levelxxxx, BlockPos.containing(x + 0.5, y + 1.0, z + 0.7), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (Math.random() < 0.6 && world instanceof ServerLevel _levelxxxxx) {
         Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MAGGOT.get())
            .spawn(_levelxxxxx, BlockPos.containing(x - 0.7, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (Math.random() < 0.4 && world instanceof ServerLevel _levelxxxxxx) {
         Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
            .spawn(_levelxxxxxx, BlockPos.containing(x + 0.5, y, z - 0.7), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (Math.random() < 0.3) {
         if (world instanceof ServerLevel _levelxxxxxxx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CORPSE_FLY.get())
               .spawn(_levelxxxxxxx, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }
      } else if (Math.random() < 0.3 && world instanceof ServerLevel _levelxxxxxxxx) {
         Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.BLOODY_GADFLY.get())
            .spawn(_levelxxxxxxxx, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }
   }
}
