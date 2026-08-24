package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.entity.Entity;

public class DarkSplashPriIstiechieniiEffiektaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putDouble("chargedsplash", 0.0);
      }
   }
}
