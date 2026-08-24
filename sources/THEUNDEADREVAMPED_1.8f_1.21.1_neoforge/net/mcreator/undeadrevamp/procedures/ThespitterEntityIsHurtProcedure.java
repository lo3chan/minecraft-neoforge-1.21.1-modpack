package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class ThespitterEntityIsHurtProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.setShiftKeyDown(true);
      }
   }
}
