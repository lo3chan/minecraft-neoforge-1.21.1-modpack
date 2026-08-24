package net.mcreator.borninchaosv.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class MeshDoorBlock extends DoorBlock {
   public MeshDoorBlock() {
      super(
         BlockSetType.IRON,
         Properties.of().sound(SoundType.METAL).strength(1.7F, 40.0F).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((bs, br, bp) -> false)
      );
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }
}
