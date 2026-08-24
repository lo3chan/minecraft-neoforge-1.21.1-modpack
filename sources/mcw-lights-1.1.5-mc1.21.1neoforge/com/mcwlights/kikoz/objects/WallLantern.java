package com.mcwlights.kikoz.objects;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallLantern extends Block {
   public static final DirectionProperty FACING = DirectionProperty.create("facing", Plane.HORIZONTAL);
   private static final VoxelShape NORTH = Block.box(5.0, 0.0, 7.0, 11.0, 16.0, 16.0);
   private static final VoxelShape WEST = Block.box(7.0, 0.0, 5.0, 16.0, 16.0, 11.0);
   private static final VoxelShape SOUTH = Block.box(5.0, 0.0, 0.0, 11.0, 16.0, 9.0);
   private static final VoxelShape EAST = Block.box(0.0, 0.0, 5.0, 9.0, 16.0, 11.0);

   public WallLantern(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      Direction direction = (Direction)state.getValue(FACING);
      switch (direction) {
         case NORTH:
            return NORTH;
         case SOUTH:
            return SOUTH;
         case EAST:
            return EAST;
         case WEST:
            return WEST;
         default:
            return Shapes.empty();
      }
   }

   protected boolean isPathfindable(BlockState state, PathComputationType type) {
      return false;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }
}
