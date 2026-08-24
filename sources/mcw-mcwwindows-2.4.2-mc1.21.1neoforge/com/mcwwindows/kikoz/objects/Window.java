package com.mcwwindows.kikoz.objects;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Window extends WindowBarred {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<Window.ExtendablePart> PART = EnumProperty.create("part", Window.ExtendablePart.class);
   protected static final VoxelShape EE = box(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);
   protected static final VoxelShape NN = box(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);

   public Window(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH))
               .setValue(PART, Window.ExtendablePart.BASE))
            .setValue(WINDOWSTATE, WindowBarred.WindowState.CLOSED)
      );
   }

   protected BlockState WindowState(BlockState state, LevelAccessor level, BlockPos pos) {
      boolean above = level.getBlockState(pos.above()).getBlock() == this;
      boolean below = level.getBlockState(pos.below()).getBlock() == this;
      if (above && below) {
         return (BlockState)state.setValue(PART, Window.ExtendablePart.MIDDLE);
      } else if (!above && below) {
         return (BlockState)state.setValue(PART, Window.ExtendablePart.ABOVE);
      } else {
         return above && !below ? (BlockState)state.setValue(PART, Window.ExtendablePart.BELOW) : (BlockState)state.setValue(PART, Window.ExtendablePart.BASE);
      }
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!statetwo.is(state.getBlock())) {
         this.WindowState(state, level, pos);
      }
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.WindowState(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos())
         .setValue(FACING, context.getHorizontalDirection());
   }

   public void placeAt(Level level, BlockPos pos, int num) {
      level.setBlock(pos, this.defaultBlockState(), num);
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor level, BlockPos pos, BlockPos newPos) {
      return this.WindowState(state, level, pos);
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, PART, WINDOWSTATE});
   }

   public static enum ExtendablePart implements StringRepresentable {
      BASE("base"),
      ABOVE("above"),
      MIDDLE("middle"),
      BELOW("below");

      private final String name;

      private ExtendablePart(final String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public String getString() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
