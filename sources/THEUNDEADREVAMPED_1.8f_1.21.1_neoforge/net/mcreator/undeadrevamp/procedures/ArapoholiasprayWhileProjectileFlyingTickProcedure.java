package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class ArapoholiasprayWhileProjectileFlyingTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity immediatesourceentity) {
      if (immediatesourceentity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               ParticleTypes.BUBBLE,
               x,
               y,
               z,
               3,
               immediatesourceentity.getBbWidth(),
               immediatesourceentity.getBbHeight(),
               immediatesourceentity.getBbWidth(),
               1.0E-8
            );
         }
      }
   }
}
