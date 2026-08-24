package net.mcreator.borninchaosv.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class BlackArgilliteButtonBlock extends ButtonBlock {
   public BlackArgilliteButtonBlock() {
      super(
         BlockSetType.STONE,
         20,
         Properties.of().instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.GILDED_BLACKSTONE).strength(4.0F, 17.0F).requiresCorrectToolForDrops()
      );
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }
}
