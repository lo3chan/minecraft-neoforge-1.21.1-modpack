package com.mcwwindows.kikoz.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Parapet extends Block {
   public static final EnumProperty<Parapet.Flower> FLOWER = EnumProperty.create("part", Parapet.Flower.class);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   protected static final VoxelShape SOUTH = Shapes.or(box(9.0, 12.0, 0.0, 16.0, 16.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape EAST = Shapes.or(box(0.0, 12.0, 0.0, 16.0, 16.0, 7.0), new VoxelShape[0]);
   protected static final VoxelShape WEST = Shapes.or(box(0.0, 12.0, 9.0, 16.0, 16.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape NORTH = Shapes.or(box(0.0, 12.0, 0.0, 7.0, 16.0, 16.0), new VoxelShape[0]);

   public Parapet(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(FLOWER, Parapet.Flower.EMPTY)
      );
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case WEST:
            return WEST;
         case EAST:
            return EAST;
         case SOUTH:
            return SOUTH;
         case NORTH:
         default:
            return NORTH;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.defaultBlockState()
            .setValue(FLOWER, Parapet.Flower.byState(context.getLevel().getBlockState(context.getClickedPos().above()))))
         .setValue(FACING, context.getHorizontalDirection().getClockWise());
   }

   public BlockState updateShape(BlockState state, Direction dir, BlockState statetwo, LevelAccessor level, BlockPos pos, BlockPos postwo) {
      return dir == Direction.UP
         ? (BlockState)state.setValue(FLOWER, Parapet.Flower.byState(statetwo))
         : super.updateShape(state, dir, statetwo, level, pos, postwo);
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, FLOWER});
   }

   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, Level worldIn, BlockPos currentPos, BlockPos facingPos) {
      return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public static enum Flower implements StringRepresentable {
      FLOWER("flower"),
      EMPTY("empty");

      private final String name;

      private Flower(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static Parapet.Flower byState(BlockState state) {
         return state.is(BlockTags.FLOWER_POTS) ? FLOWER : EMPTY;
      }
   }
}
