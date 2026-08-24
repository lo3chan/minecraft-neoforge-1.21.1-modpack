package com.mcwroofs.kikoz.objects.roofs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Lower extends RoofBlock {
   protected static final VoxelShape BASE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   protected static final VoxelShape BASE_TOP = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

   public Lower(BlockState state, Properties prop) {
      super(prop);
   }

   @Override
   public BlockState updateShape(BlockState state, Direction dir, BlockState statetwo, LevelAccessor access, BlockPos pos, BlockPos postwo) {
      return dir.getAxis().isHorizontal()
         ? (BlockState)state.setValue(SHAPE, getStairsShape(access, pos, (Direction)state.getValue(FACING), (Half)state.getValue(HALF)))
         : super.updateShape(state, dir, statetwo, access, pos, postwo);
   }

   private static StairsShape getStairsShape(BlockGetter reader, BlockPos pos, Direction facing, Half half) {
      BlockState blockstate = reader.getBlockState(pos.relative(facing));
      if (isLower(blockstate) && half == blockstate.getValue(HALF)) {
         Direction direction1 = (Direction)blockstate.getValue(FACING);
         if (direction1.getAxis() != facing.getAxis() && canTakeShape(direction1.getOpposite(), blockstate, reader, pos)) {
            return direction1 == facing.getCounterClockWise() ? StairsShape.OUTER_LEFT : StairsShape.OUTER_RIGHT;
         }
      }

      BlockState blockstate1 = reader.getBlockState(pos.relative(facing.getOpposite()));
      if (isLower(blockstate1) && half == blockstate1.getValue(HALF)) {
         Direction direction2 = (Direction)blockstate1.getValue(FACING);
         if (direction2.getAxis() != facing.getAxis() && canTakeShape(direction2, blockstate1, reader, pos)) {
            return direction2 == facing.getCounterClockWise() ? StairsShape.INNER_LEFT : StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean canTakeShape(Direction dir, BlockState state, BlockGetter reader, BlockPos pos) {
      BlockState blockstate = reader.getBlockState(pos.relative(dir));
      return !isLower(blockstate) || blockstate.getValue(FACING) != state.getValue(FACING) || blockstate.getValue(HALF) != state.getValue(HALF);
   }

   public static boolean isLower(BlockState state) {
      return state.getBlock() instanceof Lower;
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext sel) {
      return state.getValue(HALF) == Half.TOP ? BASE_TOP : BASE;
   }
}
