package io.github.razordevs.deep_aether.block.natural;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmptyLog extends DALogBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final VoxelShape SHAPE_BOTTOM = box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
   private static final VoxelShape SHAPE_TOP = box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_NORTH = box(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_SOUTH = box(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_EAST = box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
   private static final VoxelShape SHAPE_WEST = box(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_X = Shapes.or(SHAPE_BOTTOM, new VoxelShape[]{SHAPE_TOP, SHAPE_EAST, SHAPE_WEST});
   private static final VoxelShape SHAPE_Y = Shapes.or(SHAPE_NORTH, new VoxelShape[]{SHAPE_SOUTH, SHAPE_EAST, SHAPE_WEST});
   private static final VoxelShape SHAPE_Z = Shapes.or(SHAPE_BOTTOM, new VoxelShape[]{SHAPE_TOP, SHAPE_NORTH, SHAPE_SOUTH});

   public EmptyLog(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(WATERLOGGED, false));
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
      return switch ((Axis)state.getValue(AXIS)) {
         case X -> SHAPE_X;
         case Y -> SHAPE_Y;
         case Z -> SHAPE_Z;
         default -> throw new MatchException(null, null);
      };
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      return state == null
         ? null
         : (BlockState)state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
   }

   public boolean propagatesSkylightDown(BlockState state, @Nonnull BlockGetter reader, @Nonnull BlockPos pos) {
      return !(Boolean)state.getValue(WATERLOGGED) && state.getValue(AXIS) == Axis.Y;
   }

   @Nonnull
   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Nonnull
   public BlockState updateShape(
      BlockState state,
      @Nonnull Direction facing,
      @Nonnull BlockState facingState,
      @Nonnull LevelAccessor level,
      @Nonnull BlockPos pos,
      @Nonnull BlockPos facingPos
   ) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, facing, facingState, level, pos, facingPos);
   }

   public boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> def) {
      super.createBlockStateDefinition(def);
      def.add(new Property[]{WATERLOGGED});
   }

   public boolean hasDynamicShape() {
      return true;
   }
}
