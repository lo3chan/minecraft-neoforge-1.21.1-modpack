package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThehunterEntity;
import net.minecraft.world.entity.Entity;

public class ThehunterOnInitialEntitySpawnProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putDouble("gaszz_spead", 0.0);
         entity.getPersistentData().putDouble("gaszz_sped", 0.0);
         if (Math.random() < 0.2) {
            if (entity instanceof ThehunterEntity animatable) {
               animatable.setTexture("leveltwothehunter");
            }

            entity.getPersistentData().putDouble("horned", 1.0);
         } else if (Math.random() < 0.12) {
            entity.getPersistentData().putDouble("horned", 2.0);
            if (entity instanceof ThehunterEntity animatable) {
               animatable.setTexture("levelthreethehunter");
            }
         }
      }
   }
}
