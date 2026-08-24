package com.mcwfurnitures.kikoz.objects;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TableHitbox extends Block {
   public static final BooleanProperty NORTH = BooleanProperty.create("north");
   public static final BooleanProperty EAST = BooleanProperty.create("east");
   public static final BooleanProperty SOUTH = BooleanProperty.create("south");
   public static final BooleanProperty WEST = BooleanProperty.create("west");
   protected static final VoxelShape CORNER_N = Shapes.or(
      Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0), new VoxelShape[]{Block.box(12.0, 0.0, 1.0, 15.0, 11.0, 4.0), Block.box(0.0, 11.0, 1.0, 15.0, 14.0, 16.0)}
   );
   protected static final VoxelShape CORNER_E = Shapes.or(
      Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0), new VoxelShape[]{Block.box(12.0, 0.0, 12.0, 15.0, 11.0, 15.0), Block.box(0.0, 11.0, 0.0, 15.0, 14.0, 15.0)}
   );
   protected static final VoxelShape CORNER_S = Shapes.or(
      Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0), new VoxelShape[]{Block.box(1.0, 0.0, 12.0, 4.0, 11.0, 15.0), Block.box(1.0, 11.0, 0.0, 16.0, 14.0, 15.0)}
   );
   protected static final VoxelShape CORNER_W = Shapes.or(
      Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0), new VoxelShape[]{Block.box(1.0, 0.0, 1.0, 4.0, 11.0, 4.0), Block.box(1.0, 11.0, 1.0, 16.0, 14.0, 16.0)}
   );
   protected static final VoxelShape SIDE_N = Shapes.or(
      Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0),
      new VoxelShape[]{
         Block.box(12.0, 0.0, 1.0, 15.0, 14.0, 4.0),
         Block.box(12.0, 0.0, 12.0, 15.0, 14.0, 15.0),
         Block.box(12.0, 11.0, 4.0, 15.0, 14.0, 12.0),
         Block.box(0.0, 11.0, 1.0, 12.0, 14.0, 15.0)
      }
   );
   protected static final VoxelShape SIDE_E = Shapes.or(
      Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0),
      new VoxelShape[]{
         Block.box(12.0, 0.0, 12.0, 15.0, 14.0, 15.0),
         Block.box(1.0, 0.0, 12.0, 4.0, 14.0, 15.0),
         Block.box(4.0, 11.0, 12.0, 12.0, 14.0, 15.0),
         Block.box(1.0, 11.0, 0.0, 15.0, 14.0, 12.0)
      }
   );
   protected static final VoxelShape SIDE_S = Shapes.or(
      Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0),
      new VoxelShape[]{
         Block.box(1.0, 0.0, 12.0, 4.0, 14.0, 15.0),
         Block.box(1.0, 0.0, 1.0, 4.0, 14.0, 4.0),
         Block.box(1.0, 11.0, 4.0, 4.0, 14.0, 12.0),
         Block.box(4.0, 11.0, 1.0, 16.0, 14.0, 15.0)
      }
   );
   protected static final VoxelShape SIDE_W = Shapes.or(
      Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0),
      new VoxelShape[]{
         Block.box(1.0, 0.0, 1.0, 4.0, 14.0, 4.0),
         Block.box(12.0, 0.0, 1.0, 15.0, 14.0, 4.0),
         Block.box(4.0, 11.0, 1.0, 12.0, 14.0, 4.0),
         Block.box(1.0, 11.0, 4.0, 15.0, 14.0, 16.0)
      }
   );
   protected static final VoxelShape SINGLE = Shapes.or(
      Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0),
      new VoxelShape[]{
         Block.box(12.0, 0.0, 1.0, 15.0, 11.0, 4.0),
         Block.box(12.0, 0.0, 12.0, 15.0, 11.0, 15.0),
         Block.box(1.0, 0.0, 1.0, 4.0, 11.0, 4.0),
         Block.box(1.0, 0.0, 12.0, 4.0, 11.0, 15.0),
         Block.box(1.0, 11.0, 1.0, 15.0, 14.0, 15.0)
      }
   );
   protected static final VoxelShape MIDDLE_NS = Shapes.or(Block.box(0.0, 11.0, 1.0, 16.0, 14.0, 15.0), Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0));
   protected static final VoxelShape MIDDLE_WE = Shapes.or(Block.box(1.0, 11.0, 0.0, 15.0, 14.0, 16.0), Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0));
   protected static final VoxelShape CENTER = Block.box(0.0, 11.0, 0.0, 16.0, 16.0, 16.0);

   public TableHitbox(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false))
               .setValue(SOUTH, false))
            .setValue(WEST, false)
      );
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      boolean north = (Boolean)state.getValue(NORTH);
      boolean east = (Boolean)state.getValue(EAST);
      boolean south = (Boolean)state.getValue(SOUTH);
      boolean west = (Boolean)state.getValue(WEST);
      switch ((north ? 1 : 0) << 3 | (east ? 1 : 0) << 2 | (south ? 1 : 0) << 1 | (west ? 1 : 0)) {
         case 0:
            return SINGLE;
         case 1:
            return SIDE_N;
         case 2:
            return SIDE_W;
         case 3:
            return CORNER_N;
         case 4:
            return SIDE_S;
         case 5:
            return MIDDLE_WE;
         case 6:
            return CORNER_W;
         case 7:
            return MIDDLE_NS;
         case 8:
            return SIDE_E;
         case 9:
            return CORNER_E;
         case 10:
         case 11:
         default:
            return CENTER;
         case 12:
            return CORNER_S;
      }
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livent, ItemStack stack) {
      this.TableState(state, level, pos);
   }

   private BlockState TableState(BlockState state, LevelAccessor level, BlockPos pos) {
      boolean north = level.getBlockState(pos.north()).getBlock() == this;
      boolean east = level.getBlockState(pos.east()).getBlock() == this;
      boolean south = level.getBlockState(pos.south()).getBlock() == this;
      boolean west = level.getBlockState(pos.west()).getBlock() == this;
      return (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, north)).setValue(EAST, east)).setValue(SOUTH, south))
         .setValue(WEST, west);
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bolean) {
      if (!statetwo.is(state.getBlock())) {
         this.TableState(state, level, pos);
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{NORTH, EAST, SOUTH, WEST});
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor level, BlockPos pos, BlockPos newPos) {
      return this.TableState(state, level, pos);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.TableState(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos());
   }

   public void placeAt(Level level, BlockPos pos, int number) {
      level.setBlock(pos, this.defaultBlockState(), number);
   }
}
