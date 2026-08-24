package net.joefoxe.hexerei.block.custom;

import java.util.List;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;

public class Altar extends ConnectingTableEntityBase implements ITileEntity<BookOfShadowsAltarTile> {
   public Altar(Properties pProperties) {
      super(pProperties);
   }

   @Override
   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      return super.useWithoutItem(state, level, pos, player, hitResult);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (!(level.getBlockEntity(pos) instanceof BookOfShadowsAltarTile bookOfShadowsAltarTile && !(Block.byItem(stack.getItem()) instanceof ConnectingTable))) {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      } else if (stack.is((Item)ModItems.CROW_FLUTE.get()) && ((FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 2) {
         stack.useOn(new UseOnContext(player, hand, hitResult));
         return ItemInteractionResult.SUCCESS;
      } else {
         return bookOfShadowsAltarTile.interact(player, hand, stack) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         if (level.getBlockEntity(pos) instanceof BookOfShadowsAltarTile te && !te.itemHandler.getStackInSlot(0).isEmpty()) {
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F, te.itemHandler.getStackInSlot(0)));
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{WEST, EAST, NORTH, SOUTH, WATERLOGGED});
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
         tooltipComponents.add(Component.translatable("tooltip.hexerei.altar_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
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

   @Override
   public Class<BookOfShadowsAltarTile> getTileEntityClass() {
      return BookOfShadowsAltarTile.class;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
      return entityType == ModTileEntities.BOOK_OF_SHADOWS_ALTAR_TILE.get() ? (world2, pos, state2, entity) -> ((BookOfShadowsAltarTile)entity).tick() : null;
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new BookOfShadowsAltarTile((BlockEntityType<?>)ModTileEntities.BOOK_OF_SHADOWS_ALTAR_TILE.get(), pos, state);
   }
}
