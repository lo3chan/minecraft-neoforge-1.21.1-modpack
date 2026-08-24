package net.mcreator.borninchaosv.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class RestlessSpiritPriObnovlieniiTaktaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1.0, z))) {
            if (world instanceof Level _lvl1
               && _lvl1.isDay()
               && !world.getLevelData().isRaining()
               && !world.getLevelData().isThundering()
               && !entity.isInWaterRainOrBubble()
               && !entity.isOnFire()
               && !world.isClientSide()) {
               entity.igniteForSeconds(5.0F);
            }

            if (entity.isInWaterRainOrBubble()) {
               entity.clearFire();
            }
         }
      }
   }
}
