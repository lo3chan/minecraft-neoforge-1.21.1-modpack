package fuzs.puzzleslib.api.container.v1;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ListBackedContainer extends Container {
   static ListBackedContainer of(NonNullList<ItemStack> items) {
      return () -> items;
   }

   static ListBackedContainer of(int size) {
      return of(NonNullList.withSize(size, ItemStack.EMPTY));
   }

   NonNullList<ItemStack> getContainerItems();

   default int getContainerSize() {
      return this.getContainerItems().size();
   }

   default boolean isEmpty() {
      for (ItemStack stack : this.getContainerItems()) {
         if (!stack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   default ItemStack getItem(int slot) {
      return slot >= 0 && slot < this.getContainerSize() ? (ItemStack)this.getContainerItems().get(slot) : ItemStack.EMPTY;
   }

   default ItemStack removeItem(int slot, int count) {
      ItemStack result = ContainerHelper.removeItem(this.getContainerItems(), slot, count);
      if (!result.isEmpty()) {
         this.setChanged();
      }

      return result;
   }

   default ItemStack removeItemNoUpdate(int slot) {
      return ContainerHelper.takeItem(this.getContainerItems(), slot);
   }

   default void setItem(int slot, ItemStack stack) {
      if (slot >= 0 && slot < this.getContainerSize()) {
         this.getContainerItems().set(slot, stack);
         if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
         }

         this.setChanged();
      }
   }

   default void clearContent() {
      this.getContainerItems().clear();
      this.setChanged();
   }

   default void setChanged() {
   }

   default boolean stillValid(Player player) {
      return true;
   }
}
