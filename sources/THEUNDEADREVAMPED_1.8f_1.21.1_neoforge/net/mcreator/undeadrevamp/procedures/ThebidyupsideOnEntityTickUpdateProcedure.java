package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class ThebidyupsideOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.getPersistentData().getBoolean("upside") && !world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
            entity.setDeltaMovement(new Vec3(0.0, 500.0, 0.0));
         }
      }
   }
}
