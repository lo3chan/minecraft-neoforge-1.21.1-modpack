package net.mcreator.borninchaosv.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class BlackArgilliteStairsBlock extends StairBlock {
   public BlackArgilliteStairsBlock() {
      super(
         Blocks.AIR.defaultBlockState(),
         Properties.of().instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.DEEPSLATE).strength(4.0F, 17.0F).requiresCorrectToolForDrops()
      );
   }

   public float getExplosionResistance() {
      return 17.0F;
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }
}
