package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import java.util.concurrent.atomic.AtomicReference;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RootedItemBlock extends BaseEntityBlock {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   private static final MapCodec<RootedItemBlock> CODEC = simpleCodec(RootedItemBlock::new);

   public RootedItemBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(ROTATION, 0));
   }

   public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return Shapes.empty();
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{ROTATION});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)super.getStateForPlacement(context).setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation()));
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (level.getBlockEntity(pos) instanceof RootedItemBlockEntity root) {
         ItemStack rootedItem = root.getItem();
         AtomicReference<ItemInteractionResult> result = new AtomicReference<>(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
         InfusedPropertiesHelper.forEachProperty(
            rootedItem,
            propertyHolder -> {
               ItemInteractionResult propertyResult = ((net.cibernet.alchemancy.properties.Property)propertyHolder.value())
                  .onRootedRightClick(root, player, hand, hitResult);
               if (propertyResult != null) {
                  result.set(propertyResult);
               }
            }
         );
         return result.get();
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new RootedItemBlockEntity(pPos, pState);
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return null;
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean pIsMoving) {
      Containers.dropContentsOnDestroy(state, newState, level, pos);
      super.onRemove(state, level, pos, newState, pIsMoving);
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
      return level.getBlockEntity(pos) instanceof ItemStackHolderBlockEntity blockEntity
         ? blockEntity.getItem().copy()
         : super.getCloneItemStack(state, target, level, pos, player);
   }

   public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
      if (pLevel.getBlockEntity(pPos) instanceof RootedItemBlockEntity root) {
         InfusedPropertiesHelper.forEachProperty(
            root.getItem(), propertyHolder -> ((net.cibernet.alchemancy.properties.Property)propertyHolder.value()).onRootedAnimateTick(root, pRandom)
         );
      }

      super.animateTick(pState, pLevel, pPos, pRandom);
   }

   @javax.annotation.Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return pLevel.isClientSide
         ? null
         : createTickerHelper(pBlockEntityType, (BlockEntityType)AlchemancyBlockEntities.ROOTED_ITEM.get(), RootedItemBlockEntity::serverTick);
   }

   public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
      return pState.getFluidState().isEmpty();
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return pathComputationType == PathComputationType.AIR && !this.hasCollision || super.isPathfindable(state, pathComputationType);
   }

   public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
      return !pState.canSurvive(pLevel, pCurrentPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
   }

   public boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return pState.is(BlockTags.DIRT) || pState.is(Blocks.FARMLAND);
   }

   public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
      BlockPos blockpos = pPos.below();
      return this.mayPlaceOn(pLevel.getBlockState(blockpos), pLevel, blockpos);
   }

   @Nullable
   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.DESTROY;
   }
}
