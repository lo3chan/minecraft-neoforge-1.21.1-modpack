package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class INVISIBLEBIDYOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!entity.getPersistentData().getBoolean("choosen")) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiterator) {
                  if (world instanceof ServerLevel _level) {
                     Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.PROPBALL_1.get())
                        .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                     }
                  }

                  if (world instanceof ServerLevel _levelx) {
                     Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.PROPBALL_1.get())
                        .spawn(_levelx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                     }
                  }

                  if (world instanceof ServerLevel _levelxx) {
                     Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.PROPBALL_1.get())
                        .spawn(_levelxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                     }
                  }

                  if (world instanceof ServerLevel _levelxxx) {
                     Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.PROPBALL_1.get())
                        .spawn(_levelxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                     }
                  }

                  if (world instanceof ServerLevel _levelxxxx) {
                     Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.PROPBALL_1.get())
                        .spawn(_levelxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                     }
                  }

                  if (world instanceof ServerLevel _levelxxxxx) {
                     Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.PROPBALL_1.get())
                        .spawn(_levelxxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                     }
                  }

                  if (world instanceof ServerLevel _levelxxxxxx) {
                     Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.PROPBALL_1.get())
                        .spawn(_levelxxxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                     }
                  }

                  if (world instanceof ServerLevel _levelxxxxxxx) {
                     Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.PROPBALL_1.get())
                        .spawn(_levelxxxxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                     }
                  }

                  if (world instanceof ServerLevel _levelxxxxxxxx) {
                     Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.PROPBALL_1.get())
                        .spawn(_levelxxxxxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                     }
                  }

                  if (world instanceof ServerLevel _levelxxxxxxxxx) {
                     Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.PROPBALL_1.get())
                        .spawn(_levelxxxxxxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                     }
                  }

                  entity.getPersistentData().putBoolean("choosen", true);
                  entity.getPersistentData().putBoolean("noatk", true);
               }
            }
         }

         if (entity.getPersistentData().getBoolean("choosen")) {
            UndeadRevamp2Mod.queueServerWork(
               100,
               () -> {
                  if (entity.getPersistentData().getBoolean("choosen")) {
                     if (!entity.level().isClientSide()) {
                        entity.discard();
                     }

                     if (world instanceof ServerLevel _levelxxxxxxxxxx) {
                        Entity entityToSpawnx = ((EntityType)UndeadRevamp2ModEntities.CLOGGER.get())
                           .spawn(_levelxxxxxxxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawnx != null) {
                           entityToSpawnx.setDeltaMovement(0.0, 0.0, 0.0);
                        }
                     }

                     entity.getPersistentData().putBoolean("choosen", false);
                  }
               }
            );
         }
      }
   }
}
