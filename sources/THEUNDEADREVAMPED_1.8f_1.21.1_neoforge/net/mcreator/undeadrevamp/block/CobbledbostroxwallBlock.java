package net.mcreator.undeadrevamp.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.pathfinder.PathType;

public class CobbledbostroxwallBlock extends WallBlock {
   public CobbledbostroxwallBlock() {
      super(
         Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.STONE)
            .strength(3.0F, 12.0F)
            .friction(0.4F)
            .jumpFactor(1.4F)
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false)
            .forceSolidOn()
      );
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }

   public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
      return PathType.FENCE;
   }
}
