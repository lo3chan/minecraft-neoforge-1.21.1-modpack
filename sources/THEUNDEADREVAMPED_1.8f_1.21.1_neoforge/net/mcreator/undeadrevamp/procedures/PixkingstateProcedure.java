package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class PixkingstateProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : entity.getPersistentData().getDouble("fleepick") == 1.0;
   }
}
