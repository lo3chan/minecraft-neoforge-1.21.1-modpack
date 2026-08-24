package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class SpitterneccWhileProjectileFlyingTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity immediatesourceentity) {
      if (immediatesourceentity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               (SimpleParticleType)UndeadRevamp2ModParticleTypes.ACIDGOO.get(),
               x,
               y,
               z,
               80,
               immediatesourceentity.getBbWidth(),
               immediatesourceentity.getBbHeight(),
               immediatesourceentity.getBbWidth(),
               0.1
            );
         }
      }
   }
}
