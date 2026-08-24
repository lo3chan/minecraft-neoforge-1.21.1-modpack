package com.aetherteam.aether.block.construction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class AerogelSlabBlock extends SlabBlock implements AerogelCulling {
   public AerogelSlabBlock(Properties properties) {
      super(properties);
   }

   public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
      return 3;
   }

   public boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
      return AerogelCulling.super.shouldHideNeighboringAerogelFace(level, pos, state, neighborState, dir);
   }
}
