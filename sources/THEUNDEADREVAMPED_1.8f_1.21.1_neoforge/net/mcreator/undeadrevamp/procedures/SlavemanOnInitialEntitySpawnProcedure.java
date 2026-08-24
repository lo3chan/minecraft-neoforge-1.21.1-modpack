package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class SlavemanOnInitialEntitySpawnProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (Math.random() < 0.2 && world.getBlockState(BlockPos.containing(x, y + 1.0, z)).getBlock() == Blocks.AIR && world instanceof ServerLevel _level) {
         Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THEHUNTER.get()).spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (world instanceof ServerLevel _levelx) {
         Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THEHUNTER.get()).spawn(_levelx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (world instanceof ServerLevel _levelxx) {
         Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THEHUNTER.get()).spawn(_levelxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }

      if (world instanceof ServerLevel _levelxxx) {
         Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THEHUNTER.get())
            .spawn(_levelxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }
   }
}
