package net.joefoxe.hexerei.container;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class ItemStackContainer extends AbstractContainerMenu {
   private IItemHandler playerInventory;
   protected Player playerEntity;
   protected final ItemStack stack;
   protected final InteractionHand hand;
   protected final int slotIndex;
   protected final int itemIndex;
   int containerSize;
   private static final int HOTBAR_SLOT_COUNT = 9;
   private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
   private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
   private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
   private static final int VANILLA_SLOT_COUNT = 36;
   private static final int VANILLA_FIRST_SLOT_INDEX = 0;
   private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 36;

   protected ItemStackContainer(@Nullable MenuType<?> pMenuType, int windowId, Inventory playerInventory, ItemStack stack, InteractionHand hand, int slotIndex) {
      super(pMenuType, windowId);
      this.stack = stack;
      this.hand = hand;
      this.slotIndex = slotIndex;
      this.playerInventory = new InvWrapper(playerInventory);
      this.addPlayerSlots();
      int playerSlots = this.slots.size();
      this.addContainerSlots();
      this.containerSize = this.slots.size() - playerSlots;
      this.playerEntity = playerInventory.player;
      this.itemIndex = hand == InteractionHand.MAIN_HAND ? 27 + slotIndex : -999;
   }

   public ItemStack getSelf() {
      return this.stack;
   }

   public boolean stillValid(Player pPlayer) {
      return true;
   }

   protected void addContainerSlots() {
   }

   protected void addPlayerSlots() {
   }

   private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
      for (int i = 0; i < amount; i++) {
         this.addSlot(new SlotItemHandler(handler, index, x, y));
         x += dx;
         index++;
      }

      return index;
   }

   public void clicked(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
      if (pSlotId != this.itemIndex) {
         super.clicked(pSlotId, pButton, pClickType, pPlayer);
      }
   }

   private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
      for (int j = 0; j < verAmount; j++) {
         index = this.addSlotRange(handler, index, x, y, horAmount, dx);
         y += dy;
      }

      return index;
   }

   public void layoutPlayerInventorySlots(int leftCol, int topRow) {
      this.addSlotBox(this.playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
      topRow += 58;
      this.addSlotRange(this.playerInventory, 0, leftCol, topRow, 9, 18);
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      if (index == this.itemIndex) {
         return ItemStack.EMPTY;
      } else {
         Slot sourceSlot = (Slot)this.slots.get(index);
         if (sourceSlot != null && sourceSlot.hasItem()) {
            ItemStack sourceStack = sourceSlot.getItem();
            ItemStack copyOfSourceStack = sourceStack.copy();
            if (index < 36) {
               if (!this.moveItemStackTo(sourceStack, 36, 36 + this.containerSize, false)) {
                  return ItemStack.EMPTY;
               }
            } else {
               if (index >= 36 + this.containerSize) {
                  System.out.println("Invalid slotIndex:" + index);
                  return ItemStack.EMPTY;
               }

               if (!this.moveItemStackTo(sourceStack, 0, 36, false)) {
                  return ItemStack.EMPTY;
               }
            }

            if (sourceStack.getCount() == 0) {
               sourceSlot.set(ItemStack.EMPTY);
            } else {
               sourceSlot.setChanged();
            }

            sourceSlot.onTake(playerIn, sourceStack);
            return copyOfSourceStack;
         } else {
            return ItemStack.EMPTY;
         }
      }
   }
}
