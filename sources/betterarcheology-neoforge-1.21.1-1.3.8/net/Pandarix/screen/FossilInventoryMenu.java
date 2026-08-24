package net.Pandarix.screen;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FossilInventoryMenu extends BAAbstractContainerMenu {
   private final Container container;
   private static final int HOTBAR_SLOT_COUNT = 9;
   private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
   private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
   private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
   private static final int VANILLA_SLOT_COUNT = 36;
   private static final int VANILLA_FIRST_SLOT_INDEX = 0;
   private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 36;
   private static final int TE_INVENTORY_SLOT_COUNT = 1;

   public FossilInventoryMenu(int syncId, Inventory inventory) {
      this(syncId, inventory, new SimpleContainer(1));
   }

   public FossilInventoryMenu(int syncId, Inventory playerInventory, Container container) {
      super((MenuType<?>)ModMenuTypes.FOSSIL_MENU.get(), syncId);
      checkContainerSize(playerInventory, 1);
      this.container = container;
      this.createPlayerInventory(playerInventory);
      this.createPlayerHotbar(playerInventory);
      this.addSlot(new Slot(container, 0, 80, 22));
   }

   public boolean stillValid(Player pPlayer) {
      return this.container.stillValid(pPlayer);
   }

   @NotNull
   public ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
      Slot sourceSlot = (Slot)this.slots.get(index);
      if (!sourceSlot.hasItem()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack sourceStack = sourceSlot.getItem();
         ItemStack copyOfSourceStack = sourceStack.copy();
         if (index < 36) {
            if (!this.moveItemStackTo(sourceStack, 36, 37, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (index >= 37) {
               System.out.println("Invalid slotIndex:" + index);
               return ItemStack.EMPTY;
            }

            if (!this.moveItemStackTo(sourceStack, 0, 36, false)) {
               return ItemStack.EMPTY;
            }
         }

         if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
         }

         sourceSlot.onTake(playerIn, sourceStack);
         sourceSlot.setChanged();
         return copyOfSourceStack;
      }
   }
}
