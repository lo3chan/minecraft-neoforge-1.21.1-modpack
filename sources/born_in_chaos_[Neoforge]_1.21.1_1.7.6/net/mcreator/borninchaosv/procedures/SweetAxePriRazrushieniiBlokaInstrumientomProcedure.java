package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

public class SweetAxePriRazrushieniiBlokaInstrumientomProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.CANDY_ORANGE.get(), x + 0.5, y + 0.5, z + 0.5, 1, 0.2, 0.2, 0.2, 0.2);
      }

      if (world instanceof ServerLevel _level) {
         _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.CANDYGREN.get(), x + 0.5, y + 0.5, z + 0.5, 1, 0.2, 0.2, 0.2, 0.2);
      }

      if (world instanceof ServerLevel _level) {
         _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.CANDYPURPLE.get(), x + 0.5, y + 0.5, z + 0.5, 1, 0.2, 0.2, 0.2, 0.2);
      }
   }
}
