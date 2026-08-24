package io.github.razordevs.deep_aether.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class YagrootVineBlock extends VineBlock {
   public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

   public YagrootVineBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(BOTTOM, false))
                        .setValue(UP, false))
                     .setValue(NORTH, false))
                  .setValue(EAST, false))
               .setValue(SOUTH, false))
            .setValue(WEST, false)
      );
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState p_57877_, LevelAccessor level, BlockPos pos, BlockPos update) {
      boolean isBottom = !level.getBlockState(pos.below()).is(this);
      if (direction == Direction.DOWN) {
         return (BlockState)state.setValue(BOTTOM, isBottom);
      } else {
         BlockState blockstate = this.getUpdatedState(state, level, pos);
         return !this.hasFaces(blockstate) ? Blocks.AIR.defaultBlockState() : (BlockState)blockstate.setValue(BOTTOM, isBottom);
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> state) {
      state.add(new Property[]{BOTTOM});
      super.createBlockStateDefinition(state);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      boolean isBottom = !context.getLevel().getBlockState(context.getClickedPos().below()).is(this);
      BlockState state = super.getStateForPlacement(context);
      return state != null ? (BlockState)state.setValue(BOTTOM, isBottom) : null;
   }
}
