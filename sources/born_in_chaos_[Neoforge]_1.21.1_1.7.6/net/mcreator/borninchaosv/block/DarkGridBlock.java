package net.mcreator.borninchaosv.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class DarkGridBlock extends IronBarsBlock {
   public DarkGridBlock() {
      super(Properties.of().sound(SoundType.NETHERITE_BLOCK).strength(1.75F, 100.0F).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }
}
