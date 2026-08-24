package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class TheheavyOnInitialEntitySpawnProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putDouble("passorsmash", 0.0);
         entity.getPersistentData().putDouble("pokemode", 0.0);
         entity.getPersistentData().putDouble("pastat", 1.0);
         entity.getPersistentData().putDouble("rage", 0.0);
         entity.getPersistentData().putDouble("roar", 0.0);
         entity.getPersistentData().putDouble("BLOCKIN", 0.0);
         entity.getPersistentData().putDouble("stone", 0.0);
         entity.getPersistentData().putDouble("fleepick", 0.0);
         entity.getPersistentData().putDouble("throw", 0.0);
         entity.getPersistentData().putDouble("honeyman_a", 0.0);
         entity.getPersistentData().putDouble("honeyman_b", 0.0);
         entity.getPersistentData().putDouble("r_range", 0.0);
         entity.getPersistentData().putDouble("capped", 0.0);
      }
   }
}
