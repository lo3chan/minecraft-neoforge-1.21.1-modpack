package com.aetherteam.aether.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SkyrootHangingSignBlockEntity extends HangingSignBlockEntity {
   public SkyrootHangingSignBlockEntity(BlockPos pos, BlockState state) {
      super(pos, state);
   }

   public BlockEntityType<SkyrootHangingSignBlockEntity> getType() {
      return (BlockEntityType<SkyrootHangingSignBlockEntity>)AetherBlockEntityTypes.SKYROOT_HANGING_SIGN.get();
   }
}
