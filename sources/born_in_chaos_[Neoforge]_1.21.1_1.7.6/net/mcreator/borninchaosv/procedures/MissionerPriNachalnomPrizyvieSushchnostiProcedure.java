package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

public class MissionerPriNachalnomPrizyvieSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!world.getBlockState(BlockPos.containing(x + 2.0, y, z)).canOcclude()
            && !world.getBlockState(BlockPos.containing(x + 2.0, y + 1.0, z)).canOcclude()) {
            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = EntityType.ZOMBIE_VILLAGER.spawn(_level, BlockPos.containing(x + 2.0, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIMLARG.get(), x + 2.0, y, z, 8, 0.2, 0.4, 0.2, 0.1);
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(ParticleTypes.POOF, x + 2.0, y, z, 8, 0.2, 0.4, 0.2, 0.1);
            }
         }

         if (!world.getBlockState(BlockPos.containing(x - 2.0, y, z)).canOcclude()
            && !world.getBlockState(BlockPos.containing(x - 2.0, y + 1.0, z)).canOcclude()) {
            if (world instanceof ServerLevel _levelx) {
               Entity entityToSpawn = EntityType.ZOMBIE_VILLAGER.spawn(_levelx, BlockPos.containing(x - 2.0, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIMLARG.get(), x - 2.0, y, z, 8, 0.2, 0.4, 0.2, 0.1);
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles(ParticleTypes.POOF, x - 2.0, y, z, 8, 0.2, 0.4, 0.2, 0.1);
            }
         }

         entity.getPersistentData().putBoolean("callforrain", true);
      }
   }
}
