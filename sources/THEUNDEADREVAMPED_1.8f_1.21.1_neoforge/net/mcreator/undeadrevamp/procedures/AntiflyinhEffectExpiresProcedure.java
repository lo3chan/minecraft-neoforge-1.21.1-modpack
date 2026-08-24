package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class AntiflyinhEffectExpiresProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.setDeltaMovement(new Vec3(0.0, 0.0, 0.0));
      }
   }
}
