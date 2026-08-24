package net.mcreator.undeadrevamp.procedures;

import net.minecraft.world.entity.Entity;

public class ThelurkerOnInitialEntitySpawnProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putDouble("passorsmash", 0.0);
         entity.getPersistentData().putDouble("pokemode", 0.0);
         entity.getPersistentData().putDouble("pastat", 1.0);
         entity.getPersistentData().putDouble("rage", 0.0);
      }
   }
}
