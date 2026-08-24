package net.joefoxe.hexerei.data.owl;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class OwlCourierDepotData {
   public String name;
   public NonNullList<ItemStack> items;

   OwlCourierDepotData(String name) {
      this.name = name;
      this.items = NonNullList.withSize(8, new ItemStack(Items.AIR));
   }

   public boolean isFull() {
      for (ItemStack stack : this.items) {
         if (stack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack takeFirstSlotAndSlide() {
      ItemStack stack = ItemStack.EMPTY;
      if (!((ItemStack)this.items.get(0)).isEmpty()) {
         stack = ((ItemStack)this.items.get(0)).copy();

         for (int i = 1; i < this.items.size(); i++) {
            this.items.set(i - 1, (ItemStack)this.items.get(i));
         }

         this.items.set(this.items.size() - 1, ItemStack.EMPTY);
      }

      return stack;
   }
}
