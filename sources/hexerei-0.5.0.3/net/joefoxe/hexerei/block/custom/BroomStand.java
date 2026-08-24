package net.joefoxe.hexerei.block.custom;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.tileentity.BroomStandTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BroomStand extends Block implements ITileEntity<BroomStandTile>, EntityBlock, SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   VoxelShape shape_turned = Stream.of(
         Block.box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
         Block.box(6.5, 3.0, 6.5, 9.5, 10.0, 9.5),
         Block.box(9.5, 10.0, 11.0, 12.5, 13.0, 14.0),
         Block.box(3.5, 10.0, 11.0, 6.5, 13.0, 14.0),
         Block.box(6.5, 10.0, 2.0, 9.5, 13.0, 14.0),
         Block.box(3.5, 10.0, 2.0, 6.5, 13.0, 5.0),
         Block.box(9.5, 10.0, 2.0, 12.5, 13.0, 5.0),
         Block.box(9.5, 13.0, 2.0, 12.5, 15.0, 5.0),
         Block.box(9.5, 13.0, 11.0, 12.5, 15.0, 14.0),
         Block.box(3.5, 13.0, 11.0, 6.5, 15.0, 14.0),
         Block.box(3.5, 13.0, 2.0, 6.5, 15.0, 5.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   VoxelShape shape = Stream.of(
         Block.box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
         Block.box(6.5, 3.0, 6.5, 9.5, 10.0, 9.5),
         Block.box(11.0, 10.0, 3.5, 14.0, 13.0, 6.5),
         Block.box(11.0, 10.0, 9.5, 14.0, 13.0, 12.5),
         Block.box(2.0, 10.0, 6.5, 14.0, 13.0, 9.5),
         Block.box(2.0, 10.0, 9.5, 5.0, 13.0, 12.5),
         Block.box(2.0, 10.0, 3.5, 5.0, 13.0, 6.5),
         Block.box(2.0, 13.0, 3.5, 5.0, 15.0, 6.5),
         Block.box(11.0, 13.0, 3.5, 14.0, 15.0, 6.5),
         Block.box(11.0, 13.0, 9.5, 14.0, 15.0, 12.5),
         Block.box(2.0, 13.0, 9.5, 5.0, 15.0, 12.5)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();

   public BroomStand(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState(
         (BlockState)((BlockState)super.defaultBlockState().setValue(WATERLOGGED, false)).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
      );
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      if (!pState.hasProperty(HorizontalDirectionalBlock.FACING)) {
         return this.shape;
      } else {
         Direction dir = (Direction)pState.getValue(HorizontalDirectionalBlock.FACING);
         return dir != Direction.NORTH && dir != Direction.SOUTH ? this.shape_turned : this.shape;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()))
         .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (level.getBlockEntity(pos) instanceof BroomStandTile broomStand) {
         return broomStand.interact(player, InteractionHand.MAIN_HAND, false) == 1 ? InteractionResult.SUCCESS : InteractionResult.PASS;
      } else {
         return super.useWithoutItem(state, level, pos, player, hitResult);
      }
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (level.getBlockEntity(pos) instanceof BroomStandTile broomStand) {
         if (stack.is((Item)ModItems.CROW_FLUTE.get()) && ((FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 2) {
            stack.useOn(new UseOnContext(player, hand, hitResult));
            return ItemInteractionResult.SUCCESS;
         } else {
            return broomStand.interact(player, hand, true) == 1
               ? ItemInteractionResult.SUCCESS
               : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
         }
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         if (level.getBlockEntity(pos) instanceof BroomStandTile te && !te.itemHandler.getStackInSlot(0).isEmpty()) {
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F, te.itemHandler.getStackInSlot(0)));
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{WATERLOGGED, HorizontalDirectionalBlock.FACING});
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
   public Class<BroomStandTile> getTileEntityClass() {
      return BroomStandTile.class;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
      return entityType == ModTileEntities.BROOM_STAND_TILE.get() ? (world2, pos, state2, entity) -> ((BroomStandTile)entity).tick() : null;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new BroomStandTile((BlockEntityType<?>)ModTileEntities.BROOM_STAND_TILE.get(), pos, state);
   }
}
