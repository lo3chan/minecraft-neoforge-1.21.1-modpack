package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class DarcGonProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.DISAPPEARANCEOFSPIRITSUNDERTHESUN)
            && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1.0, z))
            && world instanceof Level _lvl2
            && _lvl2.isDay()
            && !world.getLevelData().isRaining()
            && !world.getLevelData().isThundering()
            && !world.isClientSide()) {
            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x, y, z, 10, 0.3, 0.3, 0.3, 0.1);
            }
         }

         if (world instanceof ServerLevel _level) {
            _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DIM.get(), x, y, z, 1, 0.2, 0.2, 0.2, 0.1);
         }
      }
   }
}
