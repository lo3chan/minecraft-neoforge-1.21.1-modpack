package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.util.SlotFilter;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

@RemapPrefixForJS("kjs$")
public interface RecipeInputKJS {
   default RecipeInput kjs$self() {
      return (RecipeInput)this;
   }

   default List<ItemStack> kjs$findAll(SlotFilter filter) {
      ArrayList<ItemStack> list = new ArrayList<>();
      int size = this.kjs$self().size();

      for (int i = 0; i < size; i++) {
         ItemStack stack = this.kjs$self().getItem(i);
         if (filter.checkFilter(i, stack)) {
            list.add(stack.copy());
         }
      }

      return list;
   }

   default List<ItemStack> kjs$findAll() {
      return this.kjs$findAll(SlotFilter.EMPTY);
   }

   default ItemStack find(SlotFilter filter, int skip) {
      for (ItemStack item : this.kjs$findAll(filter)) {
         if (skip == 0) {
            return item;
         }

         skip--;
      }

      return ItemStack.EMPTY;
   }

   default ItemStack find(SlotFilter filter) {
      return this.find(filter, 0);
   }
}
