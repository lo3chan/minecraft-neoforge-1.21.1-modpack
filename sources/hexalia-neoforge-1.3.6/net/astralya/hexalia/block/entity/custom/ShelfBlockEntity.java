package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.custom.ShelfBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ShelfBlockEntity extends BlockEntity implements WorldlyContainer, Clearable {
   private static final int SIZE = 6;
   private static final int[] TOP_SLOTS = new int[]{0, 1, 2, 3, 4, 5};
   private static final int[] NO_SLOTS = new int[0];
   private final NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);

   public ShelfBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.SHELF.get(), pos, state);
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public int[] getSlotsForFace(Direction side) {
      return side == Direction.UP ? TOP_SLOTS : NO_SLOTS;
   }

   public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
      return direction == Direction.UP && this.canPlaceItem(slot, stack);
   }

   public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
      return false;
   }

   public int getContainerSize() {
      return 6;
   }

   public boolean isEmpty() {
      return this.items.stream().allMatch(ItemStack::isEmpty);
   }

   public ItemStack getItem(int slot) {
      return slot >= 0 && slot < 6 ? (ItemStack)this.items.get(slot) : ItemStack.EMPTY;
   }

   public ItemStack removeItem(int slot, int amount) {
      ItemStack removed = ContainerHelper.removeItem(this.items, slot, amount);
      if (!removed.isEmpty()) {
         this.setChangedAndSync();
      }

      return removed;
   }

   public ItemStack removeItemNoUpdate(int slot) {
      if (slot >= 0 && slot < 6) {
         ItemStack removed = (ItemStack)this.items.get(slot);
         if (removed.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            this.items.set(slot, ItemStack.EMPTY);
            this.setChangedAndSync();
            return removed;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setItem(int slot, ItemStack stack) {
      if (slot >= 0 && slot < 6) {
         ItemStack one = stack.copy();
         if (!one.isEmpty()) {
            one.setCount(1);
         }

         this.items.set(slot, one);
         this.setChangedAndSync();
      }
   }

   public boolean canPlaceItem(int slot, ItemStack stack) {
      return slot >= 0 && slot < 6 && ((ItemStack)this.items.get(slot)).isEmpty() && ShelfBlock.isValidItem(stack);
   }

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this
         ? player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0
         : false;
   }

   public void clearContent() {
      this.items.clear();
      this.setChangedAndSync();
   }

   private void setChangedAndSync() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide()) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      ContainerHelper.loadAllItems(tag, this.items, registries);

      for (ItemStack stack : this.items) {
         if (!stack.isEmpty()) {
            stack.setCount(1);
         }
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      ContainerHelper.saveAllItems(tag, this.items, registries);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
