package net.joefoxe.hexerei.block.custom;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.tileentity.DryingRackTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HerbDryingRack extends Block implements ITileEntity<DryingRackTile>, EntityBlock, SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final VoxelShape SHAPE = Optional.of(Block.box(0.5, 5.5, 7.5, 15.5, 16.0, 8.5)).get();
   public static final VoxelShape SHAPE_TURNED = Optional.of(Block.box(7.5, 5.5, 0.5, 8.5, 16.0, 15.5)).get();

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()))
         .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate((Direction)pState.getValue(HorizontalDirectionalBlock.FACING)));
   }

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

   public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
      return p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) != Direction.NORTH
            && p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) != Direction.SOUTH
         ? SHAPE_TURNED
         : SHAPE;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_drying_rack_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

   public HerbDryingRack(Properties properties) {
      super(properties.noCollission());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING, WATERLOGGED});
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         if (level.getBlockEntity(pos) instanceof DryingRackTile te && te.getLevel() != null) {
            if (!((ItemStack)te.getItems().get(0)).isEmpty()) {
               te.getLevel()
                  .addFreshEntity(new ItemEntity(te.getLevel(), pos.getX() + 0.5F, pos.getY() - 0.5F, pos.getZ() + 0.5F, (ItemStack)te.getItems().get(0)));
            }

            if (!((ItemStack)te.getItems().get(1)).isEmpty()) {
               te.getLevel()
                  .addFreshEntity(new ItemEntity(te.getLevel(), pos.getX() + 0.5F, pos.getY() - 0.5F, pos.getZ() + 0.5F, (ItemStack)te.getItems().get(1)));
            }

            if (!((ItemStack)te.getItems().get(2)).isEmpty()) {
               te.getLevel()
                  .addFreshEntity(new ItemEntity(te.getLevel(), pos.getX() + 0.5F, pos.getY() - 0.5F, pos.getZ() + 0.5F, (ItemStack)te.getItems().get(2)));
            }
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
      }

      return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
      return canSupportCenter(worldIn, pos.above(), Direction.DOWN);
   }

   @Override
   public Class<DryingRackTile> getTileEntityClass() {
      return DryingRackTile.class;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new DryingRackTile((BlockEntityType<?>)ModTileEntities.DRYING_RACK_TILE.get(), pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
      return entityType == ModTileEntities.DRYING_RACK_TILE.get() ? (world2, pos, state2, entity) -> ((DryingRackTile)entity).tick() : null;
   }
}
