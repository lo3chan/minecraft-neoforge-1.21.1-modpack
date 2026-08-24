package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public record CreativeTabCallbackForge(BuildCreativeModeTabContentsEvent event) implements CreativeTabCallback {
   @Override
   public void addAfter(ItemStack order, ItemStack[] items, TabVisibility visibility) {
      for (ItemStack item : items) {
         if (order.isEmpty()) {
            this.event.accept(item, visibility);
         } else {
            this.event.insertAfter(order, item, visibility);
         }
      }
   }

   @Override
   public void addBefore(ItemStack order, ItemStack[] items, TabVisibility visibility) {
      for (ItemStack item : items) {
         if (order.isEmpty()) {
            this.event.insertFirst(item, visibility);
         } else {
            this.event.insertBefore(order, item, visibility);
         }
      }
   }

   @Override
   public void remove(ItemPredicate filter, boolean removeParent, boolean removeSearch) {
      if (removeParent) {
         for (ItemStack is : List.copyOf(this.event.getParentEntries())) {
            if (filter.test(is)) {
               this.event.remove(is, TabVisibility.PARENT_TAB_ONLY);
            }
         }
      }

      if (removeSearch) {
         for (ItemStack isx : List.copyOf(this.event.getSearchEntries())) {
            if (filter.test(isx)) {
               this.event.remove(isx, TabVisibility.SEARCH_TAB_ONLY);
            }
         }
      }
   }
}
