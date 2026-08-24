package com.mcwwindows.kikoz.objects;

import com.mcwwindows.kikoz.util.WindowPart;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class WindowBase extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<WindowPart> PART = EnumProperty.create("part", WindowPart.class);

   public WindowBase(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   public void onBroken(Level worldIn, BlockPos pos) {
      worldIn.levelEvent(1029, pos, 0);
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public boolean allowsMovement(BlockState state, LevelAccessor worldIn, BlockPos pos) {
      return false;
   }

   protected BlockState WindowState(BlockState state, LevelAccessor level, BlockPos pos) {
      boolean above = level.getBlockState(pos.above()).getBlock() == this;
      boolean below = level.getBlockState(pos.below()).getBlock() == this;
      if (above && below) {
         return (BlockState)state.setValue(PART, WindowPart.MIDDLE);
      } else if (!above && below) {
         return (BlockState)state.setValue(PART, WindowPart.TOP);
      } else {
         return above && !below ? (BlockState)state.setValue(PART, WindowPart.BOTTOM) : (BlockState)state.setValue(PART, WindowPart.BASE);
      }
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!statetwo.is(state.getBlock())) {
         this.WindowState(state, level, pos);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.WindowState(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos())
         .setValue(FACING, context.getHorizontalDirection().getCounterClockWise());
   }

   public void placeAt(Level level, BlockPos pos, int num) {
      level.setBlock(pos, this.defaultBlockState(), num);
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor level, BlockPos pos, BlockPos newPos) {
      return this.WindowState(state, level, pos);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, PART});
   }

   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, Level worldIn, BlockPos currentPos, BlockPos facingPos) {
      return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }
}
