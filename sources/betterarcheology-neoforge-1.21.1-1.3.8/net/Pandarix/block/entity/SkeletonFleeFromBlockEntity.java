package net.Pandarix.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SkeletonFleeFromBlockEntity extends BlockEntity {
   public SkeletonFleeFromBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.SKELETON_FLEE_FROM.get(), pos, state);
   }
}
