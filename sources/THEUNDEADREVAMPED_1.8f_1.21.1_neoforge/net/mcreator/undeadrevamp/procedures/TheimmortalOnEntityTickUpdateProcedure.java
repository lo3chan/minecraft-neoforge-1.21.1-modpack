package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.TheimmortalEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class TheimmortalOnEntityTickUpdateProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity.getPersistentData().getDouble("decored") == 0.0) {
            if (entity.isOnFire()) {
               entity.getPersistentData().putDouble("burned", 1.0);
            }

            if (entity.getPersistentData().getDouble("burned") == 1.0) {
               if (entity instanceof TheimmortalEntity animatable) {
                  animatable.setTexture("burnedimt");
               }

               if (entity.isAlive() && entity instanceof LivingEntity _entity) {
                  _entity.setHealth(1.0F);
               }
            }
         }

         if (entity.getPersistentData().getDouble("decored") == 1.0 && entity instanceof TheimmortalEntity animatable) {
            animatable.setTexture("ceremonialimmortal");
         }
      }
   }
}
