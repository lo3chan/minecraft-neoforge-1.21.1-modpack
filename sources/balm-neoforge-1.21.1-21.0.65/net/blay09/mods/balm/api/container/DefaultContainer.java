package net.blay09.mods.balm.api.container;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DefaultContainer implements ImplementedContainer, WorldlyContainer {
   private NonNullList<ItemStack> items;

   public DefaultContainer(int size) {
      this.items = NonNullList.withSize(size, ItemStack.EMPTY);
   }

   public DefaultContainer(NonNullList<ItemStack> items) {
      this.items = items;
   }

   @Override
   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public void deserialize(CompoundTag tag, Provider provider) {
      this.items = ImplementedContainer.deserializeInventory(tag, this.items.size(), provider);
   }

   public CompoundTag serialize(Provider provider) {
      return this.serializeInventory(provider);
   }

   public int[] getSlotsForFace(Direction direction) {
      int[] slots = new int[this.items.size()];
      int i = 0;

      while (i < slots.length) {
         slots[i] = i++;
      }

      return slots;
   }

   public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
      return this.canPlaceItem(slot, itemStack);
   }

   public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
      return this.canTakeItem(this, slot, itemStack);
   }
}
