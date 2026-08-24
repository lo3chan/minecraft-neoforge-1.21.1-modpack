package net.blay09.mods.balm.api.block.entity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BalmBlockEntityBase extends BlockEntity {
   public BalmBlockEntityBase(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
   }

   @Deprecated
   protected abstract void buildProviders(List<Object> var1);
}
