package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

public class SlavemanEntityIsHurtProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         _level.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 2, 1.0, 2.0, 1.0, 1.0);
      }
   }
}
