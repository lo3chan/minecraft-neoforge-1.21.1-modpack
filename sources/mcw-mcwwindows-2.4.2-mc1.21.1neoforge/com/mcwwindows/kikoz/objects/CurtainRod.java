package com.mcwwindows.kikoz.objects;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CurtainRod extends HorizontalDirectionalBlock {
   protected static final VoxelShape NORTH = Shapes.or(box(0.0, 0.0, 13.0, 16.0, 3.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape WEST = Shapes.or(box(13.0, 0.0, 0.0, 16.0, 3.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape EAST = Shapes.or(box(0.0, 0.0, 0.0, 3.0, 3.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape SOUTH = Shapes.or(box(0.0, 0.0, 0.0, 16.0, 3.0, 3.0), new VoxelShape[0]);

   public CurtainRod(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case WEST:
            return WEST;
         case EAST:
            return EAST;
         case SOUTH:
            return SOUTH;
         case NORTH:
         default:
            return NORTH;
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return null;
   }
}
