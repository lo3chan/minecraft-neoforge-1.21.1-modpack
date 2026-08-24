package io.github.razordevs.deep_aether.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class GlowingGrassBlock extends DoublePlantBlock {
   public GlowingGrassBlock(Properties properties) {
      super(properties);
   }

   public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
      return ((DoubleBlockHalf)state.getValue(DoublePlantBlock.HALF)).equals(DoubleBlockHalf.UPPER) ? 7 : 0;
   }
}
