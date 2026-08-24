package net.blay09.mods.inventoryessentials.client;

import net.blay09.mods.inventoryessentials.InventoryEssentialsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

final class StackRefillHandler {
   private int pendingMenuSlot = -1;
   private ItemStack pendingStack = ItemStack.EMPTY;

   public void beforeUseItemOn(Minecraft client, LocalPlayer player, InteractionHand hand) {
      if (hand == InteractionHand.MAIN_HAND) {
         this.reset();
         if (client.player == player && client.screen == null && InventoryEssentialsConfig.getActive().enableStackRefill) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof BlockItem) {
               this.pendingMenuSlot = 36 + player.getInventory().selected;
               this.pendingStack = stack.copy();
            }
         }
      }
   }

   public void afterUseItemOn(Minecraft client, LocalPlayer player, InteractionHand hand, InteractionResult result) {
      if (hand == InteractionHand.MAIN_HAND && this.pendingMenuSlot != -1) {
         if (client.player == player && result.consumesAction() && player.getItemInHand(hand).isEmpty()) {
            ItemStack refillStack = this.pendingStack;
            int targetMenuSlot = this.pendingMenuSlot;
            this.reset();
            if (client.gameMode != null && client.screen == null && InventoryEssentialsConfig.getActive().enableStackRefill) {
               InventoryMenu menu = player.inventoryMenu;
               if (menu.getCarried().isEmpty()) {
                  Slot targetSlot = menu.isValidSlotIndex(targetMenuSlot) ? menu.getSlot(targetMenuSlot) : null;
                  if (targetSlot != null && targetSlot.getItem().isEmpty()) {
                     Slot sourceSlot = this.findReplacementSlot(menu, refillStack);
                     if (sourceSlot != null) {
                        this.refillSlot(menu, sourceSlot, targetSlot, refillStack);
                     }
                  }
               }
            }
         } else {
            this.reset();
         }
      }
   }

   private boolean matchesRefillStack(ItemStack emptiedStack, ItemStack refillStack) {
      return refillStack.getItem() instanceof BlockItem && ItemStack.isSameItemSameComponents(emptiedStack, refillStack);
   }

   private Slot findReplacementSlot(AbstractContainerMenu menu, ItemStack refillStack) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null) {
         return null;
      } else {
         for (Slot slot : menu.slots) {
            if (slot.container instanceof Inventory
               && slot.getContainerSlot() >= Inventory.getSelectionSize()
               && slot.getContainerSlot() < 36
               && slot.isActive()
               && !slot.isFake()
               && slot.hasItem()
               && slot.mayPickup(player)) {
               ItemStack slotStack = slot.getItem();
               if (this.matchesRefillStack(refillStack, slotStack)) {
                  return slot;
               }
            }
         }

         return null;
      }
   }

   private void refillSlot(AbstractContainerMenu menu, Slot sourceSlot, Slot targetSlot, ItemStack refillStack) {
      this.clickSlot(menu, sourceSlot);
      if (!this.matchesRefillStack(refillStack, menu.getCarried())) {
         if (!menu.getCarried().isEmpty()) {
            this.clickSlot(menu, sourceSlot);
         }
      } else {
         this.clickSlot(menu, targetSlot);
         if (!menu.getCarried().isEmpty()) {
            this.clickSlot(menu, sourceSlot);
         }
      }
   }

   private void clickSlot(AbstractContainerMenu menu, Slot slot) {
      LocalPlayer player = Minecraft.getInstance().player;
      MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
      if (player != null && gameMode != null) {
         gameMode.handleInventoryMouseClick(menu.containerId, slot.index, 0, ClickType.PICKUP, player);
      }
   }

   public void reset() {
      this.pendingMenuSlot = -1;
      this.pendingStack = ItemStack.EMPTY;
   }
}
