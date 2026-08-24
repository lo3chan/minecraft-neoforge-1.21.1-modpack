package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.plugin.builtin.event.PlayerEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class KubeJSInventoryListener implements ContainerListener {
   public final Player player;

   public KubeJSInventoryListener(Player p) {
      this.player = p;
   }

   public void slotChanged(AbstractContainerMenu container, int index, ItemStack stack) {
      if (!stack.isEmpty() && container.getSlot(index).container == this.player.getInventory()) {
         ResourceKey<Item> key = stack.getItem().kjs$getKey();
         if (PlayerEvents.INVENTORY_CHANGED.hasListeners(key)) {
            PlayerEvents.INVENTORY_CHANGED.post(this.player, key, new InventoryChangedKubeEvent(this.player, stack, index));
         }
      }
   }

   public void dataChanged(AbstractContainerMenu container, int id, int value) {
   }
}
