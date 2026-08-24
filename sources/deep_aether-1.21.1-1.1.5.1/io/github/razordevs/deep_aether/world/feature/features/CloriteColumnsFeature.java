package io.github.razordevs.deep_aether.world.feature.features;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import io.github.razordevs.deep_aether.init.DABlocks;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;

public class CloriteColumnsFeature extends Feature<ColumnFeatureConfiguration> {
   private static final ImmutableList<Block> CANNOT_PLACE_ON = ImmutableList.of(Blocks.WATER, Blocks.BEDROCK, Blocks.CHEST, Blocks.SPAWNER);

   public CloriteColumnsFeature(Codec<ColumnFeatureConfiguration> pCodec) {
      super(pCodec);
   }

   public boolean place(FeaturePlaceContext<ColumnFeatureConfiguration> pContext) {
      int i = pContext.chunkGenerator().getSeaLevel();
      BlockPos blockpos = pContext.origin();
      WorldGenLevel worldgenlevel = pContext.level();
      RandomSource randomsource = pContext.random();
      ColumnFeatureConfiguration columnfeatureconfiguration = (ColumnFeatureConfiguration)pContext.config();
      if (!canPlaceAt(worldgenlevel, blockpos.mutable())) {
         return false;
      } else {
         int j = columnfeatureconfiguration.height().sample(randomsource);
         boolean flag = randomsource.nextFloat() < 0.9F;
         int k = Math.min(j, flag ? 5 : 8);
         int l = flag ? 50 : 15;
         boolean flag1 = false;

         for (BlockPos blockpos1 : BlockPos.randomBetweenClosed(
            randomsource, l, blockpos.getX() - k, blockpos.getY(), blockpos.getZ() - k, blockpos.getX() + k, blockpos.getY(), blockpos.getZ() + k
         )) {
            int i1 = j - blockpos1.distManhattan(blockpos);
            if (i1 >= 0) {
               flag1 |= this.placeColumn(worldgenlevel, i, blockpos1, i1, columnfeatureconfiguration.reach().sample(randomsource));
            }
         }

         return flag1;
      }
   }

   private boolean placeColumn(LevelAccessor pLevel, int pSeaLevel, BlockPos pPos, int pDistance, int pReach) {
      boolean flag = false;

      for (BlockPos blockpos : BlockPos.betweenClosed(
         pPos.getX() - pReach, pPos.getY(), pPos.getZ() - pReach, pPos.getX() + pReach, pPos.getY(), pPos.getZ() + pReach
      )) {
         int i = blockpos.distManhattan(pPos);
         BlockPos blockpos1 = isAirOrCloud(pLevel, blockpos) ? findSurface(pLevel, pSeaLevel, blockpos.mutable(), i) : findAir(pLevel, blockpos.mutable(), i);
         if (blockpos1 != null) {
            int j = pDistance - i / 2;

            for (MutableBlockPos blockpos$mutableblockpos = blockpos1.mutable(); j >= 0; j--) {
               if (isAirOrCloud(pLevel, blockpos$mutableblockpos)) {
                  this.setBlock(pLevel, blockpos$mutableblockpos, ((Block)DABlocks.CLORITE.get()).defaultBlockState());
                  blockpos$mutableblockpos.move(Direction.UP);
                  flag = true;
               } else {
                  if (!pLevel.getBlockState(blockpos$mutableblockpos).is((Block)DABlocks.CLORITE.get())) {
                     break;
                  }

                  blockpos$mutableblockpos.move(Direction.UP);
               }
            }
         }
      }

      return flag;
   }

   @Nullable
   private static BlockPos findSurface(LevelAccessor pLevel, int pSeaLevel, MutableBlockPos pPos, int pDistance) {
      while (pPos.getY() > pLevel.getMinBuildHeight() + 1 && pDistance > 0) {
         pDistance--;
         if (canPlaceAt(pLevel, pPos)) {
            return pPos;
         }

         pPos.move(Direction.DOWN);
      }

      return null;
   }

   private static boolean canPlaceAt(LevelAccessor pLevel, MutableBlockPos pPos) {
      if (!isAirOrCloud(pLevel, pPos)) {
         return false;
      } else {
         BlockState blockstate = pLevel.getBlockState(pPos.move(Direction.DOWN));
         pPos.move(Direction.UP);
         return !blockstate.isAir() && !CANNOT_PLACE_ON.contains(blockstate.getBlock());
      }
   }

   @Nullable
   private static BlockPos findAir(LevelAccessor pLevel, MutableBlockPos pPos, int pDistance) {
      while (pPos.getY() < pLevel.getMaxBuildHeight() && pDistance > 0) {
         pDistance--;
         BlockState blockstate = pLevel.getBlockState(pPos);
         if (CANNOT_PLACE_ON.contains(blockstate.getBlock())) {
            return null;
         }

         if (blockstate.isAir()) {
            return pPos;
         }

         pPos.move(Direction.UP);
      }

      return null;
   }

   private static boolean isAirOrCloud(LevelAccessor pLevel, BlockPos pPos) {
      BlockState blockstate = pLevel.getBlockState(pPos);
      return blockstate.isAir() || blockstate.is(com.aetherteam.aether.AetherTags.Blocks.AERCLOUDS);
   }
}
