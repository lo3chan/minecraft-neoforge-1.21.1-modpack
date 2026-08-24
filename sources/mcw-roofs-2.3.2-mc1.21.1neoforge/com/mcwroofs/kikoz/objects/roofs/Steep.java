package com.mcwroofs.kikoz.objects.roofs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Steep extends RoofBlock {
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{
      Block.box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
      Block.box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
      Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 8.0),
      Block.box(0.0, 0.0, 8.0, 8.0, 16.0, 16.0),
      Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 8.0),
      Block.box(0.0, 0.0, 8.0, 8.0, 16.0, 16.0),
      Block.box(8.0, 0.0, 0.0, 16.0, 16.0, 8.0),
      Block.box(8.0, 0.0, 8.0, 16.0, 16.0, 16.0),
      Block.box(8.0, 0.0, 0.0, 16.0, 16.0, 8.0),
      Block.box(8.0, 0.0, 8.0, 16.0, 16.0, 16.0)
   };
   protected static final VoxelShape OCCLUSION = Block.box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   protected static final VoxelShape[] TOP_SHAPES = createStairShapes(SHAPES[0], SHAPES[2], SHAPES[6], SHAPES[3], SHAPES[7]);
   protected static final VoxelShape[] BOTTOM_SHAPES = createStairShapes(SHAPES[1], SHAPES[4], SHAPES[8], SHAPES[5], SHAPES[9]);
   private static final int[] SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};

   private static VoxelShape[] createStairShapes(VoxelShape baseShape, VoxelShape... shapes) {
      VoxelShape[] stairShapes = new VoxelShape[16];

      for (int i = 0; i < 16; i++) {
         stairShapes[i] = baseShape;

         for (int j = 0; j < shapes.length; j++) {
            if ((i & 1 << j) != 0) {
               stairShapes[i] = Shapes.or(stairShapes[i], shapes[j]);
            }
         }
      }

      return stairShapes;
   }

   public Steep(BlockState state, Properties prop) {
      super(prop);
   }

   @Override
   public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos) {
      return OCCLUSION;
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext collision) {
      VoxelShape[] shapes = state.getValue(HALF) == Half.TOP ? TOP_SHAPES : BOTTOM_SHAPES;
      int shapeIndex = this.getShapeIndex(state);
      return shapes[SHAPE_BY_STATE[shapeIndex]];
   }

   private int getShapeIndex(BlockState state) {
      return ((StairsShape)state.getValue(SHAPE)).ordinal() * 4 + ((Direction)state.getValue(FACING)).get2DDataValue();
   }

   @Override
   public BlockState updateShape(BlockState state, Direction dir, BlockState statetwo, LevelAccessor access, BlockPos pos, BlockPos postwo) {
      return dir.getAxis().isHorizontal()
         ? (BlockState)state.setValue(SHAPE, getStairsShape(access, pos, (Direction)state.getValue(FACING), (Half)state.getValue(HALF)))
         : super.updateShape(state, dir, statetwo, access, pos, postwo);
   }

   private static StairsShape getStairsShape(BlockGetter reader, BlockPos pos, Direction facing, Half half) {
      BlockState blockstate = reader.getBlockState(pos.relative(facing));
      if (isSteep(blockstate) && half == blockstate.getValue(HALF)) {
         Direction direction1 = (Direction)blockstate.getValue(FACING);
         if (direction1.getAxis() != facing.getAxis() && canTakeShape(direction1.getOpposite(), blockstate, reader, pos)) {
            return direction1 == facing.getCounterClockWise() ? StairsShape.OUTER_LEFT : StairsShape.OUTER_RIGHT;
         }
      }

      BlockState blockstate1 = reader.getBlockState(pos.relative(facing.getOpposite()));
      if (isSteep(blockstate1) && half == blockstate1.getValue(HALF)) {
         Direction direction2 = (Direction)blockstate1.getValue(FACING);
         if (direction2.getAxis() != facing.getAxis() && canTakeShape(direction2, blockstate1, reader, pos)) {
            return direction2 == facing.getCounterClockWise() ? StairsShape.INNER_LEFT : StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean canTakeShape(Direction dir, BlockState state, BlockGetter reader, BlockPos pos) {
      BlockState blockstate = reader.getBlockState(pos.relative(dir));
      return !isSteep(blockstate) || blockstate.getValue(FACING) != state.getValue(FACING) || blockstate.getValue(HALF) != state.getValue(HALF);
   }

   public static boolean isSteep(BlockState state) {
      return state.getBlock() instanceof Steep;
   }
}
