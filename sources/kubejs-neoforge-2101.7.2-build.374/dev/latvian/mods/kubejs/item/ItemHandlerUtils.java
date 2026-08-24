package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class ItemHandlerUtils {
   public static void giveItemToPlayer(Player player, @NotNull ItemStack stack, int preferredSlot) {
      ItemHandlerHelper.giveItemToPlayer(player, stack, preferredSlot);
   }

   @NotNull
   public static ItemStack insertItemStacked(InventoryKJS inventory, @NotNull ItemStack stack, boolean simulate) {
      if (inventory == null || stack.isEmpty()) {
         return stack;
      } else if (!stack.isStackable()) {
         return insertItem(inventory, stack, simulate);
      } else {
         int sizeInventory = inventory.kjs$getSlots();

         for (int i = 0; i < sizeInventory; i++) {
            ItemStack slot = inventory.kjs$getStackInSlot(i);
            if (ItemStack.isSameItemSameComponents(slot, stack)) {
               stack = inventory.kjs$insertItem(i, stack, simulate);
               if (stack.isEmpty()) {
                  break;
               }
            }
         }

         if (!stack.isEmpty()) {
            for (int ix = 0; ix < sizeInventory; ix++) {
               if (inventory.kjs$getStackInSlot(ix).isEmpty()) {
                  stack = inventory.kjs$insertItem(ix, stack, simulate);
                  if (stack.isEmpty()) {
                     break;
                  }
               }
            }
         }

         return stack;
      }
   }

   @NotNull
   public static ItemStack insertItem(InventoryKJS dest, @NotNull ItemStack stack, boolean simulate) {
      if (dest != null && !stack.isEmpty()) {
         for (int i = 0; i < dest.kjs$getSlots(); i++) {
            stack = dest.kjs$insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
               return ItemStack.EMPTY;
            }
         }

         return stack;
      } else {
         return stack;
      }
   }
}
