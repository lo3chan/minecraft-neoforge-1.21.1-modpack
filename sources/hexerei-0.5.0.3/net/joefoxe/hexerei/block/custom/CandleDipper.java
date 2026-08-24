package net.joefoxe.hexerei.block.custom;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.tileentity.CandleDipperTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CandleDipper extends BaseEntityBlock implements ITileEntity<CandleDipperTile>, EntityBlock, SimpleWaterloggedBlock {
   public static final MapCodec<CandleDipper> CODEC = simpleCodec(CandleDipper::new);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final VoxelShape SHAPE = Stream.of(
         Block.box(14.0, 1.0, 4.0, 16.0, 6.0, 12.0),
         Block.box(13.0, -1.0, 3.5, 17.0, 1.0, 6.5),
         Block.box(13.0, -1.0, 9.5, 17.0, 1.0, 12.5),
         Block.box(-1.0, -1.0, 9.5, 3.0, 1.0, 12.5),
         Block.box(0.0, 1.0, 4.0, 2.0, 6.0, 12.0),
         Block.box(-1.0, -1.0, 3.5, 3.0, 1.0, 6.5),
         Block.box(2.0, -1.0, 2.0, 14.0, 0.0, 14.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   public static final VoxelShape SHAPE_TURNED = Stream.of(
         Block.box(4.0, 1.0, 0.0, 12.0, 6.0, 2.0),
         Block.box(3.5, -1.0, -1.0, 6.5, 1.0, 3.0),
         Block.box(9.5, -1.0, -1.0, 12.5, 1.0, 3.0),
         Block.box(9.5, -1.0, 13.0, 12.5, 1.0, 17.0),
         Block.box(4.0, 1.0, 14.0, 12.0, 6.0, 16.0),
         Block.box(3.5, -1.0, 13.0, 6.5, 1.0, 17.0),
         Block.box(2.0, -1.0, 2.0, 14.0, 0.0, 14.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
      return this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
            && context.getLevel().getBlockState(context.getClickedPos().below()).getBlock() instanceof MixingCauldron
         ? (BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER))
            .setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection())
         : null;
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate((Direction)pState.getValue(HorizontalDirectionalBlock.FACING)));
   }

   public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState p_49862_) {
      super.destroy(worldIn, pos, p_49862_);
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return state.getValue(HorizontalDirectionalBlock.FACING) != Direction.NORTH && state.getValue(HorizontalDirectionalBlock.FACING) != Direction.SOUTH
         ? SHAPE_TURNED
         : SHAPE;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      return level.getBlockEntity(pos) instanceof CandleDipperTile candleDipperTile
         ? candleDipperTile.interactWithItem(player)
         : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      return level.getBlockEntity(pos) instanceof CandleDipperTile candleDipperTile
         ? candleDipperTile.interactWithoutItem(player)
         : super.useWithoutItem(state, level, pos, player, hitResult);
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.DESTROY;
   }

   public CandleDipper(Properties properties) {
      super(properties.noOcclusion());
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, Boolean.FALSE));
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING, WATERLOGGED});
   }

   public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
      if (!(Boolean)state.getValue(BlockStateProperties.WATERLOGGED) && fluidStateIn.getType() == Fluids.WATER) {
         worldIn.setBlock(pos, (BlockState)state.setValue(WATERLOGGED, Boolean.TRUE), 3);
         worldIn.scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(worldIn));
         return true;
      } else {
         return false;
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         BlockEntity tileentity = level.getBlockEntity(pos);
         if (tileentity != null) {
            CandleDipperTile te = (CandleDipperTile)level.getBlockEntity(pos);
            if (!((ItemStack)te.getItems().get(0)).isEmpty()) {
               level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, (ItemStack)te.getItems().get(0)));
            }

            if (!((ItemStack)te.getItems().get(1)).isEmpty()) {
               level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, (ItemStack)te.getItems().get(1)));
            }

            if (!((ItemStack)te.getItems().get(2)).isEmpty()) {
               level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, (ItemStack)te.getItems().get(2)));
            }
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      return !stateIn.canSurvive(worldIn, currentPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.below()).getBlock() instanceof MixingCauldron;
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return !(Boolean)state.getValue(WATERLOGGED);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.candle_dipper_shift_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(Component.translatable("tooltip.hexerei.candle_dipper_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }

   public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
   }

   @Override
   public Class<CandleDipperTile> getTileEntityClass() {
      return CandleDipperTile.class;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new CandleDipperTile((BlockEntityType<?>)ModTileEntities.CANDLE_DIPPER_TILE.get(), pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
      return entityType == ModTileEntities.CANDLE_DIPPER_TILE.get() ? (world2, pos, state2, entity) -> ((CandleDipperTile)entity).tick() : null;
   }
}
