package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThesmokerEntity;
import net.minecraft.world.entity.Entity;

public class ThesmokerOnInitialEntitySpawnProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof ThesmokerEntity _datEntSetI) {
            _datEntSetI.getEntityData().set(ThesmokerEntity.DATA_gas_delay, 1);
         }

         if (entity instanceof ThesmokerEntity _datEntSetI) {
            _datEntSetI.getEntityData().set(ThesmokerEntity.DATA_fume_whezeticks, -1);
         }

         if (entity instanceof ThesmokerEntity _datEntSetI) {
            _datEntSetI.getEntityData().set(ThesmokerEntity.DATA_bubblehp, 60);
         }

         if (Math.random() < 0.2) {
            if (entity instanceof ThesmokerEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(ThesmokerEntity.DATA_axe, 1);
            }

            if (entity instanceof ThesmokerEntity animatable) {
               animatable.setTexture("wheezerwithaxe");
            }
         } else if (entity instanceof ThesmokerEntity _datEntSetI) {
            _datEntSetI.getEntityData().set(ThesmokerEntity.DATA_axe, 0);
         }
      }
   }
}
