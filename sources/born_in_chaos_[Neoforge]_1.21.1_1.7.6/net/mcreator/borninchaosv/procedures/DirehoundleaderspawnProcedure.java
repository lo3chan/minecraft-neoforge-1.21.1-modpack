package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class DirehoundleaderspawnProcedure {
   public static void execute(LevelAccessor world, double y, Entity entity) {
      if (entity != null) {
         if (!world.getBlockState(BlockPos.containing(entity.getX() - 2.0, y, entity.getZ() + 0.5)).canOcclude()
            || world.getBlockState(BlockPos.containing(entity.getX() - 2.0, y, entity.getZ() + 0.5)).getBlock() == Blocks.SNOW) {
            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DREAD_HOUND.get())
                  .spawn(_level, BlockPos.containing(entity.getX() - 2.0, y, entity.getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(entity.getYRot());
                  entityToSpawn.setYBodyRot(entity.getYRot());
                  entityToSpawn.setYHeadRot(entity.getYRot());
                  entityToSpawn.setXRot(entity.getXRot());
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(ParticleTypes.POOF, entity.getX() - 2.0, y, entity.getZ() + 0.5, 5, 0.3, 0.3, 0.3, 0.1);
            }
         }

         if (!world.getBlockState(BlockPos.containing(entity.getX() + 2.0, y, entity.getZ() + 0.5)).canOcclude()
            || world.getBlockState(BlockPos.containing(entity.getX() + 2.0, y, entity.getZ() + 0.5)).getBlock() == Blocks.SNOW) {
            if (world instanceof ServerLevel _levelx) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DREAD_HOUND.get())
                  .spawn(_levelx, BlockPos.containing(entity.getX() + 2.0, y, entity.getZ() + 0.5), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(entity.getYRot());
                  entityToSpawn.setYBodyRot(entity.getYRot());
                  entityToSpawn.setYHeadRot(entity.getYRot());
                  entityToSpawn.setXRot(entity.getXRot());
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles(ParticleTypes.POOF, entity.getX() + 2.0, y, entity.getZ() + 0.5, 5, 0.3, 0.3, 0.3, 0.1);
            }
         }
      }
   }
}
