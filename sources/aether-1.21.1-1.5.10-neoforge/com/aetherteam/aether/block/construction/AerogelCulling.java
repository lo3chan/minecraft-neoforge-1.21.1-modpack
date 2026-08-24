package com.aetherteam.aether.block.construction;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public interface AerogelCulling {
   default boolean shouldHideNeighboringAerogelFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
      if (neighborState.getBlock() instanceof AerogelCulling) {
         List<AABB> faceBounds = state.getBlockSupportShape(level, pos).getFaceShape(dir).toAabbs();
         List<AABB> neighborFaceBounds = neighborState.getBlockSupportShape(level, pos.offset(dir.getNormal())).getFaceShape(dir.getOpposite()).toAabbs();
         return faceBounds.equals(neighborFaceBounds);
      } else {
         return neighborState.getBlock() instanceof AerogelWallBlock;
      }
   }
}
