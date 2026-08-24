package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

public class ArapholiaClientDisplayRandomTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (Math.random() < 0.15 && world instanceof ServerLevel _level) {
         _level.sendParticles(ParticleTypes.ASH, x, y, z, 5, 1.0, 1.0, 1.0, 0.001);
      }
   }
}
