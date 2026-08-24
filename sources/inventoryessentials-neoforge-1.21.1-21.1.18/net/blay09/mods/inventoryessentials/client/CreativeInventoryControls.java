package net.blay09.mods.inventoryessentials.client;

import net.blay09.mods.inventoryessentials.InventoryOperations;
import net.blay09.mods.inventoryessentials.mixin.SlotWrapperAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public class CreativeInventoryControls extends ClientOnlyInventoryControls {
   @Override
   protected InventoryOperations createOperations() {
      return new InventoryOperations(this::slotClick, slot -> slot.container instanceof Inventory);
   }

   @Override
   protected boolean isValidTargetSlot(Slot slot) {
      return slot.container instanceof Inventory;
   }

   @Override
   protected void slotClick(AbstractContainerMenu menu, Slot slot, int mouseButton, ClickType clickType) {
      if (slot instanceof SlotWrapperAccessor accessor) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            this.slotClick(player.inventoryMenu, accessor.getTarget().index, mouseButton, clickType);
         }
      } else {
         this.slotClick(menu, slot.index, mouseButton, clickType);
      }
   }

   @Override
   protected void slotClick(AbstractContainerMenu menu, int slotIndex, int mouseButton, ClickType clickType) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         menu.clicked(slotIndex, mouseButton, clickType, player);
         player.inventoryMenu.broadcastChanges();
      }
   }
}
