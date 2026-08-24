package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.entity.Entity;

public class RabbitAgilityPriNalozhieniiEffiektaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putDouble("transformativesnack", 0.0);
      }
   }
}
