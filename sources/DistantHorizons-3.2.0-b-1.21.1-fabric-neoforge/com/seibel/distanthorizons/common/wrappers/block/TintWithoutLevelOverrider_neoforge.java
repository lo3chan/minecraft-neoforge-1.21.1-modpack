package com.seibel.distanthorizons.common.wrappers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class TintWithoutLevelOverrider_neoforge extends AbstractDhTintGetter_neoforge {
   public float getShade(Direction direction, boolean shade) {
      throw new UnsupportedOperationException("ERROR: getShade() called on TintWithoutLevelOverrider. Object is for tinting only.");
   }

   public LevelLightEngine getLightEngine() {
      throw new UnsupportedOperationException("ERROR: getLightEngine() called on TintWithoutLevelOverrider. Object is for tinting only.");
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pos) {
      throw new UnsupportedOperationException("ERROR: getBlockEntity() called on TintWithoutLevelOverrider. Object is for tinting only.");
   }

   public BlockState getBlockState(BlockPos pos) {
      throw new UnsupportedOperationException("ERROR: getBlockState() called on TintWithoutLevelOverrider. Object is for tinting only.");
   }

   public FluidState getFluidState(BlockPos pos) {
      throw new UnsupportedOperationException("ERROR: getFluidState() called on TintWithoutLevelOverrider. Object is for tinting only.");
   }

   public int getHeight() {
      throw new UnsupportedOperationException("ERROR: getHeight() called on TintWithoutLevelOverrider. Object is for tinting only.");
   }

   public int getMinBuildHeight() {
      throw new UnsupportedOperationException("ERROR: getMinBuildHeight() called on TintWithoutLevelSmoothOverrider. Object is for tinting only.");
   }
}
