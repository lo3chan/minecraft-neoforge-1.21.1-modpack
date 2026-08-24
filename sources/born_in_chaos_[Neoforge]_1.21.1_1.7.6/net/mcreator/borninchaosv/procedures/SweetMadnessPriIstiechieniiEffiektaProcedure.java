package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.entity.Entity;

public class SweetMadnessPriIstiechieniiEffiektaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putBoolean("protection", false);
         entity.getPersistentData().putBoolean("treatment", false);
         entity.getPersistentData().putBoolean("energy", false);
         entity.getPersistentData().putBoolean("power", false);
         entity.getPersistentData().putBoolean("hot", false);
         entity.getPersistentData().putBoolean("creepy", false);
         entity.getPersistentData().putBoolean("queasiness", false);
      }
   }
}
