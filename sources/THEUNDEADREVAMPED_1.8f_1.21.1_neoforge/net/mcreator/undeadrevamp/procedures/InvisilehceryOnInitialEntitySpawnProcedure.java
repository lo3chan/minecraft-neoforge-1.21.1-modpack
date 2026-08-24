package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class InvisilehceryOnInitialEntitySpawnProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putBoolean("choosen", false);
         entity.getPersistentData().putBoolean("noatk", false);
         entity.getPersistentData().putDouble("flee", 1.0);
         entity.getPersistentData().putDouble("rare", 0.0);
         UndeadRevamp2Mod.queueServerWork(50, () -> entity.getPersistentData().putDouble("flee", 0.0));
      }
   }
}
