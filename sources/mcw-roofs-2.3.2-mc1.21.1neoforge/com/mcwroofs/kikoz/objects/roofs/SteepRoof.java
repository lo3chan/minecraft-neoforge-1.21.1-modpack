package com.mcwroofs.kikoz.objects.roofs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;

public class SteepRoof extends RoofBlock {
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

   public SteepRoof(BlockState state, Properties prop) {
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
      if (isSteepRoof(blockstate) && half == blockstate.getValue(HALF)) {
         Direction direction1 = (Direction)blockstate.getValue(FACING);
         if (direction1.getAxis() != facing.getAxis() && canTakeShape(direction1.getOpposite(), blockstate, reader, pos)) {
            return direction1 == facing.getCounterClockWise() ? StairsShape.OUTER_LEFT : StairsShape.OUTER_RIGHT;
         }
      }

      BlockState blockstate1 = reader.getBlockState(pos.relative(facing.getOpposite()));
      if (isSteepRoof(blockstate1) && half == blockstate1.getValue(HALF)) {
         Direction direction2 = (Direction)blockstate1.getValue(FACING);
         if (direction2.getAxis() != facing.getAxis() && canTakeShape(direction2, blockstate1, reader, pos)) {
            return direction2 == facing.getCounterClockWise() ? StairsShape.INNER_LEFT : StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean canTakeShape(Direction dir, BlockState state, BlockGetter reader, BlockPos pos) {
      BlockState blockstate = reader.getBlockState(pos.relative(dir));
      return !isSteepRoof(blockstate) || blockstate.getValue(FACING) != state.getValue(FACING) || blockstate.getValue(HALF) != state.getValue(HALF);
   }

   public static boolean isSteepRoof(BlockState state) {
      return state.getBlock() instanceof SteepRoof;
   }
}
