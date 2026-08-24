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

public class FeatherGrassPlant extends TallGrassBlock {
   public FeatherGrassPlant(Properties properties) {
      super(properties);
   }

   public void performBonemeal(ServerLevel level, RandomSource source, BlockPos pos, BlockState state) {
      if (state.is((Block)DABlocks.FEATHER_GRASS.get())
         && ((Block)DABlocks.TALL_FEATHER_GRASS.get()).defaultBlockState().canSurvive(level, pos)
         && level.isEmptyBlock(pos.above())) {
         DoublePlantBlock.placeAt(level, ((Block)DABlocks.TALL_FEATHER_GRASS.get()).defaultBlockState(), pos, 2);
      }
   }
}
