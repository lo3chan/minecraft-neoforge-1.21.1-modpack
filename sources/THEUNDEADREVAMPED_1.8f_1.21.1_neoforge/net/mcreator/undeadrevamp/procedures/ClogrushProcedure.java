package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.CloggerEntity;
import net.minecraft.world.entity.Entity;

public class ClogrushProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : (entity instanceof CloggerEntity _datEntI ? (Integer)_datEntI.getEntityData().get(CloggerEntity.DATA_eating) : 0) == 1;
   }
}
