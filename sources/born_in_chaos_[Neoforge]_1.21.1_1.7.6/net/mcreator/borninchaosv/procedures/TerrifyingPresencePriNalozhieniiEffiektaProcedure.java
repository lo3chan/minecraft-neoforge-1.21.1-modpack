package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.NightmareStalkerEntity;
import net.minecraft.world.entity.Entity;

public class TerrifyingPresencePriNalozhieniiEffiektaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof NightmareStalkerEntity) {
            ((NightmareStalkerEntity)entity).setAnimation("rage");
         }
      }
   }
}
