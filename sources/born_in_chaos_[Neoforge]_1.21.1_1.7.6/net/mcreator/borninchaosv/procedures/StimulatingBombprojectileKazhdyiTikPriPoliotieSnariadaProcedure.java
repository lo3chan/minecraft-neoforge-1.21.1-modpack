package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

public class StimulatingBombprojectileKazhdyiTikPriPoliotieSnariadaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.STIMULATINGBUBBLES.get(), x, y, z, 2, 0.1, 0.1, 0.1, 0.1);
      }
   }
}
