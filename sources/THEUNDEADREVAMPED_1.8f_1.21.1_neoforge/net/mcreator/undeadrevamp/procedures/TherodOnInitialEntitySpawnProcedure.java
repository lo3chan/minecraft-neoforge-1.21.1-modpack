package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.TherodEntity;
import net.minecraft.world.entity.Entity;

public class TherodOnInitialEntitySpawnProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof TherodEntity _datEntSetI) {
            _datEntSetI.getEntityData().set(TherodEntity.DATA_honeyman_a, 0);
         }

         if (entity instanceof TherodEntity _datEntSetI) {
            _datEntSetI.getEntityData().set(TherodEntity.DATA_honeyman_b, 0);
         }

         if (entity instanceof TherodEntity _datEntSetI) {
            _datEntSetI.getEntityData().set(TherodEntity.DATA_honeyman_c, 0);
         }

         if (entity instanceof TherodEntity _datEntSetI) {
            _datEntSetI.getEntityData().set(TherodEntity.DATA_activatehitbox, 0);
         }

         if (entity instanceof TherodEntity _datEntSetI) {
            _datEntSetI.getEntityData().set(TherodEntity.DATA_tt, 0);
         }
      }
   }
}
