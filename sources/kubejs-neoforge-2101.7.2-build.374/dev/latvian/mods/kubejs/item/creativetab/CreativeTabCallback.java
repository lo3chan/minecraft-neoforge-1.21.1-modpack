package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;

public interface CreativeTabCallback {
   void addAfter(ItemStack order, ItemStack[] items, TabVisibility visibility);

   void addBefore(ItemStack order, ItemStack[] items, TabVisibility visibility);

   void remove(ItemPredicate filter, boolean removeParent, boolean removeSearch);
}
