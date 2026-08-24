package net.mcreator.borninchaosv.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class FirelightDeathTimeIsReachedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         _level.sendParticles(ParticleTypes.LAVA, x, y, z, 3, 0.3, 0.3, 0.3, 1.0);
      }

      if (world.getBlockState(BlockPos.containing(x, y - 1.0, z)).canOcclude()) {
         world.setBlock(BlockPos.containing(x, y, z), Blocks.FIRE.defaultBlockState(), 3);
      }
   }
}
