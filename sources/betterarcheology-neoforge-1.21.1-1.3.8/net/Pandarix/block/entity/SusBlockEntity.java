package net.Pandarix.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SusBlockEntity extends BrushableBlockEntity {
   public SusBlockEntity(BlockPos pos, BlockState state) {
      super(pos, state);
   }

   public boolean isValidBlockState(@NotNull BlockState blockState) {
      return ((BlockEntityType)ModBlockEntities.SUSBLOCK.get()).isValid(blockState) || super.isValidBlockState(blockState);
   }

   @NotNull
   public BlockEntityType<?> getType() {
      return (BlockEntityType<?>)ModBlockEntities.SUSBLOCK.get();
   }
}
