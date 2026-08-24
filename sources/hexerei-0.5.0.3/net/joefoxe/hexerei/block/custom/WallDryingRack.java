package net.joefoxe.hexerei.block.custom;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.tileentity.DryingRackTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallDryingRack extends HerbDryingRack {
   public static final VoxelShape SHAPE = Stream.of(
         Block.box(2.0, 6.0, 12.0, 14.0, 7.0, 14.0), Block.box(1.0, 5.0, 14.0, 15.0, 9.0, 15.0), Block.box(0.0, 4.0, 15.0, 16.0, 10.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   public static final VoxelShape SHAPE_90 = Stream.of(
         Block.box(2.0, 6.0, 2.0, 4.0, 7.0, 14.0), Block.box(1.0, 5.0, 1.0, 2.0, 9.0, 15.0), Block.box(0.0, 4.0, 0.0, 1.0, 10.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   public static final VoxelShape SHAPE_180 = Stream.of(
         Block.box(2.0, 6.0, 2.0, 14.0, 7.0, 4.0), Block.box(1.0, 5.0, 1.0, 15.0, 9.0, 2.0), Block.box(0.0, 4.0, 0.0, 16.0, 10.0, 1.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   public static final VoxelShape SHAPE_270 = Stream.of(
         Block.box(12.0, 6.0, 2.0, 14.0, 7.0, 14.0), Block.box(14.0, 5.0, 1.0, 15.0, 9.0, 15.0), Block.box(15.0, 4.0, 0.0, 16.0, 10.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   public static final VoxelShape SHAPE_LEFT = Shapes.join(
      Block.box(14.0, 6.0, 12.0, 16.0, 7.0, 14.0), Block.box(15.0, 5.0, 14.0, 16.0, 9.0, 15.0), BooleanOp.OR
   );
   public static final VoxelShape SHAPE_LEFT_90 = Shapes.join(
      Block.box(2.0, 6.0, 14.0, 4.0, 7.0, 16.0), Block.box(1.0, 5.0, 15.0, 2.0, 9.0, 16.0), BooleanOp.OR
   );
   public static final VoxelShape SHAPE_LEFT_180 = Shapes.join(Block.box(0.0, 6.0, 2.0, 2.0, 7.0, 4.0), Block.box(0.0, 5.0, 1.0, 1.0, 9.0, 2.0), BooleanOp.OR);
   public static final VoxelShape SHAPE_LEFT_270 = Shapes.join(
      Block.box(12.0, 6.0, 0.0, 14.0, 7.0, 2.0), Block.box(14.0, 5.0, 0.0, 15.0, 9.0, 1.0), BooleanOp.OR
   );
   public static final VoxelShape SHAPE_RIGHT = Shapes.join(Block.box(0.0, 6.0, 12.0, 2.0, 7.0, 14.0), Block.box(0.0, 5.0, 14.0, 1.0, 9.0, 15.0), BooleanOp.OR);
   public static final VoxelShape SHAPE_RIGHT_90 = Shapes.join(Block.box(2.0, 6.0, 0.0, 4.0, 7.0, 2.0), Block.box(1.0, 5.0, 0.0, 2.0, 9.0, 1.0), BooleanOp.OR);
   public static final VoxelShape SHAPE_RIGHT_180 = Shapes.join(
      Block.box(14.0, 6.0, 2.0, 16.0, 7.0, 4.0), Block.box(15.0, 5.0, 1.0, 16.0, 9.0, 2.0), BooleanOp.OR
   );
   public static final VoxelShape SHAPE_RIGHT_270 = Shapes.join(
      Block.box(12.0, 6.0, 14.0, 14.0, 7.0, 16.0), Block.box(14.0, 5.0, 15.0, 15.0, 9.0, 16.0), BooleanOp.OR
   );
   private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(
      ImmutableMap.of(Direction.SOUTH, SHAPE, Direction.NORTH, SHAPE_180, Direction.WEST, SHAPE_90, Direction.EAST, SHAPE_270)
   );
   private static final Map<Direction, VoxelShape> AABBS_LEFT = Maps.newEnumMap(
      ImmutableMap.of(Direction.SOUTH, SHAPE_LEFT, Direction.NORTH, SHAPE_LEFT_180, Direction.WEST, SHAPE_LEFT_90, Direction.EAST, SHAPE_LEFT_270)
   );
   private static final Map<Direction, VoxelShape> AABBS_RIGHT = Maps.newEnumMap(
      ImmutableMap.of(Direction.SOUTH, SHAPE_RIGHT, Direction.NORTH, SHAPE_RIGHT_180, Direction.WEST, SHAPE_RIGHT_90, Direction.EAST, SHAPE_RIGHT_270)
   );
   public static BooleanProperty WEST = BooleanProperty.create("west");
   public static BooleanProperty EAST = BooleanProperty.create("east");
   public static BooleanProperty NORTH = BooleanProperty.create("north");
   public static BooleanProperty SOUTH = BooleanProperty.create("south");
   public static BooleanProperty RIGHT = BooleanProperty.create("right");
   public static BooleanProperty LEFT = BooleanProperty.create("left");

   @Override
   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
      BlockGetter iblockreader = context.getLevel();
      BlockPos blockpos = context.getClickedPos();
      return (BlockState)((BlockState)this.updateSides(iblockreader, blockpos, super.getStateForPlacement(context))
            .setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()))
         .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   @Override
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      BlockEntity tileEntity = level.getBlockEntity(pos);
      if (tileEntity instanceof DryingRackTile) {
         ((DryingRackTile)tileEntity).interactDryingRack(player, hitResult);
         return ItemInteractionResult.SUCCESS;
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
   }

   @Override
   public VoxelShape getShape(BlockState pState, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
      boolean left = (Boolean)pState.getValue(LEFT);
      boolean right = (Boolean)pState.getValue(RIGHT);
      if (!left && right) {
         return Stream.of(AABBS_RIGHT.get(pState.getValue(HorizontalDirectionalBlock.FACING)), AABBS.get(pState.getValue(HorizontalDirectionalBlock.FACING)))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
            .get();
      } else if (!right && left) {
         return Stream.of(AABBS_LEFT.get(pState.getValue(HorizontalDirectionalBlock.FACING)), AABBS.get(pState.getValue(HorizontalDirectionalBlock.FACING)))
            .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
            .get();
      } else {
         return right && left
            ? Stream.of(
                  AABBS_LEFT.get(pState.getValue(HorizontalDirectionalBlock.FACING)),
                  AABBS_RIGHT.get(pState.getValue(HorizontalDirectionalBlock.FACING)),
                  AABBS.get(pState.getValue(HorizontalDirectionalBlock.FACING))
               )
               .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
               .get()
            : AABBS.get(pState.getValue(HorizontalDirectionalBlock.FACING));
      }
   }

   public WallDryingRack(Properties properties) {
      super(properties.noCollission());
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING, WATERLOGGED, NORTH, SOUTH, EAST, WEST, LEFT, RIGHT});
   }

   @Override
   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   protected BlockState updateSides(BlockGetter world, BlockPos pos, BlockState state) {
      BlockState bs_north = world.getBlockState(pos.north());
      BlockState bs_east = world.getBlockState(pos.east());
      BlockState bs_south = world.getBlockState(pos.south());
      BlockState bs_west = world.getBlockState(pos.west());
      Direction dir = (Direction)state.getValue(HorizontalDirectionalBlock.FACING);
      boolean north = bs_north.getBlock() == this
         && bs_north.hasProperty(HorizontalDirectionalBlock.FACING)
         && bs_north.getValue(HorizontalDirectionalBlock.FACING) == dir;
      boolean south = bs_south.getBlock() == this
         && bs_south.hasProperty(HorizontalDirectionalBlock.FACING)
         && bs_south.getValue(HorizontalDirectionalBlock.FACING) == dir;
      boolean east = bs_east.getBlock() == this
         && bs_east.hasProperty(HorizontalDirectionalBlock.FACING)
         && bs_east.getValue(HorizontalDirectionalBlock.FACING) == dir;
      boolean west = bs_west.getBlock() == this
         && bs_west.hasProperty(HorizontalDirectionalBlock.FACING)
         && bs_west.getValue(HorizontalDirectionalBlock.FACING) == dir;
      boolean left = dir == Direction.NORTH && west || dir == Direction.WEST && south || dir == Direction.SOUTH && east || dir == Direction.EAST && north;
      boolean right = dir == Direction.NORTH && east || dir == Direction.WEST && north || dir == Direction.SOUTH && west || dir == Direction.EAST && south;
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, north)).setValue(EAST, east))
                  .setValue(SOUTH, south))
               .setValue(WEST, west))
            .setValue(LEFT, left))
         .setValue(RIGHT, right);
   }

   @Override
   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate((Direction)pState.getValue(HorizontalDirectionalBlock.FACING)));
   }

   @Override
   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
      if (state.hasProperty(WATERLOGGED) && (Boolean)state.getValue(WATERLOGGED)) {
         world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
      }

      return super.updateShape(this.updateSides(world, pos, state), facing, facingState, world, pos, facingPos);
   }

   @Override
   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Override
   public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
      return pLevel.getBlockState(pPos.relative((Direction)pState.getValue(HorizontalDirectionalBlock.FACING))).isSolid();
   }

   @Override
   public Class<DryingRackTile> getTileEntityClass() {
      return DryingRackTile.class;
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new DryingRackTile((BlockEntityType<?>)ModTileEntities.DRYING_RACK_TILE.get(), pos, state);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
      return entityType == ModTileEntities.DRYING_RACK_TILE.get() ? (world2, pos, state2, entity) -> ((DryingRackTile)entity).tick() : null;
   }
}
