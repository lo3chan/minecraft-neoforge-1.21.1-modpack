package net.mcreator.undeadrevamp.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.pathfinder.PathType;

public class BostroxslabsBlock extends SlabBlock {
   public BostroxslabsBlock() {
      super(
         Properties.of().sound(SoundType.METAL).strength(3.0F, 12.0F).friction(0.4F).jumpFactor(1.4F).noOcclusion().isRedstoneConductor((bs, br, bp) -> false)
      );
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }

   public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
      return PathType.FENCE;
   }
}
