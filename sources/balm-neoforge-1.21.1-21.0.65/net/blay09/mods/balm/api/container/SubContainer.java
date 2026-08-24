package net.blay09.mods.balm.api.container;

import java.util.ArrayList;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SubContainer implements Container, WorldlyContainer, ExtractionAwareContainer {
   private final Container container;
   private final int minSlot;
   private final int maxSlot;

   public SubContainer(Container container, int minSlot, int maxSlot) {
      this.container = container;
      this.minSlot = minSlot;
      this.maxSlot = maxSlot;
   }

   public int getContainerSize() {
      return this.maxSlot - this.minSlot;
   }

   public ItemStack getItem(int slot) {
      return this.containsSlot(slot) ? this.container.getItem(slot + this.minSlot) : ItemStack.EMPTY;
   }

   public ItemStack removeItem(int slot, int amount) {
      return this.containsSlot(slot) ? this.container.removeItem(slot + this.minSlot, amount) : ItemStack.EMPTY;
   }

   public ItemStack removeItemNoUpdate(int slot) {
      return this.containsSlot(slot) ? this.container.removeItemNoUpdate(slot + this.minSlot) : ItemStack.EMPTY;
   }

   public void setItem(int slot, ItemStack itemStack) {
      if (this.containsSlot(slot)) {
         this.container.setItem(slot + this.minSlot, itemStack);
      }
   }

   public void startOpen(Player player) {
      this.container.startOpen(player);
   }

   public void stopOpen(Player player) {
      this.container.stopOpen(player);
   }

   public boolean canPlaceItem(int slot, ItemStack itemStack) {
      return this.containsSlot(slot) && this.container.canPlaceItem(slot + this.minSlot, itemStack);
   }

   public boolean isEmpty() {
      for (int i = this.minSlot; i < this.maxSlot; i++) {
         if (!this.container.getItem(i).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public boolean stillValid(Player player) {
      return this.container.stillValid(player);
   }

   public int getMaxStackSize() {
      return this.container.getMaxStackSize();
   }

   public void setChanged() {
      this.container.setChanged();
   }

   private boolean containsSlot(int slot) {
      return slot + this.minSlot < this.maxSlot;
   }

   public boolean containsOuterSlot(int slot) {
      return slot >= this.minSlot && slot < this.maxSlot;
   }

   public void clearContent() {
      for (int i = this.minSlot; i < this.maxSlot; i++) {
         this.container.setItem(i, ItemStack.EMPTY);
      }
   }

   @Override
   public boolean canExtractItem(int slot) {
      return !(this.container instanceof ExtractionAwareContainer extractionAwareContainer)
         ? this.containsSlot(slot)
         : this.containsSlot(slot) && extractionAwareContainer.canExtractItem(slot + this.minSlot);
   }

   public boolean canTakeItem(Container container, int slot, ItemStack itemStack) {
      return this.containsSlot(slot) && this.container.canTakeItem(this.container, slot + this.minSlot, itemStack);
   }

   public int[] getSlotsForFace(Direction direction) {
      if (this.container instanceof WorldlyContainer worldContainer) {
         int[] original = worldContainer.getSlotsForFace(direction);
         ArrayList<Integer> result = new ArrayList<>();

         for (int outerSlot : original) {
            if (this.containsOuterSlot(outerSlot)) {
               result.add(outerSlot - this.minSlot);
            }
         }

         return result.stream().mapToInt(ix -> ix).toArray();
      } else {
         int[] result = new int[this.getContainerSize()];
         int i = 0;

         while (i < result.length) {
            result[i] = i++;
         }

         return result;
      }
   }

   public int[] getOuterSlotsForFace(Direction direction) {
      if (this.container instanceof WorldlyContainer worldContainer) {
         int[] original = worldContainer.getSlotsForFace(direction);
         ArrayList<Integer> result = new ArrayList<>();

         for (int outerSlot : original) {
            if (this.containsOuterSlot(outerSlot)) {
               result.add(outerSlot);
            }
         }

         return result.stream().mapToInt(ix -> ix).toArray();
      } else {
         int[] slots = new int[this.maxSlot - this.minSlot];

         for (int i = 0; i < slots.length; i++) {
            slots[i] = i + this.minSlot;
         }

         return slots;
      }
   }

   public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
      return !(this.container instanceof WorldlyContainer worldlyContainer)
         ? this.canPlaceItem(slot, itemStack)
         : this.containsSlot(slot) && worldlyContainer.canPlaceItemThroughFace(slot + this.minSlot, itemStack, direction);
   }

   public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
      return !(this.container instanceof WorldlyContainer worldlyContainer)
         ? this.canTakeItem(this, slot, itemStack)
         : this.containsSlot(slot) && worldlyContainer.canTakeItemThroughFace(slot + this.minSlot, itemStack, direction);
   }
}
