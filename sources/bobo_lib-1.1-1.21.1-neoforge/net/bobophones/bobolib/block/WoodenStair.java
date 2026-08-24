package net.bobophones.bobolib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class WoodenStair extends StairBlock {
   public WoodenStair(BlockState state, Properties props) {
      super(state, props);
   }

   public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
      return true;
   }

   public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
      return 20;
   }

   public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
      return 5;
   }
}
