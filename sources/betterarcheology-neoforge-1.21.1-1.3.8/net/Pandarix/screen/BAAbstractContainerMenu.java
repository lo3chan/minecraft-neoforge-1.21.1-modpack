package net.Pandarix.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public abstract class BAAbstractContainerMenu extends AbstractContainerMenu {
   protected BAAbstractContainerMenu(@Nullable MenuType<?> menuType, int i) {
      super(menuType, i);
   }

   protected void createPlayerInventory(Inventory playerInv) {
      int columnNo = 9;
      int rowNoWithoutHotbar = 36 / columnNo - 1;

      for (int row = 0; row < rowNoWithoutHotbar; row++) {
         for (int column = 0; column < columnNo; column++) {
            this.addSlot(new Slot(playerInv, 9 + column + row * 9, 8 + column * 18, 86 + row * 18));
         }
      }
   }

   protected void createPlayerHotbar(Inventory playerInv) {
      for (int column = 0; column < 9; column++) {
         this.addSlot(new Slot(playerInv, column, 8 + column * 18, 144));
      }
   }
}
