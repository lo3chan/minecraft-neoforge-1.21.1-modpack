package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class WitherballWhileProjectileFlyingTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity immediatesourceentity) {
      if (immediatesourceentity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               ParticleTypes.SMOKE,
               x,
               y,
               z,
               30,
               immediatesourceentity.getBbWidth() - 0.15,
               immediatesourceentity.getBbHeight() - 0.3,
               immediatesourceentity.getBbWidth() - 0.15,
               0.0025
            );
         }
      }
   }
}
