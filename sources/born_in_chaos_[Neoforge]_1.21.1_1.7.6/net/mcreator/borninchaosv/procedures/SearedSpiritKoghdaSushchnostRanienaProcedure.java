package net.mcreator.borninchaosv.procedures;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

public class SearedSpiritKoghdaSushchnostRanienaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (Math.random() < 0.5 && world instanceof ServerLevel _level) {
         _level.sendParticles(ParticleTypes.LAVA, x, y, z, 3, 0.5, 0.5, 0.5, 1.0);
      }
   }
}
