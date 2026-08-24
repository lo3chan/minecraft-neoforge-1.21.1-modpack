package com.aetherteam.aether.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SkyrootSignBlockEntity extends SignBlockEntity {
   public SkyrootSignBlockEntity(BlockPos pos, BlockState state) {
      super(pos, state);
   }

   public BlockEntityType<SkyrootSignBlockEntity> getType() {
      return (BlockEntityType<SkyrootSignBlockEntity>)AetherBlockEntityTypes.SKYROOT_SIGN.get();
   }
}
