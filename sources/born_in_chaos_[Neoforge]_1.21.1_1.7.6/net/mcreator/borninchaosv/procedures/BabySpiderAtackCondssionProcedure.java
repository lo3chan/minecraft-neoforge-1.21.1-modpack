package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.entity.Entity;

public class BabySpiderAtackCondssionProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : !entity.getPersistentData().getBoolean("attack_target");
   }
}
