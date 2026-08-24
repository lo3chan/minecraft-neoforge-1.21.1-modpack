package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.configuration.MobsabilityConfiguration;
import net.mcreator.undeadrevamp.entity.ThepregnantEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

public class ThepregnantspawnsProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putDouble("honeyman_a", 0.0);
         entity.getPersistentData().putDouble("honeyman_b", 0.0);
         entity.getPersistentData().putDouble("honeyman_c", 0.0);
         entity.getPersistentData().putDouble("pukeshut", 0.0);
         entity.getPersistentData().putDouble("babies", (Double)MobsabilityConfiguration.PREG_BABE.get());
         if (Math.random() < 0.2) {
            if (entity instanceof ThepregnantEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(ThepregnantEntity.DATA_male, 1);
            }

            if (entity instanceof ThepregnantEntity animatable) {
               animatable.setTexture("thepregnantex");
            }
         } else {
            if (entity instanceof ThepregnantEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(ThepregnantEntity.DATA_male, 0);
            }

            if (entity instanceof ThepregnantEntity animatable) {
               animatable.setTexture("thepregnant");
            }
         }

         if (world instanceof ServerLevel _level) {
            Entity entityToSpawn = EntityType.ZOMBIE.spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
            }
         }

         if (world instanceof ServerLevel _levelx) {
            Entity entityToSpawn = EntityType.ZOMBIE.spawn(_levelx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
            }
         }

         if (world instanceof ServerLevel _levelxx) {
            Entity entityToSpawn = EntityType.ZOMBIE.spawn(_levelxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
            }
         }

         if (world instanceof ServerLevel _levelxxx) {
            Entity entityToSpawn = EntityType.ZOMBIE.spawn(_levelxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
            }
         }
      }
   }
}
