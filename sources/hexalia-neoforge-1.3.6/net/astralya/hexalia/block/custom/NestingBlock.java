package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.MenuRegistry;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.NestingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class NestingBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final MapCodec<NestingBlock> CODEC = simpleCodec(NestingBlock::new);
   public static final VoxelShape SHAPE = Shapes.or(
      Shapes.box(0.0, 0.125, 0.0, 1.0, 1.0, 1.0),
      new VoxelShape[]{
         Shapes.box(0.0, 0.0, 0.8125, 0.1875, 0.125, 1.0),
         Shapes.box(0.0, 0.0, 0.0, 0.1875, 0.125, 0.1875),
         Shapes.box(0.8125, 0.0, 0.0, 1.0, 0.125, 0.1875),
         Shapes.box(0.8125, 0.0, 0.8125, 1.0, 0.125, 1.0)
      }
   );

   public NestingBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new NestingBlockEntity(pos, state);
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (level.isClientSide) {
         return InteractionResult.SUCCESS;
      } else if (level.getBlockEntity(pos) instanceof NestingBlockEntity nest) {
         MenuRegistry.openExtendedMenu((ServerPlayer)player, nest);
         player.awardStat(Stats.OPEN_CHEST);
         level.gameEvent(player, GameEvent.CONTAINER_OPEN, pos);
         return InteractionResult.CONSUME;
      } else {
         throw new IllegalStateException("NestingBlock menu provider is missing!");
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
      if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof NestingBlockEntity nest) {
         Containers.dropContents(level, pos, nest);
         level.updateNeighbourForOutputSignal(pos, this);
      }

      super.onRemove(state, level, pos, newState, movedByPiston);
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
      return level.getBlockEntity(pos) instanceof NestingBlockEntity nest ? AbstractContainerMenu.getRedstoneSignalFromContainer(nest) : 0;
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.DESTROY;
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return level.isClientSide
         ? createTickerHelper(type, (BlockEntityType)ModBlockEntityTypes.NESTING_BLOCK.get(), NestingBlockEntity::clientTick)
         : createTickerHelper(type, (BlockEntityType)ModBlockEntityTypes.NESTING_BLOCK.get(), NestingBlockEntity::serverTick);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }
}
