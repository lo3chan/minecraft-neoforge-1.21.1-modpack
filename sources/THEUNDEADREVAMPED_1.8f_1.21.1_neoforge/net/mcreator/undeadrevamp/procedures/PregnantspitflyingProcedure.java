package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class PregnantspitflyingProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity immediatesourceentity) {
      if (immediatesourceentity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               (SimpleParticleType)UndeadRevamp2ModParticleTypes.BOMBERGOO.get(),
               x,
               y,
               z,
               80,
               immediatesourceentity.getBbWidth(),
               immediatesourceentity.getBbHeight(),
               immediatesourceentity.getBbWidth(),
               1.0E-19
            );
         }
      }
   }
}
