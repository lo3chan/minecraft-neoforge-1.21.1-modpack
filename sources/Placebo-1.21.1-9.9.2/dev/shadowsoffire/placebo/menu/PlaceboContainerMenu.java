package dev.shadowsoffire.placebo.menu;

import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;

public abstract class PlaceboContainerMenu extends AbstractContainerMenu implements QuickMoveHandler.QuickMoveMenu {
   protected final Level level;
   protected final QuickMoveHandler mover = new QuickMoveHandler();
   protected int playerInvStart = -1;
   protected int hotbarStart = -1;

   protected PlaceboContainerMenu(MenuType<?> type, int id, Inventory pInv) {
      super(type, id);
      this.level = pInv.player.level();
   }

   protected void addPlayerSlots(Inventory pInv, int x, int y) {
      this.playerInvStart = this.slots.size();

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(pInv, column + row * 9 + 9, x + column * 18, y + row * 18));
         }
      }

      this.hotbarStart = this.slots.size();

      for (int row = 0; row < 9; row++) {
         this.addSlot(new Slot(pInv, row, x + row * 18, y + 58));
      }
   }

   protected void registerInvShuffleRules() {
      if (this.hotbarStart != -1 && this.playerInvStart != -1) {
         this.mover.registerRule((stack, slot) -> slot >= this.hotbarStart, this.playerInvStart, this.hotbarStart);
         this.mover.registerRule((stack, slot) -> slot >= this.playerInvStart, this.hotbarStart, this.slots.size());
      } else {
         throw new UnsupportedOperationException("Attempted to register inv shuffle rules with no player inv slots.");
      }
   }

   public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
      return this.mover.quickMoveStack(this, pPlayer, pIndex);
   }

   @Override
   public boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
      return super.moveItemStackTo(pStack, pStartIndex, pEndIndex, pReverseDirection);
   }

   public void setData(int pId, int pData) {
      super.setData(pId, pData);
      this.updateDataSlotListeners(pId, pData);
   }

   public void addDataListener(final IDataUpdateListener listener) {
      this.addSlotListener(new ContainerListener() {
         public void slotChanged(AbstractContainerMenu pContainerToSend, int pDataSlotIndex, ItemStack pStack) {
         }

         public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
            listener.dataUpdated(pDataSlotIndex, pValue);
         }
      });
   }

   @Deprecated(
      forRemoval = true
   )
   public void addSlotListener(final SlotUpdateListener listener) {
      this.addSlotListener(new ContainerListener() {
         public void slotChanged(AbstractContainerMenu pContainerToSend, int pDataSlotIndex, ItemStack pStack) {
            listener.slotUpdated(pDataSlotIndex, pStack);
         }

         public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
         }
      });
   }

   protected void clearContainer(Player player, IItemHandler inv) {
      if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected()) {
         for (int slot = 0; slot < inv.getSlots(); slot++) {
            player.drop(inv.getStackInSlot(slot), false);
         }
      } else {
         for (int slot = 0; slot < inv.getSlots(); slot++) {
            Inventory inventory = player.getInventory();
            if (inventory.player instanceof ServerPlayer) {
               inventory.placeItemBackInInventory(inv.getStackInSlot(slot));
            }
         }
      }
   }

   protected class UpdatingSlot extends FilteredSlot {
      public UpdatingSlot(InternalItemHandler handler, int index, int x, int y, Predicate<ItemStack> filter) {
         super(handler, index, x, y, filter);
      }

      public void setChanged() {
         super.setChanged();
         PlaceboContainerMenu.this.slotsChanged(null);
      }
   }
}
