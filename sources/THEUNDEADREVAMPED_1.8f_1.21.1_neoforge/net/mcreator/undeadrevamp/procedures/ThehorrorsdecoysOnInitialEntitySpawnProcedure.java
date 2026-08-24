package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class ThehorrorsdecoysOnInitialEntitySpawnProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putDouble("decoyticks", 350.0);
      }
   }
}
