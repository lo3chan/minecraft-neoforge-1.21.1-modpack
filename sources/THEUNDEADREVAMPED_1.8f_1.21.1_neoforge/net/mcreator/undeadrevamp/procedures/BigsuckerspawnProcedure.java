package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.configuration.MobsabilityConfiguration;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

public class BigsuckerspawnProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putBoolean("fall", false);

         for (int index0 = 0; index0 < (int)((Double)MobsabilityConfiguration.SUCK_MAIN.get()).doubleValue(); index0++) {
            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.SUCKER.get())
                  .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
               }
            }
         }
      }
   }
}
