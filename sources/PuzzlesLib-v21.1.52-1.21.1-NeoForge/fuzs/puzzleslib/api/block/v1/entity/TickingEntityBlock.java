package fuzs.puzzleslib.api.block.v1.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface TickingEntityBlock<T extends BlockEntity & TickingBlockEntity> extends EntityBlock {
   BlockEntityType<? extends T> getBlockEntityType();

   default BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return this.getBlockEntityType().create(blockPos, blockState);
   }

   @Nullable
   default <BE extends BlockEntity> BlockEntityTicker<BE> getTicker(Level level, BlockState blockState, BlockEntityType<BE> blockEntityType) {
      return this.getBlockEntityType() == blockEntityType ? this.getBlockEntityTicker() : null;
   }

   private <BE extends BlockEntity> BlockEntityTicker<BE> getBlockEntityTicker() {
      return (level, blockPos, blockState, blockEntity) -> {
         if (level instanceof ServerLevel serverLevel) {
            ((TickingBlockEntity)blockEntity).serverTick(serverLevel, blockPos, blockState);
         } else {
            ((TickingBlockEntity)blockEntity).clientTick(level, blockPos, blockState);
         }
      };
   }
}
