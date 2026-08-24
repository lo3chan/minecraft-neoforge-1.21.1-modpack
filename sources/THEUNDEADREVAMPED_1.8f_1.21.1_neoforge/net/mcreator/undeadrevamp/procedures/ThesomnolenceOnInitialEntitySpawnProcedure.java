package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

public class ThesomnolenceOnInitialEntitySpawnProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
            .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
         }
      }
   }
}
