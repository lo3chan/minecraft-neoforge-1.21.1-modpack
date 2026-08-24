package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class FlameOnInitialEntitySpawnProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         UndeadRevamp2Mod.queueServerWork(450, () -> {
            if (!entity.level().isClientSide()) {
               entity.discard();
            }
         });
      }
   }
}
