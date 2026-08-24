package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.block.AetherBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SkyrootBedBlockEntity extends BlockEntity {
   public SkyrootBedBlockEntity() {
      super((BlockEntityType)AetherBlockEntityTypes.SKYROOT_BED.get(), BlockPos.ZERO, ((BedBlock)AetherBlocks.SKYROOT_BED.get()).defaultBlockState());
   }

   public SkyrootBedBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)AetherBlockEntityTypes.SKYROOT_BED.get(), pos, state);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
