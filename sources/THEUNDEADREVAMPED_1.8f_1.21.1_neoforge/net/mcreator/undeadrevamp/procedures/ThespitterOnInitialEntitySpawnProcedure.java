package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThespitterEntity;
import net.minecraft.world.entity.Entity;

public class ThespitterOnInitialEntitySpawnProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (Math.random() < 0.5 && entity instanceof ThespitterEntity animatable) {
            animatable.setTexture("spitterblack");
         }

         entity.getPersistentData().putDouble("spitter_hid", 0.0);
      }
   }
}
