package net.joefoxe.hexerei.block.custom;

import java.util.List;
import java.util.stream.Stream;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.tileentity.PestleAndMortarTile;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PestleAndMortar extends Block implements ITileEntity<PestleAndMortarTile>, EntityBlock, SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final VoxelShape SHAPE = Stream.of(
         Block.box(4.0, 1.0, 4.0, 5.0, 7.0, 12.0),
         Block.box(5.0, 1.0, 4.0, 11.0, 7.0, 5.0),
         Block.box(5.0, 1.0, 11.0, 11.0, 7.0, 12.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 2.0, 11.0),
         Block.box(11.0, 1.0, 4.0, 12.0, 7.0, 12.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();

   public RenderShape getRenderShape(BlockState p_60550_) {
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

   public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
      return SHAPE;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (stack.is((Item)ModItems.CROW_FLUTE.get()) && ((FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 2) {
         stack.useOn(new UseOnContext(player, hand, hitResult));
         return ItemInteractionResult.SUCCESS;
      } else {
         BlockEntity tileEntity = level.getBlockEntity(pos);
         if (tileEntity instanceof PestleAndMortarTile) {
            ((PestleAndMortarTile)tileEntity).interactPestleAndMortar(player, hitResult);
            return ItemInteractionResult.SUCCESS;
         } else {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
         }
      }
   }

   public PestleAndMortar(Properties properties) {
      super(properties.noOcclusion());
      this.withPropertiesOf((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, Boolean.FALSE));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING, WATERLOGGED});
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.pestle_and_mortar_shift_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.pestle_and_mortar_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.pestle_and_mortar_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.pestle_and_mortar_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.pestle_and_mortar_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         BlockEntity tileentity = level.getBlockEntity(pos);
         if (tileentity != null) {
            PestleAndMortarTile te = (PestleAndMortarTile)level.getBlockEntity(pos);
            if (!((ItemStack)te.getItems().get(0)).isEmpty()) {
               level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.25F, pos.getZ() + 0.5F, (ItemStack)te.getItems().get(0)));
            }

            if (!((ItemStack)te.getItems().get(1)).isEmpty()) {
               level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.25F, pos.getZ() + 0.5F, (ItemStack)te.getItems().get(1)));
            }

            if (!((ItemStack)te.getItems().get(2)).isEmpty()) {
               level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.25F, pos.getZ() + 0.5F, (ItemStack)te.getItems().get(2)));
            }

            if (!((ItemStack)te.getItems().get(3)).isEmpty()) {
               level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.25F, pos.getZ() + 0.5F, (ItemStack)te.getItems().get(3)));
            }

            if (!((ItemStack)te.getItems().get(4)).isEmpty()) {
               level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.25F, pos.getZ() + 0.5F, (ItemStack)te.getItems().get(4)));
            }

            if (!((ItemStack)te.getItems().get(5)).isEmpty()) {
               level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.25F, pos.getZ() + 0.5F, (ItemStack)te.getItems().get(5)));
            }
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   @Override
   public Class<PestleAndMortarTile> getTileEntityClass() {
      return PestleAndMortarTile.class;
   }

   @javax.annotation.Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new PestleAndMortarTile((BlockEntityType<?>)ModTileEntities.PESTLE_AND_MORTAR_TILE.get(), pos, state);
   }

   @javax.annotation.Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
      return entityType == ModTileEntities.PESTLE_AND_MORTAR_TILE.get() ? (world2, pos, state2, entity) -> ((PestleAndMortarTile)entity).tick() : null;
   }
}
