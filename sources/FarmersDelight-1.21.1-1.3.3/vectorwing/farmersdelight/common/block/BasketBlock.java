package vectorwing.farmersdelight.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.entity.BasketBlockEntity;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;

public class BasketBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<BasketBlock> CODEC = simpleCodec(BasketBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final VoxelShape OUT_SHAPE = Shapes.block();
   public static final VoxelShape RENDER_SHAPE = box(1.0, 1.0, 1.0, 15.0, 15.0, 15.0);
   public static final ImmutableMap<Direction, VoxelShape> COLLISION_SHAPE_FACING = Maps.immutableEnumMap(
      ImmutableMap.builder()
         .put(Direction.DOWN, makeHollowCubeShape(box(2.0, 0.0, 2.0, 14.0, 14.0, 14.0)))
         .put(Direction.UP, makeHollowCubeShape(box(2.0, 2.0, 2.0, 14.0, 16.0, 14.0)))
         .put(Direction.NORTH, makeHollowCubeShape(box(2.0, 2.0, 0.0, 14.0, 14.0, 14.0)))
         .put(Direction.SOUTH, makeHollowCubeShape(box(2.0, 2.0, 2.0, 14.0, 14.0, 16.0)))
         .put(Direction.WEST, makeHollowCubeShape(box(0.0, 2.0, 2.0, 14.0, 14.0, 14.0)))
         .put(Direction.EAST, makeHollowCubeShape(box(2.0, 2.0, 2.0, 16.0, 14.0, 14.0)))
         .build()
   );

   private static VoxelShape makeHollowCubeShape(VoxelShape cutout) {
      return Shapes.joinUnoptimized(OUT_SHAPE, cutout, BooleanOp.ONLY_FIRST).optimize();
   }

   public BasketBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(FACING, Direction.UP)).setValue(WATERLOGGED, false)
      );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, ENABLED, WATERLOGGED});
   }

   public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (level.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         if (level.getBlockEntity(pos) instanceof BasketBlockEntity basket) {
            player.openMenu(basket);
         }

         return InteractionResult.CONSUME;
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         if (level.getBlockEntity(pos) instanceof BasketBlockEntity basket) {
            Containers.dropContents(level, pos, basket);
            level.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
      boolean isPowered = !level.hasNeighborSignal(pos);
      if (isPowered != (Boolean)state.getValue(ENABLED)) {
         level.setBlock(pos, (BlockState)state.setValue(ENABLED, isPowered), 4);
      }
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()))
         .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
   }

   public boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return (VoxelShape)COLLISION_SHAPE_FACING.get(state.getValue(FACING));
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
      return RENDER_SHAPE;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
      return OUT_SHAPE;
   }

   protected boolean isPathfindable(BlockState state, PathComputationType type) {
      return false;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new BasketBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return level.isClientSide
         ? null
         : createTickerHelper(blockEntityType, (BlockEntityType)ModBlockEntityTypes.BASKET.get(), BasketBlockEntity::pushItemsTick);
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }
}
