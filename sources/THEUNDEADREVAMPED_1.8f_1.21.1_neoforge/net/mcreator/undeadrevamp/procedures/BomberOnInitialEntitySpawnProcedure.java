package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class BomberOnInitialEntitySpawnProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putDouble("gaszz_spead", 125.0);
         entity.getPersistentData().putDouble("gaszz_sped", 0.0);
      }
   }
}
