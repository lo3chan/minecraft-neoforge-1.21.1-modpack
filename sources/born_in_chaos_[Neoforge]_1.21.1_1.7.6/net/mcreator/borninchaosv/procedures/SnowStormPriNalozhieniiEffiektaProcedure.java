package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.KrampusEntity;
import net.minecraft.world.entity.Entity;

public class SnowStormPriNalozhieniiEffiektaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof KrampusEntity) {
            ((KrampusEntity)entity).setAnimation("rage");
         }
      }
   }
}
