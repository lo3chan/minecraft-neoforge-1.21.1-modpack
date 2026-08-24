package net.mehvahdjukaar.moonlight.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public final class DummyBlockGetter implements BlockGetter {
   public static final BlockGetter INSTANCE = new DummyBlockGetter();

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pos) {
      return null;
   }

   public BlockState getBlockState(BlockPos pos) {
      return Blocks.AIR.defaultBlockState();
   }

   public FluidState getFluidState(BlockPos pos) {
      return Fluids.EMPTY.defaultFluidState();
   }

   public int getHeight() {
      return 1;
   }

   public int getMinBuildHeight() {
      return 0;
   }
}
