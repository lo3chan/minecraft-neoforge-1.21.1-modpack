package io.github.razordevs.deep_aether.block.natural;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class HalfTransperentHugeMushroomBlock extends HugeMushroomBlock {
   public HalfTransperentHugeMushroomBlock(Properties properties) {
      super(properties);
   }

   public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
      return pAdjacentBlockState.is(this) || super.skipRendering(pState, pAdjacentBlockState, pSide);
   }
}
