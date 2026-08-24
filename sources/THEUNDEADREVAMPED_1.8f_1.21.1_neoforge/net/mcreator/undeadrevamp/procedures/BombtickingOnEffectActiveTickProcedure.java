package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.CrackleballEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class BombtickingOnEffectActiveTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof CrackleballEntity && world instanceof ServerLevel _level) {
            _level.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 1, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 1.0E-5);
         }
      }
   }
}
