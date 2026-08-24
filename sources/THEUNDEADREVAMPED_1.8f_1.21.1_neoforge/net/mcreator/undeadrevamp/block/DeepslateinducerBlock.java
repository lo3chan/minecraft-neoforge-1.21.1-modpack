package net.mcreator.undeadrevamp.block;

import net.mcreator.undeadrevamp.procedures.DeepslateinducerOnTickUpdateProcedure;
import net.mcreator.undeadrevamp.procedures.InducerstoneBlockDestroyedByPlayerProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FluidState;

public class DeepslateinducerBlock extends Block {
   public DeepslateinducerBlock() {
      super(Properties.of().sound(SoundType.STONE).strength(1.3F, 10.0F).lightLevel(s -> 5).randomTicks());
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }

   public void randomTick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
      super.randomTick(blockstate, world, pos, random);
      DeepslateinducerOnTickUpdateProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }

   public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
      boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
      InducerstoneBlockDestroyedByPlayerProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
      return retval;
   }
}
