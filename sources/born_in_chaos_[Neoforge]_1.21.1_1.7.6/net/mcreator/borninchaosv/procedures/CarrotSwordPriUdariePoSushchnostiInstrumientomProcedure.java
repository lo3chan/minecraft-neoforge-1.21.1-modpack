package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class CarrotSwordPriUdariePoSushchnostiInstrumientomProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLE_CARROT.get(),
               entity.getX(),
               entity.getY() + 1.5,
               entity.getZ(),
               Mth.nextInt(RandomSource.create(), 4, 6),
               0.3,
               0.3,
               0.3,
               0.3
            );
         }
      }
   }
}
