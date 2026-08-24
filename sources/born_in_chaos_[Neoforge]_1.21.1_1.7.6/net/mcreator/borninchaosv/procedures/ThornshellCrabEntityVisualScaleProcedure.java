package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ThornshellCrabEntityVisualScaleProcedure {
   public static double execute(Entity entity) {
      if (entity == null) {
         return 0.0;
      } else {
         double visual_size_child = 0.0;
         double visual_size = 0.0;
         visual_size = 1.0;
         visual_size_child = 0.6;
         return entity instanceof LivingEntity _livEnt0 && _livEnt0.isBaby() ? visual_size_child : visual_size;
      }
   }
}
