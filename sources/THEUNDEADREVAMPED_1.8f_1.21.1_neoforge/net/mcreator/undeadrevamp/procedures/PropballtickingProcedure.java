package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class PropballtickingProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity.isInWaterRainOrBubble() && !entity.level().isClientSide()) {
            entity.discard();
         }
      }
   }
}
