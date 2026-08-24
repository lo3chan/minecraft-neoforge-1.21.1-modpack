package io.github.razordevs.deep_aether.block.natural;

import io.github.razordevs.deep_aether.init.DABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class GoldenGrassPlant extends TallGrassBlock {
   public GoldenGrassPlant(Properties properties) {
      super(properties);
   }

   public void performBonemeal(ServerLevel level, RandomSource source, BlockPos pos, BlockState state) {
      if (state.is((Block)DABlocks.MINI_GOLDEN_GRASS.get())) {
         level.setBlockAndUpdate(pos, ((Block)DABlocks.SHORT_GOLDEN_GRASS.get()).defaultBlockState());
      } else if (state.is((Block)DABlocks.SHORT_GOLDEN_GRASS.get())) {
         level.setBlockAndUpdate(pos, ((Block)DABlocks.MEDIUM_GOLDEN_GRASS.get()).defaultBlockState());
      } else if (((Block)DABlocks.TALL_GOLDEN_GRASS.get()).defaultBlockState().canSurvive(level, pos) && level.isEmptyBlock(pos.above())) {
         DoublePlantBlock.placeAt(level, ((Block)DABlocks.TALL_GOLDEN_GRASS.get()).defaultBlockState(), pos, 2);
      }
   }
}
