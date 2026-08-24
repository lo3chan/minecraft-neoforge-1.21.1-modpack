package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class SomnolencediesProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         UndeadRevamp2Mod.queueServerWork(5, () -> {
            entity.setDeltaMovement(new Vec3(0.0, -2.0, 0.0));
            entity.getPersistentData().putBoolean("fall", true);
         });
      }
   }
}
