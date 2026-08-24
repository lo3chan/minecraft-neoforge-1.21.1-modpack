package com.yungnickyoung.minecraft.betterendisland.world.util;

import com.google.common.collect.ImmutableList;
import com.yungnickyoung.minecraft.betterendisland.BetterEndIslandCommon;
import com.yungnickyoung.minecraft.betterendisland.world.feature.BetterSpikeFeature;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.SpikeFeature.EndSpike;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.phys.AABB;

public class EndSpikeUtils {
   public static void resetSpikes(ServerLevel serverLevel, List<EndSpike> spikes) {
      spikes.forEach(
         spike -> {
            int resetRadius = 11;
            int verticalRadius = BetterEndIslandCommon.betterEnd ? 40 : 30;

            for (BlockPos blockPos : BlockPos.betweenClosed(
               new BlockPos(spike.getCenterX() - resetRadius, spike.getHeight() - verticalRadius, spike.getCenterZ() - resetRadius),
               new BlockPos(spike.getCenterX() + resetRadius, spike.getHeight() + verticalRadius, spike.getCenterZ() + resetRadius)
            )) {
               if (!serverLevel.getBlockState(blockPos).is(Blocks.END_STONE)) {
                  serverLevel.removeBlock(blockPos, false);
               }
            }

            SpikeConfiguration spikeConfig = new SpikeConfiguration(true, ImmutableList.of(spike), null);
            BetterSpikeFeature.placeSpike(serverLevel, RandomSource.create(), spikeConfig, spike, true);
         }
      );
   }

   public static void removeVanillaPillars(ServerLevel serverLevel) {
      int obsidianRemoved = 0;
      RandomSource randomSource = RandomSource.create(serverLevel.getSeed());
      long seed = randomSource.nextLong() & 65535L;
      IntArrayList indexes = Util.toShuffledList(IntStream.range(0, 10), RandomSource.create(seed));

      for (int i = 0; i < 10; i++) {
         int x = Mth.floor(42.0 * Math.cos(2.0 * (-3.141592653589793 + 0.3141592653589793 * i)));
         int z = Mth.floor(42.0 * Math.sin(2.0 * (-3.141592653589793 + 0.3141592653589793 * i)));
         int index = indexes.get(i);
         int radius = 2 + index / 3;
         int height = 76 + index * 3;
         boolean isGuarded = index == 1 || index == 2;
         AABB topBoundingBox = new AABB(x - radius, DimensionType.MIN_Y, z - radius, x + radius, DimensionType.MAX_Y, z + radius);
         serverLevel.getEntitiesOfClass(EndCrystal.class, topBoundingBox).forEach(Entity::discard);

         for (BlockPos pos : BlockPos.betweenClosed(
            new BlockPos(x - radius, serverLevel.getMinBuildHeight(), z - radius), new BlockPos(x + radius, height + 20, z + radius)
         )) {
            if (pos.distToLowCornerSqr(x, pos.getY(), z) <= radius * radius + 1) {
               BlockState blockState = serverLevel.getBlockState(pos);
               if (blockState.is(Blocks.OBSIDIAN) || blockState.is(Blocks.BEDROCK)) {
                  serverLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                  if (blockState.is(Blocks.OBSIDIAN)) {
                     obsidianRemoved++;
                  }
               }
            }
         }

         if (obsidianRemoved > 10) {
            int offset = radius + 1;
            int topY = -1;
            int surfaceY;
            if ((surfaceY = WorldgenUtils.getSurfacePosAt(serverLevel, x - offset, z - offset)) > topY) {
               topY = surfaceY;
            }

            if ((surfaceY = WorldgenUtils.getSurfacePosAt(serverLevel, x - offset, z + offset)) > topY) {
               topY = surfaceY;
            }

            if ((surfaceY = WorldgenUtils.getSurfacePosAt(serverLevel, x + offset, z - offset)) > topY) {
               topY = surfaceY;
            }

            if ((surfaceY = WorldgenUtils.getSurfacePosAt(serverLevel, x + offset, z + offset)) > topY) {
               topY = surfaceY;
            }

            int bottomY = 255;
            if ((surfaceY = WorldgenUtils.getLowestBlockPosAt(serverLevel, x - offset, z - offset)) < bottomY) {
               bottomY = surfaceY;
            }

            if ((surfaceY = WorldgenUtils.getLowestBlockPosAt(serverLevel, x - offset, z + offset)) < bottomY) {
               bottomY = surfaceY;
            }

            if ((surfaceY = WorldgenUtils.getLowestBlockPosAt(serverLevel, x + offset, z - offset)) < bottomY) {
               bottomY = surfaceY;
            }

            if ((surfaceY = WorldgenUtils.getLowestBlockPosAt(serverLevel, x + offset, z + offset)) < bottomY) {
               bottomY = surfaceY;
            }

            if (topY != -1 && bottomY != 255) {
               for (BlockPos posx : BlockPos.betweenClosed(new BlockPos(x - radius, bottomY, z - radius), new BlockPos(x + radius, topY, z + radius))) {
                  if (posx.distToLowCornerSqr(x, posx.getY(), z) <= radius * radius + 1) {
                     BlockState blockState = serverLevel.getBlockState(posx);
                     if (blockState.is(Blocks.AIR) && posx.getY() <= topY && posx.getY() >= bottomY) {
                        serverLevel.setBlockAndUpdate(posx, Blocks.END_STONE.defaultBlockState());
                     }
                  }
               }
            }
         }

         if (isGuarded) {
            MutableBlockPos mutable = new MutableBlockPos();

            for (int fenceX = -2; fenceX <= 2; fenceX++) {
               for (int fenceZ = -2; fenceZ <= 2; fenceZ++) {
                  for (int fenceY = 0; fenceY <= 3; fenceY++) {
                     if (Mth.abs(fenceX) == 2 || Mth.abs(fenceZ) == 2 || fenceY == 3) {
                        mutable.set(x + fenceX, height + fenceY, z + fenceZ);
                        serverLevel.setBlockAndUpdate(mutable, Blocks.AIR.defaultBlockState());
                     }
                  }
               }
            }
         }
      }
   }
}
