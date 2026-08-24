package at.petrak.hexcasting.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockFlammable extends Block {
   public final int burn;
   public final int spread;

   public BlockFlammable(Properties $$0, int burn, int spread) {
      super($$0);
      this.burn = burn;
      this.spread = spread;
   }

   public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return true;
   }

   public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return this.burn;
   }

   public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return this.spread;
   }
}
