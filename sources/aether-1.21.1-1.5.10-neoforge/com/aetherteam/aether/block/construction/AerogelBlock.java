package com.aetherteam.aether.block.construction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AerogelBlock extends HalfTransparentBlock implements AerogelCulling {
   public AerogelBlock(Properties properties) {
      super(properties);
   }

   public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
      return 3;
   }

   public boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
      return AerogelCulling.super.shouldHideNeighboringAerogelFace(level, pos, state, neighborState, dir);
   }
}
