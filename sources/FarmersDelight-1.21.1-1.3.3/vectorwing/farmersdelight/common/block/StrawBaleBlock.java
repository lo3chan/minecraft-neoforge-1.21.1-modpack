package vectorwing.farmersdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class StrawBaleBlock extends HayBlock {
   public StrawBaleBlock(Properties properties) {
      super(properties);
   }

   public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
      return 60;
   }

   public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
      return 20;
   }
}
