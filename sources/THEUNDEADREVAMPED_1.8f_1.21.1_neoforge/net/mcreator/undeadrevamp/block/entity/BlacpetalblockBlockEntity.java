package net.mcreator.undeadrevamp.block.entity;

import io.netty.buffer.Unpooled;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlockEntities;
import net.mcreator.undeadrevamp.world.inventory.BlackpetalblockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public class BlacpetalblockBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
   private NonNullList<ItemStack> stacks = NonNullList.withSize(13, ItemStack.EMPTY);
   private final SidedInvWrapper handler = new SidedInvWrapper(this, null);

   public BlacpetalblockBlockEntity(BlockPos position, BlockState state) {
      super((BlockEntityType)UndeadRevamp2ModBlockEntities.BLACPETALBLOCK.get(), position, state);
   }

   public void loadAdditional(CompoundTag compound, Provider lookupProvider) {
      super.loadAdditional(compound, lookupProvider);
      if (!this.tryLoadLootTable(compound)) {
         this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      }

      ContainerHelper.loadAllItems(compound, this.stacks, lookupProvider);
   }

   public void saveAdditional(CompoundTag compound, Provider lookupProvider) {
      super.saveAdditional(compound, lookupProvider);
      if (!this.trySaveLootTable(compound)) {
         ContainerHelper.saveAllItems(compound, this.stacks, lookupProvider);
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider lookupProvider) {
      return this.saveWithFullMetadata(lookupProvider);
   }

   public int getContainerSize() {
      return this.stacks.size();
   }

   public boolean isEmpty() {
      for (ItemStack itemstack : this.stacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public Component getDefaultName() {
      return Component.literal("blacpetalblock");
   }

   public int getMaxStackSize() {
      return 64;
   }

   public AbstractContainerMenu createMenu(int id, Inventory inventory) {
      return new BlackpetalblockMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
   }

   public Component getDisplayName() {
      return Component.literal("Black Petal Block");
   }

   protected NonNullList<ItemStack> getItems() {
      return this.stacks;
   }

   protected void setItems(NonNullList<ItemStack> stacks) {
      this.stacks = stacks;
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      return true;
   }

   public int[] getSlotsForFace(Direction side) {
      return IntStream.range(0, this.getContainerSize()).toArray();
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
      return this.canPlaceItem(index, stack);
   }

   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      if (index == 0) {
         return false;
      } else if (index == 1) {
         return false;
      } else if (index == 2) {
         return false;
      } else if (index == 3) {
         return false;
      } else if (index == 4) {
         return false;
      } else if (index == 5) {
         return false;
      } else if (index == 6) {
         return false;
      } else if (index == 7) {
         return false;
      } else if (index == 8) {
         return false;
      } else if (index == 9) {
         return false;
      } else if (index == 10) {
         return false;
      } else {
         return index == 11 ? false : index != 12;
      }
   }

   public SidedInvWrapper getItemHandler() {
      return this.handler;
   }
}
