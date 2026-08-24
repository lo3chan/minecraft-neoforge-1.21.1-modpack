package com.aetherteam.aether.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class PlacementConvertEvent extends Event implements ICancellableEvent {
   private final LevelAccessor level;
   private final BlockPos pos;
   private final BlockState oldBlockState;
   private BlockState newBlockState;

   public PlacementConvertEvent(LevelAccessor level, BlockPos pos, BlockState oldBlockState, BlockState newBlockState) {
      this.level = level;
      this.pos = pos;
      this.oldBlockState = oldBlockState;
      this.newBlockState = newBlockState;
   }

   public LevelAccessor getLevel() {
      return this.level;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getOldBlockState() {
      return this.oldBlockState;
   }

   public BlockState getNewBlockState() {
      return this.newBlockState;
   }

   public void setNewBlockState(BlockState newBlockState) {
      this.newBlockState = newBlockState;
   }
}
