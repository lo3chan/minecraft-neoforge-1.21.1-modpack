package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class InfusionPedestalBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final MapCodec<InfusionPedestalBlock> CODEC = simpleCodec(InfusionPedestalBlock::new);
   private static final VoxelShape SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.875, 1.0);

   public InfusionPedestalBlock(Properties properties) {
      super(properties);
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState()
         .setValue(
            FACING,
            context.getPlayer() != null && context.getPlayer().isShiftKeyDown()
               ? context.getHorizontalDirection().getOpposite()
               : context.getHorizontalDirection()
         );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new ItemStackHolderBlockEntity(pos, state);
   }

   protected boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
      return level.getBlockEntity(pos) instanceof ItemStackHolderBlockEntity be ? AbstractContainerMenu.getRedstoneSignalFromContainer(be) : 0;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean pIsMoving) {
      Containers.dropContentsOnDestroy(state, newState, level, pos);
      super.onRemove(state, level, pos, newState, pIsMoving);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ItemStackHolderBlockEntity pedestal) {
         if (!pedestal.getItem().isEmpty()) {
            ItemEntity itemEntity = ItemStackHolderBlockEntity.dropItem(level, pos, pedestal.getItem());
            if (itemEntity != null) {
               itemEntity.getPersistentData().putBoolean("alchemancy:from_pedestal_click", true);
            }

            pedestal.clearContent();
         } else if (!stack.isEmpty()) {
            pedestal.setItem(stack);
            player.setItemInHand(hand, ItemStack.EMPTY);
         }
      }

      return ItemInteractionResult.SUCCESS;
   }

   protected BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }
}
