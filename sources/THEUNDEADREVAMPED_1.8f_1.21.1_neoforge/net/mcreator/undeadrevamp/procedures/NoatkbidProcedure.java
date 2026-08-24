package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class NoatkbidProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : !entity.getPersistentData().getBoolean("noatk") && entity.getPersistentData().getDouble("eating") == 0.0;
   }
}
