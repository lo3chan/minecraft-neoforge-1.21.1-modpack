package net.astralya.hexalia.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
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
import org.jetbrains.annotations.Nullable;

public class SaltLampBlock extends LanternBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
   private static final VoxelShape STANDING_SHAPE = Shapes.or(Shapes.box(0.25, 0.0, 0.25, 0.75, 0.6875, 0.75), new VoxelShape[0]);
   private static final VoxelShape HANGING_SHAPE = Shapes.or(
      Shapes.box(0.25, 0.0, 0.25, 0.75, 0.6875, 0.75), Shapes.box(0.4375, 0.625, 0.4375, 0.5625, 1.0625, 0.5625)
   );

   public SaltLampBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(HANGING, false)).setValue(WATERLOGGED, false));
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return state.getValue(HANGING) ? HANGING_SHAPE : STANDING_SHAPE;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());

      for (Direction direction : context.getNearestLookingDirections()) {
         if (direction.getAxis() == Axis.Y) {
            BlockState state = (BlockState)this.defaultBlockState().setValue(HANGING, direction == Direction.UP);
            if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
               return (BlockState)state.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
            }
         }
      }

      return null;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HANGING, WATERLOGGED});
   }
}
