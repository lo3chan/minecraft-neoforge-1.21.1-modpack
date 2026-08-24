package com.aetherteam.aether.world;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public final class BlockPlacementUtil {
   public static void placeDisk(WorldGenLevel level, BlockStateProvider blockProvider, BlockPos center, float radius, RandomSource random) {
      float radiusSq = radius * radius;
      placeProvidedBlock(level, blockProvider, center, random);

      for (int z = 0; z < radius; z++) {
         for (int x = 0; x < radius; x++) {
            if (!(x * x + z * z > radiusSq)) {
               placeProvidedBlock(level, blockProvider, center.offset(x, 0, z), random);
               placeProvidedBlock(level, blockProvider, center.offset(-x, 0, -z), random);
               placeProvidedBlock(level, blockProvider, center.offset(-z, 0, x), random);
               placeProvidedBlock(level, blockProvider, center.offset(z, 0, -x), random);
            }
         }
      }
   }

   public static boolean placeProvidedBlock(WorldGenLevel level, BlockStateProvider provider, BlockPos pos, RandomSource random) {
      return level.getBlockState(pos).isAir() ? level.setBlock(pos, provider.getState(random, pos), 2) : false;
   }
}
