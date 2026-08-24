package net.mcreator.borninchaosv.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class ScorchedPlanksPressurePlatesBlock extends PressurePlateBlock {
   public ScorchedPlanksPressurePlatesBlock() {
      super(
         BlockSetType.OAK,
         Properties.of()
            .ignitedByLava()
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.WOOD)
            .strength(0.5F, 0.0F)
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false)
            .forceSolidOn()
      );
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }
}
