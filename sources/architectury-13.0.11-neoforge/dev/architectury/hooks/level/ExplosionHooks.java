package dev.architectury.hooks.level;

import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

@Deprecated(
   forRemoval = true
)
public final class ExplosionHooks {
   private ExplosionHooks() {
   }

   @Deprecated(
      forRemoval = true
   )
   public static Vec3 getPosition(Explosion explosion) {
      return explosion.center();
   }
}
