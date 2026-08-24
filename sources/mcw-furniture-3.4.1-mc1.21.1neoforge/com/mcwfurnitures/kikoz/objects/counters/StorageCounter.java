package com.mcwfurnitures.kikoz.objects.counters;

import com.mcwfurnitures.kikoz.storage.StorageTileEntity;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;

public class StorageCounter extends Counter implements EntityBlock {
   public StorageCounter(BlockState state, Properties prop) {
      super(prop);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new StorageTileEntity(pos, state);
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
}
