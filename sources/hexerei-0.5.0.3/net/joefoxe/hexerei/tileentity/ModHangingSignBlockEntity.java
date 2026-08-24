package net.joefoxe.hexerei.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModHangingSignBlockEntity extends SignBlockEntity {
   public ModHangingSignBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType)ModTileEntities.HANGING_SIGN_TILE.get(), pPos, pBlockState);
   }

   public BlockEntityType<?> getType() {
      return (BlockEntityType<?>)ModTileEntities.HANGING_SIGN_TILE.get();
   }
}
