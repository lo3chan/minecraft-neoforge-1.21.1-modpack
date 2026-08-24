package net.blay09.mods.balm.api.container;

import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ImplementedContainer extends Container {
   static ImplementedContainer of(NonNullList<ItemStack> items) {
      return () -> items;
   }

   static ImplementedContainer ofSize(int size) {
      return of(NonNullList.withSize(size, ItemStack.EMPTY));
   }

   static NonNullList<ItemStack> deserializeInventory(CompoundTag tag, int minimumSize, Provider provider) {
      int size = Math.max(minimumSize, tag.contains("Size", 3) ? tag.getInt("Size") : minimumSize);
      NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
      ListTag itemTags = tag.getList("Items", 10);

      for (int i = 0; i < itemTags.size(); i++) {
         CompoundTag itemTag = itemTags.getCompound(i);
         int slot = itemTag.getInt("Slot");
         if (slot >= 0 && slot < items.size()) {
            items.set(slot, ItemStack.parse(provider, itemTag).orElse(ItemStack.EMPTY));
         }
      }

      return items;
   }

   NonNullList<ItemStack> getItems();

   default int getContainerSize() {
      return this.getItems().size();
   }

   default boolean isEmpty() {
      for (int i = 0; i < this.getContainerSize(); i++) {
         ItemStack stack = this.getItem(i);
         if (!stack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   default ItemStack getItem(int slot) {
      return (ItemStack)this.getItems().get(slot);
   }

   default ItemStack removeItem(int slot, int count) {
      ItemStack result = ContainerHelper.removeItem(this.getItems(), slot, count);
      if (!result.isEmpty()) {
         this.setChanged();
      }

      this.slotChanged(slot);
      return result;
   }

   default ItemStack removeItemNoUpdate(int slot) {
      ItemStack itemStack = ContainerHelper.takeItem(this.getItems(), slot);
      this.slotChanged(slot);
      return itemStack;
   }

   default void setItem(int slot, ItemStack stack) {
      this.getItems().set(slot, stack);
      if (stack.getCount() > this.getMaxStackSize()) {
         stack.setCount(this.getMaxStackSize());
      }

      this.setChanged();
      this.slotChanged(slot);
   }

   default void clearContent() {
      this.getItems().clear();

      for (int i = 0; i < this.getItems().size(); i++) {
         this.slotChanged(i);
      }
   }

   default void setChanged() {
   }

   default void slotChanged(int slot) {
   }

   default boolean stillValid(Player player) {
      return true;
   }

   default CompoundTag serializeInventory(Provider provider) {
      NonNullList<ItemStack> items = this.getItems();
      ListTag itemTags = new ListTag();

      for (int i = 0; i < items.size(); i++) {
         if (!((ItemStack)items.get(i)).isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.putInt("Slot", i);
            itemTags.add(((ItemStack)items.get(i)).save(provider, itemTag));
         }
      }

      CompoundTag nbt = new CompoundTag();
      nbt.put("Items", itemTags);
      nbt.putInt("Size", items.size());
      return nbt;
   }
}
