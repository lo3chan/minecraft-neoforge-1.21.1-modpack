package net.blay09.mods.balm.api.container;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CombinedContainer implements Container, WorldlyContainer, ExtractionAwareContainer {
   private final Container[] containers;
   private final int[] baseIndex;
   private final int totalSlots;

   public CombinedContainer(Container... containers) {
      this.containers = containers;
      this.baseIndex = new int[containers.length];
      int index = 0;

      for (int i = 0; i < containers.length; i++) {
         index += containers[i].getContainerSize();
         this.baseIndex[i] = index;
      }

      this.totalSlots = index;
   }

   private int getContainerIndexForSlot(int slot) {
      if (slot < 0) {
         return -1;
      } else {
         for (int i = 0; i < this.baseIndex.length; i++) {
            if (slot - this.baseIndex[i] < 0) {
               return i;
            }
         }

         return -1;
      }
   }

   private Container getContainerFromIndex(int index) {
      return (Container)(index >= 0 && index < this.containers.length ? this.containers[index] : EmptyContainer.INSTANCE);
   }

   private int getInnerSlotFromIndex(int slot, int index) {
      return index > 0 && index < this.baseIndex.length ? slot - this.baseIndex[index - 1] : slot;
   }

   private int getOuterSlotFromIndex(int slot, int index) {
      return index < this.baseIndex.length ? slot - this.baseIndex[index] : slot;
   }

   public int getContainerSize() {
      return this.totalSlots;
   }

   public boolean isEmpty() {
      return Arrays.stream(this.containers).allMatch(Container::isEmpty);
   }

   public ItemStack getItem(int slot) {
      int containerIndex = this.getContainerIndexForSlot(slot);
      Container container = this.getContainerFromIndex(containerIndex);
      return container.getItem(this.getInnerSlotFromIndex(slot, containerIndex));
   }

   public ItemStack removeItem(int slot, int amount) {
      int containerIndex = this.getContainerIndexForSlot(slot);
      Container container = this.getContainerFromIndex(containerIndex);
      return container.removeItem(this.getInnerSlotFromIndex(slot, containerIndex), amount);
   }

   public ItemStack removeItemNoUpdate(int slot) {
      int containerIndex = this.getContainerIndexForSlot(slot);
      Container container = this.getContainerFromIndex(containerIndex);
      return container.removeItemNoUpdate(this.getInnerSlotFromIndex(slot, containerIndex));
   }

   public void setItem(int slot, ItemStack itemStack) {
      int containerIndex = this.getContainerIndexForSlot(slot);
      Container container = this.getContainerFromIndex(containerIndex);
      container.setItem(this.getInnerSlotFromIndex(slot, containerIndex), itemStack);
   }

   public void setChanged() {
      for (Container container : this.containers) {
         container.setChanged();
      }
   }

   public boolean stillValid(Player player) {
      return Arrays.stream(this.containers).allMatch(container -> container.stillValid(player));
   }

   public void clearContent() {
      for (Container container : this.containers) {
         container.clearContent();
      }
   }

   @Override
   public boolean canExtractItem(int slot) {
      int containerIndex = this.getContainerIndexForSlot(slot);
      return this.getContainerFromIndex(containerIndex) instanceof ExtractionAwareContainer extractionAwareContainer
         ? extractionAwareContainer.canExtractItem(this.getInnerSlotFromIndex(slot, containerIndex))
         : true;
   }

   public void startOpen(Player player) {
      for (Container container : this.containers) {
         container.startOpen(player);
      }
   }

   public void stopOpen(Player player) {
      for (Container container : this.containers) {
         container.stopOpen(player);
      }
   }

   public boolean canPlaceItem(int slot, ItemStack itemStack) {
      int containerIndex = this.getContainerIndexForSlot(slot);
      Container container = this.getContainerFromIndex(containerIndex);
      return container.canPlaceItem(this.getInnerSlotFromIndex(slot, containerIndex), itemStack);
   }

   public boolean canTakeItem(Container container, int slot, ItemStack itemStack) {
      int containerIndex = this.getContainerIndexForSlot(slot);
      Container subContainer = this.getContainerFromIndex(containerIndex);
      return container.canTakeItem(subContainer, this.getInnerSlotFromIndex(slot, containerIndex), itemStack);
   }

   public int[] getSlotsForFace(Direction direction) {
      Set<Integer> slots = new HashSet<>();

      for (int index = 0; index < this.containers.length; index++) {
         Container container = this.containers[index];
         if (container instanceof WorldlyContainer worldlyContainer) {
            for (int i : worldlyContainer.getSlotsForFace(direction)) {
               slots.add(i);
            }
         } else {
            for (int i = 0; i < container.getContainerSize(); i++) {
               slots.add(this.getOuterSlotFromIndex(i, index));
            }
         }
      }

      return slots.stream().mapToInt(ix -> ix).toArray();
   }

   public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
      int containerIndex = this.getContainerIndexForSlot(slot);
      return this.getContainerFromIndex(containerIndex) instanceof WorldlyContainer worldlyContainer
         ? worldlyContainer.canPlaceItemThroughFace(this.getInnerSlotFromIndex(slot, containerIndex), itemStack, direction)
         : this.canPlaceItem(slot, itemStack);
   }

   public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
      int containerIndex = this.getContainerIndexForSlot(slot);
      return this.getContainerFromIndex(containerIndex) instanceof WorldlyContainer worldlyContainer
         ? worldlyContainer.canTakeItemThroughFace(slot, itemStack, direction)
         : this.canTakeItem(this, slot, itemStack);
   }
}
