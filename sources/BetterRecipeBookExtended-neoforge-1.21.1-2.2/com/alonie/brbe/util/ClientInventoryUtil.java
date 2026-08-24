package com.alonie.brbe.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ClientInventoryUtil {
   public static void storeItem(int fromSlot, Predicate<Integer> indexCheck) {
      MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
      Minecraft minecraft = Minecraft.getInstance();
      AbstractContainerMenu menu = minecraft.player.containerMenu;
      if (menu != null) {
         if (fromSlot >= 0) {
            if (((Slot)menu.slots.get(fromSlot)).getItem().isEmpty()) {
               return;
            }

            if (!menu.getCarried().isEmpty()) {
               storeItem(-1, indexCheck);
            }

            gameMode.handleInventoryMouseClick(menu.containerId, fromSlot, 0, ClickType.PICKUP, minecraft.player);
         } else if (menu.getCarried().isEmpty()) {
            return;
         }

         List<Slot> slots = new ArrayList<>(menu.slots);
         slots.sort((a, b) -> Boolean.compare(a.getItem().isEmpty(), b.getItem().isEmpty()));
         int count = menu.getCarried().getCount();

         for (Slot slot : slots) {
            if (count <= 0) {
               break;
            }

            if (indexCheck.test(slot.index) && (ItemStack.isSameItemSameComponents(menu.getCarried(), slot.getItem()) || slot.getItem().isEmpty())) {
               int slotCount = slot.getItem().getCount();
               if (slotCount < slot.getMaxStackSize()) {
                  count -= Math.max(0, slot.getMaxStackSize() - slotCount);
                  gameMode.handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.PICKUP, minecraft.player);
               }
            }
         }

         if (count > 0) {
            dropItem(-1, true, false);
         }
      }
   }

   public static void moveItemToSlot(AbstractContainerMenu menu, int fromSlotIndex, int toSlotId) {
      assert Minecraft.getInstance().gameMode != null;

      storeItem(-1, i -> i > 4);
      Minecraft.getInstance()
         .gameMode
         .handleInventoryMouseClick(menu.containerId, menu.getSlot(fromSlotIndex).index, 0, ClickType.PICKUP, Minecraft.getInstance().player);
      Minecraft.getInstance().gameMode.handleInventoryMouseClick(menu.containerId, toSlotId, 0, ClickType.PICKUP, Minecraft.getInstance().player);
      storeItem(-1, i -> i > 4);
   }

   public static void dropItem(int slot, boolean wholeStack, boolean force) {
      MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
      Minecraft minecraft = Minecraft.getInstance();
      AbstractContainerMenu menu = minecraft.player.containerMenu;
      if (menu != null) {
         ClickType type = ClickType.THROW;
         if (slot < 0) {
            slot = -999;
            type = ClickType.PICKUP;
            if (!force && menu.getCarried().isEmpty()) {
               return;
            }
         } else if (!force && ((Slot)menu.slots.get(slot)).getItem().isEmpty()) {
            return;
         }

         gameMode.handleInventoryMouseClick(menu.containerId, slot, wholeStack ? 0 : 1, type, minecraft.player);
      }
   }
}
