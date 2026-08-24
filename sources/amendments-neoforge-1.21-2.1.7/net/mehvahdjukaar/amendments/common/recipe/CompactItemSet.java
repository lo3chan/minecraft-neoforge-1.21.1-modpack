package net.mehvahdjukaar.amendments.common.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CompactItemSet {
   private final List<ItemStack> items = new ArrayList<>();

   public void add(@NotNull ItemStack item) {
      item = item.copy();

      for (ItemStack existing : this.items) {
         if (ItemStack.isSameItemSameComponents(existing, item)) {
            int maxSize = existing.getMaxStackSize();
            int newCount = existing.getCount() + item.getCount();
            if (newCount <= maxSize) {
               existing.setCount(newCount);
               return;
            }

            existing.setCount(maxSize);
            item.setCount(newCount - maxSize);
         }
      }

      if (!item.isEmpty()) {
         this.items.add(item);
      }
   }

   public List<ItemStack> toList() {
      return this.items.stream().<ItemStack>map(ItemStack::copy).toList();
   }
}
