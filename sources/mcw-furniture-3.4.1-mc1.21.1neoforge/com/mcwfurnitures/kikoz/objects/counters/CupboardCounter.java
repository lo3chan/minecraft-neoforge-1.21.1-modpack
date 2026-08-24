package com.mcwfurnitures.kikoz.objects.counters;

import com.mcwfurnitures.kikoz.storage.StorageTileEntity;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CupboardCounter extends Counter implements EntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;

   public CupboardCounter(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(HINGE, DoorHingeSide.LEFT)
      );
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new StorageTileEntity(pos, state);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(HINGE, this.getHinge(context))).setValue(FACING, context.getHorizontalDirection());
   }

   public ItemInteractionResult useItemOn(
      ItemStack itemstack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      Item item = itemstack.getItem();
      if (item != this.asItem()) {
         if (!level.isClientSide() && level.getBlockEntity(pos) instanceof StorageTileEntity blockEntity) {
            player.openMenu(blockEntity);
         }

         return ItemInteractionResult.SUCCESS;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!state.is(statetwo.getBlock())) {
         BlockEntity blockentity = level.getBlockEntity(pos);
         if (blockentity instanceof Container) {
            Containers.dropContents(level, pos, (Container)blockentity);
            level.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, level, pos, statetwo, bool);
      }
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, Random rand) {
      BlockEntity blockentity = level.getBlockEntity(pos);
      if (blockentity instanceof StorageTileEntity) {
         ((StorageTileEntity)blockentity).recheckOpen();
      }
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livent, ItemStack stack) {
      if (stack.getHoverName() != null) {
         BlockEntity blockentity = level.getBlockEntity(pos);
         if (blockentity instanceof StorageTileEntity) {
            ((StorageTileEntity)blockentity).getName();
         }
      }
   }

   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
   }

   private DoorHingeSide getHinge(BlockPlaceContext context) {
      BlockPos blockpos = context.getClickedPos();
      Direction direction = context.getHorizontalDirection();
      int j = direction.getStepX();
      int k = direction.getStepZ();
      Vec3 vector3d = context.getClickLocation();
      double d0 = vector3d.x - blockpos.getX();
      double d1 = vector3d.z - blockpos.getZ();
      return j < 0 && d1 < 0.5 || j > 0 && d1 > 0.5 || k < 0 && d0 > 0.5 || k > 0 && d0 < 0.5 ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, HINGE});
   }
}
