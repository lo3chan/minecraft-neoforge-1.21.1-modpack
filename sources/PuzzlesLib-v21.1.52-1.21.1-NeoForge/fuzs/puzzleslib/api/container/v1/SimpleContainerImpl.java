package fuzs.puzzleslib.api.container.v1;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

public interface SimpleContainerImpl extends Container {
   default boolean isEmpty() {
      for (int i = 0; i < this.getContainerSize(); i++) {
         if (!this.getItem(i).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   default ItemStack getItem(int slot) {
      return slot >= 0 && slot < this.getContainerSize() ? this.getContainerItem(slot) : ItemStack.EMPTY;
   }

   @OverrideOnly
   ItemStack getContainerItem(int var1);

   default ItemStack removeItem(int slot, int amount) {
      if (slot >= 0 && slot < this.getContainerSize() && !this.getContainerItem(slot).isEmpty() && amount > 0) {
         ItemStack itemStack = this.removeContainerItem(slot, amount);
         if (!itemStack.isEmpty()) {
            this.setChanged();
         }

         return itemStack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   @OverrideOnly
   ItemStack removeContainerItem(int var1, int var2);

   default ItemStack removeItemNoUpdate(int slot) {
      return slot >= 0 && slot < this.getContainerSize() ? this.removeContainerItemNoUpdate(slot) : ItemStack.EMPTY;
   }

   @OverrideOnly
   ItemStack removeContainerItemNoUpdate(int var1);

   default void setItem(int slot, ItemStack itemStack) {
      if (slot >= 0 && slot < this.getContainerSize()) {
         this.setContainerItem(slot, itemStack);
         if (!itemStack.isEmpty() && itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
         }

         this.setChanged();
      }
   }

   @OverrideOnly
   void setContainerItem(int var1, ItemStack var2);

   default void clearContent() {
      for (int i = 0; i < this.getContainerSize(); i++) {
         this.setContainerItem(i, ItemStack.EMPTY);
      }

      this.setChanged();
   }
}
