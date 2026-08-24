package at.petrak.hexcasting.common.blocks.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockHexStairs extends StairBlock {
   public BlockHexStairs(BlockState $$0, Properties $$1) {
      super($$0, $$1);
   }

   public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return true;
   }

   public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return 20;
   }

   public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return 5;
   }
}
