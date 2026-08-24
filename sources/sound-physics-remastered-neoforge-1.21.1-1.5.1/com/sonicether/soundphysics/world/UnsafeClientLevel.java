package com.sonicether.soundphysics.world;

import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class UnsafeClientLevel implements ClientLevelProxy {
   private final ClientLevel clientLevel;

   public UnsafeClientLevel(ClientLevel level) {
      this.clientLevel = level;
   }

   public BlockEntity getBlockEntity(@Nonnull BlockPos position) {
      return this.clientLevel.getBlockEntity(position);
   }

   public BlockState getBlockState(@Nonnull BlockPos position) {
      return this.clientLevel.getBlockState(position);
   }

   public FluidState getFluidState(@Nonnull BlockPos position) {
      return this.clientLevel.getFluidState(position);
   }

   public int getHeight() {
      return this.clientLevel.getHeight();
   }

   public int getMinBuildHeight() {
      return this.clientLevel.getMinBuildHeight();
   }
}
