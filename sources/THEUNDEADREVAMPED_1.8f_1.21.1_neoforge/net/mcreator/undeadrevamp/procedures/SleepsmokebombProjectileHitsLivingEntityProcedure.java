package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.SmokesmitterEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class SleepsmokebombProjectileHitsLivingEntityProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity immediatesourceentity, Entity sourceentity) {
      if (entity != null && immediatesourceentity != null && sourceentity != null) {
         if (sourceentity != entity && !(entity instanceof SmokesmitterEntity)) {
            UndeadRevamp2Mod.queueServerWork(
               20,
               () -> {
                  if (world instanceof ServerLevel _level) {
                     Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.SMOKESMITTER.get())
                        .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                     }
                  }
               }
            );
            world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(Blocks.FLOWER_POT.defaultBlockState()));
            if (!immediatesourceentity.level().isClientSide()) {
               immediatesourceentity.discard();
            }
         }
      }
   }
}
