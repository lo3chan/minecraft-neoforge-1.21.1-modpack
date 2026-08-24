package net.Pandarix.screen;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IdentifyingMenu extends BAAbstractContainerMenu {
   private final ContainerData data;
   private final Container container;

   public IdentifyingMenu(int syncId, Inventory inventory) {
      this(syncId, inventory, new SimpleContainer(3), new SimpleContainerData(2));
   }

   public IdentifyingMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
      super((MenuType<?>)ModMenuTypes.IDENTIFYING_MENU.get(), syncId);
      checkContainerSize(playerInventory, 3);
      this.data = data;
      this.container = container;
      this.createPlayerInventory(playerInventory);
      this.createPlayerHotbar(playerInventory);
      this.addSlot(new Slot(container, 0, 80, 20));
      this.addSlot(new Slot(container, 0, 80, 20));
      this.addSlot(new Slot(container, 1, 26, 48));
      this.addSlot(new Slot(container, 2, 134, 48));
      this.addDataSlots(data);
   }

   public boolean isCrafting() {
      return this.data.get(0) > 0;
   }

   public int getScaledProgress() {
      int progress = this.data.get(0);
      int maxProgress = this.data.get(1);
      int progressArrowSize = 74;
      return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
   }

   @NotNull
   public ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
      ItemStack newStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(pIndex);
      if (slot.hasItem()) {
         ItemStack originalStack = slot.getItem();
         newStack = originalStack.copy();
         if (originalStack.getItem() instanceof BrushItem) {
            if (this.isInInv(pIndex)) {
               if (!this.moveItemStackTo(originalStack, this.slots.size() - 3, this.slots.size() - 2, true)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(originalStack, 0, this.slots.size() - 3 - 1, false)) {
               return ItemStack.EMPTY;
            }
         }

         if (this.isInInv(pIndex)) {
            if (!this.moveItemStackTo(originalStack, this.slots.size() - 2, this.slots.size() - 1, true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(originalStack, 0, this.slots.size() - 3 - 1, false)) {
            return ItemStack.EMPTY;
         }

         if (!this.isInInv(pIndex) && !this.moveItemStackTo(originalStack, 0, this.slots.size() - 3 - 1, false)) {
            return ItemStack.EMPTY;
         }

         if (originalStack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (originalStack.getCount() == newStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(pPlayer, originalStack);
      }

      return newStack;
   }

   public boolean stillValid(@NotNull Player pPlayer) {
      return this.container.stillValid(pPlayer);
   }

   private boolean isInInv(int invSlot) {
      return invSlot < this.slots.size() - 3 - 1;
   }
}
