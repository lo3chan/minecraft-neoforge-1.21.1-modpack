package net.bobophones.bobolib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
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

public class HorizontalDirectionWaterloggableBlock extends HorizontalDirectionBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty waterlogged = BlockStateProperties.WATERLOGGED;

   public HorizontalDirectionWaterloggableBlock(Properties props) {
      super(props);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(facing, Direction.NORTH)).setValue(waterlogged, false)
      );
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder.add(new Property[]{waterlogged}));
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      boolean flag = ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER;
      return (BlockState)((BlockState)super.getStateForPlacement(ctx).setValue(facing, ctx.getHorizontalDirection().getOpposite())).setValue(waterlogged, flag);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(waterlogged) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState neighbor_state, LevelAccessor level, BlockPos pos, BlockPos neighbor_pos) {
      if ((Boolean)state.getValue(waterlogged)) {
         level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, facing, neighbor_state, level, pos, neighbor_pos);
   }
}
