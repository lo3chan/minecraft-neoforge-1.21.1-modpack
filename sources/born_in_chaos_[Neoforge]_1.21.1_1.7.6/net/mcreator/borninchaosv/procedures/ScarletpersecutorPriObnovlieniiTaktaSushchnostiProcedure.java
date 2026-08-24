package net.mcreator.borninchaosv.procedures;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class ScarletpersecutorPriObnovlieniiTaktaSushchnostiProcedure {
   public static void execute(double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.isInWall()) {
            entity.teleportTo(x + 1.0, y + 1.0, z);
            if (entity instanceof ServerPlayer _serverPlayer) {
               _serverPlayer.connection.teleport(x + 1.0, y + 1.0, z, entity.getYRot(), entity.getXRot());
            }
         }
      }
   }
}
