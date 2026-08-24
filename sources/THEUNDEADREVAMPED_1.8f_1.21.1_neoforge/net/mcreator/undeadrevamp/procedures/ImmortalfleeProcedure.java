package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class ImmortalfleeProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : entity.getPersistentData().getDouble("flee") == 1.0;
   }
}
