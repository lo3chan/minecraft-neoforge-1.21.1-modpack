package io.wispforest.owo.client.screens;

import io.wispforest.owo.mixin.ScreenHandlerInvoker;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ScreenUtils {
   public static ItemStack handleSlotTransfer(AbstractContainerMenu handler, int clickedSlotIndex, int upperInventorySize) {
      NonNullList<Slot> slots = handler.slots;
      Slot clickedSlot = (Slot)slots.get(clickedSlotIndex);
      if (!clickedSlot.hasItem()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack clickedStack = clickedSlot.getItem();
         if (clickedSlotIndex < upperInventorySize) {
            if (!insertIntoSlotRange(handler, clickedStack, upperInventorySize, slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!insertIntoSlotRange(handler, clickedStack, 0, upperInventorySize)) {
            return ItemStack.EMPTY;
         }

         if (clickedStack.isEmpty()) {
            clickedSlot.setByPlayer(ItemStack.EMPTY);
         } else {
            clickedSlot.setChanged();
         }

         return clickedStack;
      }
   }

   public static boolean insertIntoSlotRange(AbstractContainerMenu handler, ItemStack addition, int beginIndex, int endIndex) {
      return insertIntoSlotRange(handler, addition, beginIndex, endIndex, false);
   }

   public static boolean insertIntoSlotRange(AbstractContainerMenu handler, ItemStack addition, int beginIndex, int endIndex, boolean fromLast) {
      return ((ScreenHandlerInvoker)handler).owo$insertItem(addition, beginIndex, endIndex, fromLast);
   }
}
