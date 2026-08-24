package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.BomberEntity;
import net.minecraft.world.entity.Entity;

public class BomberexplodingEffectExpiresProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (entity instanceof BomberEntity animatable) {
            animatable.setTexture("thebomber");
         }
      }
   }
}
