package net.blay09.mods.inventoryessentials.client.sorting;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

public final class CreativeSorting {
   private static volatile Map<CreativeSorting.ItemStackKey, Integer> creativeRanks = Map.of();

   private CreativeSorting() {
   }

   public static CreativeSorting.ItemStackKey keyOf(ItemStack stack) {
      return new CreativeSorting.ItemStackKey(stack);
   }

   public static Map<CreativeSorting.ItemStackKey, Integer> getCreativeRanks() {
      Map<CreativeSorting.ItemStackKey, Integer> cachedRanks = creativeRanks;
      if (!cachedRanks.isEmpty()) {
         return cachedRanks;
      } else {
         synchronized (CreativeSorting.class) {
            if (!creativeRanks.isEmpty()) {
               return creativeRanks;
            } else {
               HashMap<CreativeSorting.ItemStackKey, Integer> computedRanks = new HashMap<>();
               int rank = 0;

               for (CreativeModeTab tab : CreativeModeTabs.tabs()) {
                  for (ItemStack stack : tab.getDisplayItems()) {
                     CreativeSorting.ItemStackKey key = keyOf(stack);
                     if (!computedRanks.containsKey(key)) {
                        computedRanks.put(key, rank++);
                     }
                  }
               }

               creativeRanks = Map.copyOf(computedRanks);
               return creativeRanks;
            }
         }
      }
   }

   public static Comparator<ItemStack> getCreativeComparator() {
      Map<CreativeSorting.ItemStackKey, Integer> creativeRanks = getCreativeRanks();
      return Comparator.comparingInt(stack -> creativeRanks.getOrDefault(keyOf(stack), 2147483647));
   }

   public record ItemStackKey(ItemStack stack) {
      public ItemStackKey(ItemStack stack) {
         this.stack = stack.copyWithCount(1);
         if (this.stack.isDamageableItem()) {
            this.stack.setDamageValue(0);
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof CreativeSorting.ItemStackKey(ItemStack var6)) {
            ItemStack var4 = var6;
            if (ItemStack.isSameItemSameComponents(this.stack, var4)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public int hashCode() {
         return ItemStack.hashItemAndComponents(this.stack);
      }
   }
}
