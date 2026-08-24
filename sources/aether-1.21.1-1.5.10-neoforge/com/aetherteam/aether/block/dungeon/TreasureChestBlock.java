package com.aetherteam.aether.block.dungeon;

import com.aetherteam.aether.blockentity.AetherBlockEntityTypes;
import com.aetherteam.aether.blockentity.TreasureChestBlockEntity;
import com.aetherteam.aether.item.components.AetherDataComponents;
import com.aetherteam.aether.item.components.DungeonKind;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner.Combiner;
import net.minecraft.world.level.block.DoubleBlockCombiner.NeighborCombineResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;

public class TreasureChestBlock extends AbstractChestBlock<TreasureChestBlockEntity> implements SimpleWaterloggedBlock {
   public static final MapCodec<TreasureChestBlock> CODEC = simpleCodec(TreasureChestBlock::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);

   public TreasureChestBlock(Properties properties) {
      this(properties, AetherBlockEntityTypes.TREASURE_CHEST::get);
   }

   public TreasureChestBlock(Properties properties, Supplier<BlockEntityType<? extends TreasureChestBlockEntity>> blockEntityTypeSupplier) {
      super(properties, blockEntityTypeSupplier);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false)
      );
   }

   protected MapCodec<? extends AbstractChestBlock<TreasureChestBlockEntity>> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, WATERLOGGED});
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TreasureChestBlockEntity(pos, state);
   }

   public BlockEntityType<? extends TreasureChestBlockEntity> blockEntityType() {
      return (BlockEntityType<? extends TreasureChestBlockEntity>)this.blockEntityType.get();
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return level.isClientSide() ? createTickerHelper(blockEntityType, this.blockEntityType(), TreasureChestBlockEntity::lidAnimateTick) : null;
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (level.getBlockEntity(pos) instanceof TreasureChestBlockEntity treasureChestBlockEntity) {
         treasureChestBlockEntity.recheckOpen();
      }
   }

   public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (!(level.getBlockEntity(pos) instanceof TreasureChestBlockEntity treasureChestBlockEntity && !treasureChestBlockEntity.getLocked())) {
         return InteractionResult.sidedSuccess(level.isClientSide());
      } else if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         MenuProvider menuprovider = this.getMenuProvider(state, level, pos);
         if (menuprovider != null) {
            player.openMenu(menuprovider);
            player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
            PiglinAi.angerNearbyPiglins(player, true);
         }

         return InteractionResult.CONSUME;
      }
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (level.getBlockEntity(pos) instanceof TreasureChestBlockEntity treasureChestBlockEntity) {
         ResourceLocation kind = treasureChestBlockEntity.getKind();
         if (treasureChestBlockEntity.getLocked()) {
            DungeonKind type = (DungeonKind)stack.get(AetherDataComponents.DUNGEON_KIND);
            if (type != null && type.id().equals(treasureChestBlockEntity.getKind()) && !stack.isEmpty() && treasureChestBlockEntity.tryUnlock(player)) {
               if (player instanceof ServerPlayer) {
                  player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
               }

               if (!player.getAbilities().instabuild) {
                  stack.shrink(1);
               }

               return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }

            player.displayClientMessage(Component.translatable(kind.getNamespace() + "." + kind.getPath() + "_treasure_chest_locked"), true);
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction direction = context.getHorizontalDirection().getOpposite();
      FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, direction)).setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState stateOther, boolean flag) {
      if (!state.is(stateOther.getBlock())) {
         if (level.getBlockEntity(pos) instanceof Container container) {
            Containers.dropContents(level, pos, container);
            level.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, level, pos, stateOther, flag);
      }
   }

   public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof TreasureChestBlockEntity treasureChestBlockEntity) {
         float f = treasureChestBlockEntity.getLocked() ? state.getDestroySpeed(level, pos) : 3.0F;
         if (f < 0.0F) {
            return 0.0F;
         } else {
            int i = EventHooks.doPlayerHarvestCheck(player, state, level, pos) ? 30 : 100;
            return player.getDigSpeed(state, pos) / f / i;
         }
      } else {
         return super.getDestroyProgress(state, player, level, pos);
      }
   }

   public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
      if (level.getBlockEntity(pos) instanceof TreasureChestBlockEntity treasureChestBlockEntity) {
         return treasureChestBlockEntity.getLocked() ? super.getExplosionResistance(state, level, pos, explosion) : 3.0F;
      } else {
         return super.getExplosionResistance(state, level, pos, explosion);
      }
   }

   public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   public NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState state, Level level, BlockPos pos, boolean flag) {
      return Combiner::acceptNone;
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      ItemStack stack = super.getCloneItemStack(level, pos, state);
      TreasureChestBlockEntity treasureChestBlockEntity = (TreasureChestBlockEntity)level.getBlockEntity(pos);
      if (treasureChestBlockEntity != null) {
         stack.applyComponents(treasureChestBlockEntity.collectComponents());
      }

      return stack;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, direction, facingState, level, currentPos, facingPos);
   }
}
