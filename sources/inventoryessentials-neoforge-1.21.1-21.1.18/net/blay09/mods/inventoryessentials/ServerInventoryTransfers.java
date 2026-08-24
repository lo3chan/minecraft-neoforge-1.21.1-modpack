package net.blay09.mods.inventoryessentials;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ServerInventoryTransfers {
   public static void singleTransfer(ServerPlayer player, AbstractContainerMenu menu, Slot slot) {
      if (slot.mayPickup(player)) {
         ItemStack sourceStack = slot.getItem();
         if (sourceStack.getCount() == 1) {
            menu.clicked(slot.index, 0, ClickType.QUICK_MOVE, player);
         } else if (!sourceStack.isEmpty()) {
            ItemStack restStack = sourceStack.copy();
            sourceStack.setCount(1);
            slot.set(sourceStack);
            restStack.shrink(1);
            menu.clicked(slot.index, 0, ClickType.QUICK_MOVE, player);
            if (!slot.hasItem()) {
               slot.set(restStack);
            } else if (!player.addItem(restStack)) {
               player.drop(restStack, false);
            }
         }
      }
   }
}
