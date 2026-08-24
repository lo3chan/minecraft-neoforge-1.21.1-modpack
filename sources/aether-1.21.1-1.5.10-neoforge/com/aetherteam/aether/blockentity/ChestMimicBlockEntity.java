package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.block.AetherBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChestMimicBlockEntity extends BlockEntity {
   public ChestMimicBlockEntity() {
      super((BlockEntityType)AetherBlockEntityTypes.CHEST_MIMIC.get(), BlockPos.ZERO, ((Block)AetherBlocks.CHEST_MIMIC.get()).defaultBlockState());
   }

   public ChestMimicBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)AetherBlockEntityTypes.CHEST_MIMIC.get(), pos, state);
   }
}
