package net.mcreator.undeadrevamp.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BostroxstairsBlock extends StairBlock {
   public BostroxstairsBlock() {
      super(
         Blocks.AIR.defaultBlockState(),
         Properties.of().sound(SoundType.METAL).strength(3.0F, 12.0F).friction(0.4F).jumpFactor(1.4F).noOcclusion().isRedstoneConductor((bs, br, bp) -> false)
      );
   }

   public float getExplosionResistance() {
      return 12.0F;
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }
}
