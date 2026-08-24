package io.github.razordevs.deep_aether.entity.block;

import io.github.razordevs.deep_aether.init.DABlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DAHangingSignBlockEntity extends HangingSignBlockEntity {
   public DAHangingSignBlockEntity(BlockPos pos, BlockState state) {
      super(pos, state);
   }

   public BlockEntityType<DAHangingSignBlockEntity> getType() {
      return (BlockEntityType<DAHangingSignBlockEntity>)DABlockEntityTypes.HANGING_SIGN.get();
   }
}
