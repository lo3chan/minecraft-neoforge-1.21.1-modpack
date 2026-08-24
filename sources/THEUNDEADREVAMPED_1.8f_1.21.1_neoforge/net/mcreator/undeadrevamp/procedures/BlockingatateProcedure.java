package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class BlockingatateProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : entity.getPersistentData().getDouble("BLOCKIN") == 0.0;
   }
}
