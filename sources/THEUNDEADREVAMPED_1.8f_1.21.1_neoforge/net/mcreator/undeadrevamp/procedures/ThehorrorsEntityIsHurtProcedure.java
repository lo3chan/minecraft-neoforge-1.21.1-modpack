package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ThehorrorsEntityIsHurtProcedure {
   public static void execute(Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof LivingEntity && !(10.0F > (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F))) {
            entity.setDeltaMovement(
               new Vec3(
                  Math.sin(Math.toRadians(sourceentity.getYRot() + 180.0F)) * 2.5 * 1.1,
                  (Math.sin(Math.toRadians(0.0F - sourceentity.getXRot())) + 0.5) * 1.12,
                  Math.cos(Math.toRadians(sourceentity.getYRot())) * 1.25 * -1.3
               )
            );
         }
      }
   }
}
