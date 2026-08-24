package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.core.CreativeModeTabKJS;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;

public class CreativeTabKubeEvent implements KubeEvent {
   public final CreativeModeTab tab;
   public final boolean showRestrictedItems;
   private final CreativeTabCallback callback;

   public CreativeTabKubeEvent(CreativeModeTab tab, boolean showRestrictedItems, CreativeTabCallback callback) {
      this.tab = tab;
      this.showRestrictedItems = showRestrictedItems;
      this.callback = callback;
   }

   public void setDisplayName(Component component) {
      ((CreativeModeTabKJS)this.tab).kjs$setDisplayName(component);
   }

   public void setIcon(ItemStack icon) {
      ((CreativeModeTabKJS)this.tab).kjs$setIcon(icon);
   }

   public void addAfter(ItemStack order, ItemStack[] items, TabVisibility visibility) {
      this.callback.addAfter(order, items, visibility);
   }

   public void addAfter(ItemStack order, ItemStack[] items) {
      this.callback.addAfter(order, items, TabVisibility.PARENT_AND_SEARCH_TABS);
   }

   public void add(ItemStack[] items, TabVisibility visibility) {
      this.callback.addAfter(ItemStack.EMPTY, items, visibility);
   }

   public void add(ItemStack[] items) {
      this.callback.addAfter(ItemStack.EMPTY, items, TabVisibility.PARENT_AND_SEARCH_TABS);
   }

   public void addBefore(ItemStack order, ItemStack[] items, TabVisibility visibility) {
      this.callback.addBefore(order, items, visibility);
   }

   public void addBefore(ItemStack order, ItemStack[] items) {
      this.callback.addBefore(order, items, TabVisibility.PARENT_AND_SEARCH_TABS);
   }

   public void removeFromParent(ItemPredicate filter) {
      this.callback.remove(filter, true, false);
   }

   public void removeFromSearch(ItemPredicate filter) {
      this.callback.remove(filter, false, true);
   }

   public void remove(ItemPredicate filter) {
      this.callback.remove(filter, true, true);
   }
}
