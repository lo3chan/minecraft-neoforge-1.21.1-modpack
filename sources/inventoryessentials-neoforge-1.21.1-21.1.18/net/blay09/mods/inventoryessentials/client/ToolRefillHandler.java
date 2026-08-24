package net.blay09.mods.inventoryessentials.client;

import net.blay09.mods.inventoryessentials.InventoryEssentialsConfig;
import net.blay09.mods.inventoryessentials.InventoryEssentialsConfigData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

final class ToolRefillHandler {
   private int pendingMenuSlot = -1;
   private ItemStack pendingStack = ItemStack.EMPTY;

   public void beforeContainerSetSlot(Minecraft client, ClientboundContainerSetSlotPacket packet) {
      this.reset();
      LocalPlayer player = client.player;
      InventoryEssentialsConfigData config = InventoryEssentialsConfig.getActive();
      if (player != null && client.screen == null && config.enableToolRefill) {
         if (packet.getContainerId() == player.inventoryMenu.containerId) {
            int packetSlot = packet.getSlot();
            if (packetSlot >= 36 && packetSlot < 45) {
               int hotbarSlot = packetSlot - 36;
               if (hotbarSlot == player.getInventory().selected && packet.getItem().isEmpty()) {
                  ItemStack oldStack = player.inventoryMenu.getSlot(packetSlot).getItem();
                  if (this.canRefill(oldStack)) {
                     this.pendingMenuSlot = packetSlot;
                     this.pendingStack = oldStack.copy();
                  }
               }
            }
         }
      }
   }

   public void afterContainerSetSlot(Minecraft client, ClientboundContainerSetSlotPacket packet) {
      if (this.pendingMenuSlot != -1 && packet.getSlot() == this.pendingMenuSlot) {
         ItemStack refillStack = this.pendingStack;
         this.reset();
         LocalPlayer player = client.player;
         if (player != null && client.gameMode != null && client.screen == null && InventoryEssentialsConfig.getActive().enableToolRefill) {
            InventoryMenu menu = player.inventoryMenu;
            if (packet.getContainerId() == menu.containerId && menu.getCarried().isEmpty()) {
               Slot targetSlot = menu.isValidSlotIndex(packet.getSlot()) ? menu.getSlot(packet.getSlot()) : null;
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

   public void reset() {
      this.pendingMenuSlot = -1;
      this.pendingStack = ItemStack.EMPTY;
   }

   private boolean canRefill(ItemStack emptiedStack) {
      return emptiedStack.isDamageableItem() && emptiedStack.isDamageableItem() && emptiedStack.getDamageValue() >= emptiedStack.getMaxDamage() - 1;
   }

   private boolean matchesRefillStack(ItemStack emptiedStack, ItemStack refillStack) {
      return refillStack.isDamageableItem() && ItemStack.isSameItem(emptiedStack, refillStack);
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
}
