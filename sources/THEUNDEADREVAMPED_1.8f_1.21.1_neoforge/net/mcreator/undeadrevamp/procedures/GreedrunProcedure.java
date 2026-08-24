package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class GreedrunProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : entity.getPersistentData().getDouble("decored") == 1.0;
   }
}
