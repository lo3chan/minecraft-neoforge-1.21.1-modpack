package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.entity.Entity;

public class SailingconditionProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : entity.isInWaterOrBubble();
   }
}
