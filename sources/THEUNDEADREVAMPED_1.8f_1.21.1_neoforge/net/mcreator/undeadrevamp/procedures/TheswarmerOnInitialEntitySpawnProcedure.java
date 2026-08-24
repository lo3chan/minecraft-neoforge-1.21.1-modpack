package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class TheswarmerOnInitialEntitySpawnProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putDouble("honeyman_a", 0.0);
         entity.getPersistentData().putDouble("honeyman_b", 0.0);
         entity.getPersistentData().putDouble("honeyman_c", 0.0);
      }
   }
}
