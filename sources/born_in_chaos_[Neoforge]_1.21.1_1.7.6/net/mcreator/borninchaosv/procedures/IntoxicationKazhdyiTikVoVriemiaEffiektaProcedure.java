package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

public class IntoxicationKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.INTOXICATIND_BOMB_PART.get(), x, y + 1.0, z, 1, 0.4, 0.4, 0.4, 0.2);
      }
   }
}
