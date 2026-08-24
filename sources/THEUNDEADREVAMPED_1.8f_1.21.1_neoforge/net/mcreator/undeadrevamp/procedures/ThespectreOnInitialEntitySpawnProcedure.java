package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThespectreEntity;
import net.minecraft.world.entity.Entity;

public class ThespectreOnInitialEntitySpawnProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (Math.random() < 0.1) {
            entity.getPersistentData().putBoolean("coldarm", true);
            if (entity instanceof ThespectreEntity animatable) {
               animatable.setTexture("spectrestrongervariant");
            }
         } else {
            entity.getPersistentData().putBoolean("coldarm", false);
         }

         entity.getPersistentData().putBoolean("brokedone", false);
      }
   }
}
