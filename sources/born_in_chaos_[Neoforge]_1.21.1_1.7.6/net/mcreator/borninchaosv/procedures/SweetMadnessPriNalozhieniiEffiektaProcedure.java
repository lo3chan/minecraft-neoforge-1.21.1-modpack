package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.entity.Entity;

public class SweetMadnessPriNalozhieniiEffiektaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (Math.random() < 0.1) {
            entity.getPersistentData().putBoolean("queasiness", true);
         } else if (Math.random() < 0.15) {
            entity.getPersistentData().putBoolean("creepy", true);
         } else if (Math.random() < 0.2) {
            entity.getPersistentData().putBoolean("hot", true);
         } else if (Math.random() < 0.25) {
            entity.getPersistentData().putBoolean("power", true);
         } else if (Math.random() < 0.26) {
            entity.getPersistentData().putBoolean("energy", true);
         } else if (Math.random() < 0.3) {
            entity.getPersistentData().putBoolean("treatment", true);
         } else if (Math.random() < 0.3) {
            entity.getPersistentData().putBoolean("protection", true);
         }
      }
   }
}
