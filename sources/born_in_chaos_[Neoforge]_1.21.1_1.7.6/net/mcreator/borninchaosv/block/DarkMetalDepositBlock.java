package net.mcreator.borninchaosv.block;

import net.mcreator.borninchaosv.procedures.DarkMetalDepositKoghdaBlokRazrushienIghrokomProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;

public class DarkMetalDepositBlock extends Block {
   public DarkMetalDepositBlock() {
      super(Properties.of().instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.DEEPSLATE).strength(4.0F, 17.0F).requiresCorrectToolForDrops());
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }

   public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
      boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
      DarkMetalDepositKoghdaBlokRazrushienIghrokomProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), entity);
      return retval;
   }
}
