package net.Pandarix.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FleeFromBlockEntity extends BlockEntity {
   public FleeFromBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.FLEE_FROM.get(), pos, state);
   }
}
