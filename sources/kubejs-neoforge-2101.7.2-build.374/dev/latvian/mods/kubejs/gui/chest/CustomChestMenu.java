package dev.latvian.mods.kubejs.gui.chest;

import dev.latvian.mods.kubejs.script.ConsoleJS;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CustomChestMenu extends AbstractContainerMenu {
   public static final MenuType[] TYPES = new MenuType[]{
      MenuType.GENERIC_9x1, MenuType.GENERIC_9x2, MenuType.GENERIC_9x3, MenuType.GENERIC_9x4, MenuType.GENERIC_9x5, MenuType.GENERIC_9x6
   };
   public ChestMenuData data;

   public CustomChestMenu(int containerId, ChestMenuData data) {
      super(TYPES[data.rows - 1], containerId);
      this.data = data;
      int k = (data.rows - 4) * 18;

      for (int y = 0; y < data.rows; y++) {
         for (int x = 0; x < 9; x++) {
            this.addSlot(new ChestMenuContainerSlot(this, x + y * 9, 8 + x * 18, 18 + y * 18));
         }
      }

      if (data.playerSlots) {
         for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
               this.addSlot(new Slot(data.capturedInventory, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + k));
            }
         }

         for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(data.capturedInventory, x, 8 + x * 18, 161 + k));
         }
      } else {
         for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
               this.addSlot(new ChestMenuContainerSlot(this, data.rows * 9 + x + y * 9, 8 + x * 18, 103 + y * 18 + k));
            }
         }

         for (int x = 0; x < 9; x++) {
            this.addSlot(new ChestMenuContainerSlot(this, data.rows * 9 + 27 + x, 8 + x * 18, 161 + k));
         }
      }
   }

   public ItemStack quickMoveStack(Player player, int slot) {
      return ItemStack.EMPTY;
   }

   public void clicked(int slot, int button, ClickType clickType, Player player) {
      if (this.data.playerSlots && slot >= this.data.rows * 9) {
         if (this.data.inventoryClicked != null && slot >= 0 && slot < this.slots.size()) {
            this.data.inventoryClicked.onClick(new ChestMenuInventoryClickEvent(this.getSlot(slot), clickType, button));
         }
      } else {
         if (slot >= this.data.rows * 9) {
            super.clicked(slot, button, clickType, player);
         }

         try {
            this.data.handleClick(slot, clickType, button);
         } catch (Exception var6) {
            ConsoleJS.SERVER.error("Error handling chest gui click", var6);
         }

         this.broadcastFullState();
      }
   }

   public boolean stillValid(Player player) {
      return true;
   }

   public void removed(Player player) {
      if (this.data.closed != null) {
         this.data.closed.run();
      }

      player.inventoryMenu.broadcastFullState();
   }

   public ItemStack getCarried() {
      return this.data.mouseItem;
   }

   public void setCarried(ItemStack stack) {
      this.data.mouseItem = stack;
   }

   public void initializeContents(int stateId, List<ItemStack> list, ItemStack carried) {
      super.initializeContents(stateId, list, carried);
      this.data.mouseItem = carried;
   }
}
