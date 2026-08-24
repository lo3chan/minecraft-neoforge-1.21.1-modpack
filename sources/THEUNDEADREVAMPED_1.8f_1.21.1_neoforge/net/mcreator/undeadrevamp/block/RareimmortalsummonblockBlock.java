package net.mcreator.undeadrevamp.block;

import net.mcreator.undeadrevamp.procedures.RareimmortalsummonblockUpdateTickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class RareimmortalsummonblockBlock extends Block {
   public RareimmortalsummonblockBlock() {
      super(Properties.of().sound(SoundType.GRAVEL).strength(1.0F, 10.0F).randomTicks());
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }

   public void randomTick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
      super.randomTick(blockstate, world, pos, random);
      RareimmortalsummonblockUpdateTickProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }
}
