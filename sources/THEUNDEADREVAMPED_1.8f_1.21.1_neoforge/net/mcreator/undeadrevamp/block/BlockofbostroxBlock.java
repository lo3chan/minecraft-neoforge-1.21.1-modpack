package net.mcreator.undeadrevamp.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockofbostroxBlock extends Block {
   public BlockofbostroxBlock() {
      super(Properties.of().sound(SoundType.METAL).strength(1.4F, 10.0F).requiresCorrectToolForDrops().jumpFactor(2.4F));
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }
}
