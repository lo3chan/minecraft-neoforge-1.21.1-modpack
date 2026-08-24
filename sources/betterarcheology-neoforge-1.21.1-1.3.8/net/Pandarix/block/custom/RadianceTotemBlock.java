package net.Pandarix.block.custom;

import java.util.List;
import net.Pandarix.block.entity.ModBlockEntities;
import net.Pandarix.block.entity.RadianceTotemBlockEntity;
import net.Pandarix.config.BAConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RadianceTotemBlock extends FossilBaseWithEntityBlock {
   public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final IntegerProperty SELECTOR = IntegerProperty.create("selector", 0, 3);
   protected static final VoxelShape AABB = Shapes.or(Block.box(5.0, 0.0, 5.0, 11.0, 7.0, 11.0), Block.box(6.0, 7.0, 6.0, 10.0, 9.0, 10.0));
   protected static final VoxelShape HANGING_AABB = Shapes.or(Block.box(5.0, 1.0, 5.0, 11.0, 8.0, 11.0), Block.box(6.0, 8.0, 6.0, 10.0, 10.0, 10.0));

   public RadianceTotemBlock(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(SELECTOR, 0));
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return createTickerHelper(type, (BlockEntityType)ModBlockEntities.RADIANCE_TOTEM.get(), RadianceTotemBlockEntity::tick);
   }

   @NotNull
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new RadianceTotemBlockEntity(pos, state);
   }

   public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
      super.animateTick(pState, pLevel, pPos, pRandom);
      if (pRandom.nextBoolean() && pLevel.isClientSide()) {
         pLevel.addParticle(
            ParticleTypes.GLOW,
            pPos.getCenter().x() + randomDirectionModifier(pRandom, 3),
            pPos.getCenter().y() - 0.25 + randomDirectionModifier(pRandom, 5),
            pPos.getCenter().z() + randomDirectionModifier(pRandom, 3),
            0.0,
            -4.0,
            0.0
         );
      }
   }

   @NotNull
   @Override
   public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
      if (!pState.is(this)) {
         return InteractionResult.PASS;
      } else if (BAConfig.radianceTotemEnabled && BAConfig.totemsEnabled) {
         BlockState newState = (BlockState)pState.cycle(SELECTOR);
         pLevel.setBlock(pPos, newState, 3);
         if (pLevel.isClientSide()) {
            pPlayer.displayClientMessage(
               Component.translatable("block.betterarcheology.radiance_totem_message_" + newState.getValue(SELECTOR)).withStyle(ChatFormatting.GREEN), true
            );
            pLevel.playLocalSound(pPos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.5F, 0.5F, false);
            RandomSource random = pLevel.getRandom();

            for (int i = 0; i <= 10; i++) {
               pLevel.addParticle(
                  ParticleTypes.GLOW,
                  pPos.getCenter().x() + randomDirectionModifier(random, 3),
                  pPos.getCenter().y() - 0.25 + randomDirectionModifier(random, 5),
                  pPos.getCenter().z() + randomDirectionModifier(random, 3),
                  0.0,
                  -4.0,
                  0.0
               );
            }
         }

         return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
      } else {
         if (pLevel.isClientSide()) {
            pPlayer.displayClientMessage(Component.translatableWithFallback("config.notify.disabled", "This feature has been disabled in the config!"), true);
         }

         return InteractionResult.PASS;
      }
   }

   public boolean isRandomlyTicking(BlockState pState) {
      return true;
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      super.tick(pState, pLevel, pPos, pRandom);
   }

   public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      super.randomTick(pState, pLevel, pPos, pRandom);
   }

   private static float randomDirectionModifier(RandomSource pRandom, int pReduce) {
      return pRandom.nextFloat() / pReduce * pRandom.nextIntBetweenInclusive(-1, 1);
   }

   public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltip, TooltipFlag pTooltipFlag) {
      super.appendHoverText(pStack, pContext, pTooltip, pTooltipFlag);
      pTooltip.add(Component.translatable("block.betterarcheology.radiance_totem_tooltip").withStyle(ChatFormatting.DARK_GREEN));
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());

      for (Direction direction : pContext.getNearestLookingDirections()) {
         if (direction.getAxis() == Axis.Y) {
            BlockState blockstate = (BlockState)this.defaultBlockState().setValue(HANGING, direction == Direction.UP);
            if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
               return (BlockState)blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
            }
         }
      }

      return null;
   }

   @NotNull
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return pState.getValue(HANGING) ? HANGING_AABB : AABB;
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{HANGING, WATERLOGGED, SELECTOR});
   }

   public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
      Direction direction = getConnectedDirection(pState).getOpposite();
      return Block.canSupportCenter(pLevel, pPos.relative(direction), direction.getOpposite());
   }

   protected static Direction getConnectedDirection(BlockState pState) {
      return pState.getValue(HANGING) ? Direction.DOWN : Direction.UP;
   }

   @NotNull
   public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
      if ((Boolean)pState.getValue(WATERLOGGED)) {
         pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
      }

      return getConnectedDirection(pState).getOpposite() == pDirection && !pState.canSurvive(pLevel, pPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
   }

   @NotNull
   public FluidState getFluidState(BlockState pState) {
      return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }

   protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
      return false;
   }
}
