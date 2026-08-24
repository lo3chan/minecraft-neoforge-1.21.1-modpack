package io.github.razordevs.deep_aether.block.natural;

import io.github.razordevs.deep_aether.init.DABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class LightCapMushroomBlock extends MushroomBlock {
   public LightCapMushroomBlock(Properties properties, ResourceKey<ConfiguredFeature<?, ?>> configuredFeature) {
      super(configuredFeature, properties);
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockPos blockpos = pos.below();
      BlockState blockstate = level.getBlockState(blockpos);
      if (blockstate.is((Block)DABlocks.ROTTEN_ROSEROOT_LOG.get())) {
         return ((Axis)blockstate.getValue(RotatedPillarBlock.AXIS)).isHorizontal();
      } else {
         return blockstate.is(BlockTags.MUSHROOM_GROW_BLOCK) ? true : blockstate.canSustainPlant(level, blockpos, Direction.UP, state).isTrue();
      }
   }
}
