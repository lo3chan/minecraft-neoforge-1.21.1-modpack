package net.blay09.mods.balm.api.container;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DelegateContainer implements Container, WorldlyContainer, ExtractionAwareContainer {
   private final Container delegate;

   public DelegateContainer(Container delegate) {
      this.delegate = delegate;
   }

   public int getMaxStackSize() {
      return this.delegate.getMaxStackSize();
   }

   public void startOpen(Player player) {
      this.delegate.startOpen(player);
   }

   public void stopOpen(Player player) {
      this.delegate.stopOpen(player);
   }

   public boolean canPlaceItem(int slot, ItemStack itemStack) {
      return this.delegate.canPlaceItem(slot, itemStack);
   }

   public int countItem(Item item) {
      return this.delegate.countItem(item);
   }

   public boolean hasAnyOf(Set<Item> items) {
      return this.delegate.hasAnyOf(items);
   }

   public int getContainerSize() {
      return this.delegate.getContainerSize();
   }

   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   public ItemStack getItem(int slot) {
      return this.delegate.getItem(slot);
   }

   public ItemStack removeItem(int slot, int count) {
      return this.delegate.removeItem(slot, count);
   }

   public ItemStack removeItemNoUpdate(int slot) {
      return this.delegate.removeItemNoUpdate(slot);
   }

   public void setItem(int slot, ItemStack itemStack) {
      this.delegate.setItem(slot, itemStack);
   }

   public void setChanged() {
      this.delegate.setChanged();
   }

   public boolean stillValid(Player player) {
      return this.delegate.stillValid(player);
   }

   public void clearContent() {
      this.delegate.clearContent();
   }

   @Override
   public boolean canExtractItem(int slot) {
      return this.delegate instanceof ExtractionAwareContainer extractionAwareContainer ? extractionAwareContainer.canExtractItem(slot) : true;
   }

   public boolean canTakeItem(Container container, int slot, ItemStack itemStack) {
      return this.delegate.canTakeItem(this, slot, itemStack);
   }

   public boolean hasAnyMatching(Predicate<ItemStack> predicate) {
      return this.delegate.hasAnyMatching(predicate);
   }

   public int[] getSlotsForFace(Direction direction) {
      if (this.delegate instanceof WorldlyContainer worldContainer) {
         return worldContainer.getSlotsForFace(direction);
      } else {
         int[] slots = new int[this.delegate.getContainerSize()];
         int i = 0;

         while (i < slots.length) {
            slots[i] = i++;
         }

         return slots;
      }
   }

   public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
      return this.delegate instanceof WorldlyContainer worldContainer
         ? worldContainer.canPlaceItemThroughFace(slot, itemStack, direction)
         : this.canPlaceItem(slot, itemStack);
   }

   public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
      return this.delegate instanceof WorldlyContainer worldContainer
         ? worldContainer.canTakeItemThroughFace(slot, itemStack, direction)
         : this.canTakeItem(this, slot, itemStack);
   }
}
