package io.wispforest.owo.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ImplementedInventory extends Container {
   NonNullList<ItemStack> getItems();

   static ImplementedInventory of(NonNullList<ItemStack> items) {
      return () -> items;
   }

   static ImplementedInventory ofSize(int size) {
      return of(NonNullList.withSize(size, ItemStack.EMPTY));
   }

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

      return result;
   }

   default ItemStack removeItemNoUpdate(int slot) {
      return ContainerHelper.takeItem(this.getItems(), slot);
   }

   default void setItem(int slot, ItemStack stack) {
      this.getItems().set(slot, stack);
      if (stack.getCount() > this.getMaxStackSize()) {
         stack.setCount(this.getMaxStackSize());
      }
   }

   default void clearContent() {
      this.getItems().clear();
   }

   default void setChanged() {
   }

   default boolean stillValid(Player player) {
      return true;
   }
}
