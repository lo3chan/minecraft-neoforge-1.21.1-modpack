package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThedungeonEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NeocrorinesNaturalEntitySpawningConditionProcedure {
   public static boolean execute(LevelAccessor world, double x, double y, double z) {
      boolean found = false;
      double sx = 0.0;
      double sy = 0.0;
      double sz = 0.0;
      sx = -3.0;
      found = false;

      for (int index0 = 0; index0 < 6; index0++) {
         sy = -3.0;

         for (int index1 = 0; index1 < 6; index1++) {
            sz = -3.0;

            for (int index2 = 0; index2 < 6; index2++) {
               if (world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock() == UndeadRevamp2ModBlocks.CHISELED_DRIPSTONEPILLAR.get()
                  || !world.getEntitiesOfClass(ThedungeonEntity.class, AABB.ofSize(new Vec3(x, y, z), 120.0, 120.0, 120.0), e -> true).isEmpty()) {
                  found = true;
               }

               sz++;
            }

            sy++;
         }

         sx++;
      }

      return found;
   }
}
