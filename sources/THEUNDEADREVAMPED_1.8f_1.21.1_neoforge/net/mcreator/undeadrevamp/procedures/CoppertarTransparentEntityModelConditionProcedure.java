package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class CoppertarTransparentEntityModelConditionProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : entity.isAlive();
   }
}
