package net.astralya.hexalia.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class NestingBlockMenu extends AbstractContainerMenu {
   public static final int COLUMNS = 9;
   public static final int ROWS = 1;
   public static final int CONTAINER_SLOTS = 9;
   private static final int PLAYER_INV_SLOTS = 27;
   private static final int HOTBAR_SLOTS = 9;
   private final Container container;

   public NestingBlockMenu(int syncId, Inventory playerInventory, Container container) {
      super((MenuType)ModMenuTypes.NESTING_BLOCK.get(), syncId);
      this.container = container;
      checkContainerSize(container, 9);
      container.startOpen(playerInventory.player);
      int slotIndex = 0;
      int containerX = 8;
      int containerY = 18;

      for (int col = 0; col < 9; col++) {
         this.addSlot(new NestingBlockMenu.OutputOnlySlot(container, slotIndex++, containerX + col * 18, containerY));
      }

      int playerInvX = 8;
      int playerInvY = 64;

      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            int index = col + row * 9 + 9;
            this.addSlot(new Slot(playerInventory, index, playerInvX + col * 18, playerInvY + row * 18));
         }
      }

      int hotbarY = playerInvY + 58;

      for (int col = 0; col < 9; col++) {
         this.addSlot(new Slot(playerInventory, col, playerInvX + col * 18, hotbarY));
      }

      this.addDataSlots(new SimpleContainerData(0));
   }

   public NestingBlockMenu(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
      this(syncId, playerInventory, readContainer(playerInventory, buf));
   }

   private static Container readContainer(Inventory playerInventory, FriendlyByteBuf buf) {
      BlockPos pos = buf.readBlockPos();
      return (Container)(playerInventory.player.level().getBlockEntity(pos) instanceof Container c ? c : new SimpleContainer(9));
   }

   public Container getContainer() {
      return this.container;
   }

   public boolean stillValid(Player player) {
      return this.container.stillValid(player);
   }

   public ItemStack quickMoveStack(Player player, int index) {
      Slot slot = (Slot)this.slots.get(index);
      if (!slot.hasItem()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack stackInSlot = slot.getItem();
         ItemStack copy = stackInSlot.copy();
         int containerEnd = 9;
         int playerInvEnd = containerEnd + 27;
         int hotbarEnd = playerInvEnd + 9;
         if (index < containerEnd) {
            if (!this.moveItemStackTo(stackInSlot, containerEnd, hotbarEnd, true)) {
               return ItemStack.EMPTY;
            } else {
               if (stackInSlot.isEmpty()) {
                  slot.set(ItemStack.EMPTY);
               } else {
                  slot.setChanged();
               }

               return copy;
            }
         } else {
            return ItemStack.EMPTY;
         }
      }
   }

   public void removed(Player player) {
      super.removed(player);
      this.container.stopOpen(player);
   }

   private static final class OutputOnlySlot extends Slot {
      public OutputOnlySlot(Container container, int slot, int x, int y) {
         super(container, slot, x, y);
      }

      public boolean mayPlace(ItemStack stack) {
         return false;
      }
   }
}
