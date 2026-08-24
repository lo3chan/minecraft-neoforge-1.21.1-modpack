package com.mcwlights.kikoz.objects;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoubleStreet extends ClassicStreet {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final VoxelShape ONE = Block.box(6.0, 0.0, 6.0, 10.0, 5.0, 10.0);
   private static final VoxelShape TWO = Block.box(5.0, 7.0, 0.0, 11.0, 16.0, 16.0);
   private static final VoxelShape THREE = Block.box(6.0, 5.0, 2.0, 10.0, 7.0, 14.0);
   private static final VoxelShape BASE_TOP_NS = Shapes.or(ONE, new VoxelShape[]{TWO, THREE});
   private static final VoxelShape FOUR = Block.box(6.0, 0.0, 6.0, 10.0, 5.0, 10.0);
   private static final VoxelShape FIVE = Block.box(0.0, 7.0, 5.0, 16.0, 16.0, 11.0);
   private static final VoxelShape SIX = Block.box(2.0, 5.0, 6.0, 14.0, 7.0, 10.0);
   private static final VoxelShape BASE_TOP_WE = Shapes.or(FOUR, new VoxelShape[]{FIVE, SIX});
   private static final VoxelShape MIDDLE_BOTTOM = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            LightBaseTall.LightPart part1 = (LightBaseTall.LightPart)state.getValue(PART);
            if (part1 == LightBaseTall.LightPart.MIDDLE) {
               return MIDDLE_BOTTOM;
            } else {
               if (part1 == LightBaseTall.LightPart.BOTTOM) {
                  return MIDDLE_BOTTOM;
               }

               return BASE_TOP_NS;
            }
         case SOUTH:
            LightBaseTall.LightPart part2 = (LightBaseTall.LightPart)state.getValue(PART);
            if (part2 == LightBaseTall.LightPart.MIDDLE) {
               return MIDDLE_BOTTOM;
            } else {
               if (part2 == LightBaseTall.LightPart.BOTTOM) {
                  return MIDDLE_BOTTOM;
               }

               return BASE_TOP_NS;
            }
         case EAST:
            LightBaseTall.LightPart part3 = (LightBaseTall.LightPart)state.getValue(PART);
            if (part3 == LightBaseTall.LightPart.MIDDLE) {
               return MIDDLE_BOTTOM;
            } else {
               if (part3 == LightBaseTall.LightPart.BOTTOM) {
                  return MIDDLE_BOTTOM;
               }

               return BASE_TOP_WE;
            }
         case WEST:
         default:
            LightBaseTall.LightPart part4 = (LightBaseTall.LightPart)state.getValue(PART);
            if (part4 == LightBaseTall.LightPart.MIDDLE) {
               return MIDDLE_BOTTOM;
            } else {
               return part4 == LightBaseTall.LightPart.BOTTOM ? MIDDLE_BOTTOM : BASE_TOP_WE;
            }
      }
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.LightState(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos())
            .setValue(FACING, context.getHorizontalDirection().getCounterClockWise()))
         .setValue(POWERED, false);
   }

   public DoubleStreet(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true)).setValue(PART, LightBaseTall.LightPart.BOTTOM))
            .setValue(FACING, Direction.NORTH)
      );
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PART, LIT, FACING, POWERED});
   }
}
