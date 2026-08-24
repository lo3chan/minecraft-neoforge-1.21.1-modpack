package vectorwing.farmersdelight.common.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class TatamiBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final BooleanProperty PAIRED = BooleanProperty.create("paired");

   public TatamiBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(FACING, Direction.DOWN)).setValue(PAIRED, false)
      );
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction face = context.getClickedFace();
      BlockPos targetPos = context.getClickedPos().relative(face.getOpposite());
      BlockState targetState = context.getLevel().getBlockState(targetPos);
      boolean pairing = false;
      if (context.getPlayer() != null && !context.getPlayer().isShiftKeyDown() && targetState.getBlock() == this && !(Boolean)targetState.getValue(PAIRED)) {
         pairing = true;
      }

      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite())).setValue(PAIRED, pairing);
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      if (!level.isClientSide) {
         if (placer != null && placer.isShiftKeyDown()) {
            return;
         }

         BlockPos facingPos = pos.relative((Direction)state.getValue(FACING));
         BlockState facingState = level.getBlockState(facingPos);
         if (facingState.getBlock() == this && !(Boolean)facingState.getValue(PAIRED)) {
            level.setBlock(
               facingPos, (BlockState)((BlockState)state.setValue(FACING, ((Direction)state.getValue(FACING)).getOpposite())).setValue(PAIRED, true), 3
            );
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
         }
      }
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      return facing.equals(state.getValue(FACING)) && state.getValue(PAIRED) && level.getBlockState(facingPos).getBlock() != this
         ? (BlockState)state.setValue(PAIRED, false)
         : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, PAIRED});
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }
}
