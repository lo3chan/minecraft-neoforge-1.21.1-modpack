package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.ThebeartamerEntity;
import net.minecraft.world.entity.Entity;

public class UndeadstunsEffectStartedappliedProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof ThebeartamerEntity) {
            ((ThebeartamerEntity)entity).setAnimation("stunned");
         }
      }
   }
}
