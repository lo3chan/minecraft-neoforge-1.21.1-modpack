package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

public class TheimmortalDeathTimeIsReachedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.getPersistentData().getDouble("decored") != 1.0
            && Math.random() < 0.8
            && entity.getPersistentData().getDouble("burned") != 1.0
            && entity.getPersistentData().getDouble("nore") == 0.0
            && world instanceof ServerLevel _level) {
            Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.INVISIIMMORTAL.get())
               .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
            }
         }

         if (entity.getPersistentData().getDouble("burned") == 1.0 && world instanceof ServerLevel _levelx) {
            _levelx.addFreshEntity(new ExperienceOrb(_levelx, x, y, z, 5));
         }

         if (entity.getPersistentData().getDouble("decored") == 1.0 && world instanceof ServerLevel _levelx) {
            _levelx.addFreshEntity(new ExperienceOrb(_levelx, x, y, z, 10));
         }
      }
   }
}
