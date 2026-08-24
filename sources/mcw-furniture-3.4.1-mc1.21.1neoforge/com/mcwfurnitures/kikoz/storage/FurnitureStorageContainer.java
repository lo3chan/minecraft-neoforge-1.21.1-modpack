package com.mcwfurnitures.kikoz.storage;

import com.mcwfurnitures.kikoz.init.BlockInit;
import com.mcwfurnitures.kikoz.init.ContainerInit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class FurnitureStorageContainer extends AbstractContainerMenu {
   private final ContainerLevelAccess containerAccess;

   public FurnitureStorageContainer(int ints, Inventory inv, FriendlyByteBuf buf) {
      this(ints, inv, new ItemStackHandler(27), BlockPos.ZERO);
   }

   public FurnitureStorageContainer(int i, Inventory inventory) {
      this(i, inventory, new ItemStackHandler(27), BlockPos.ZERO);
   }

   public FurnitureStorageContainer(int ints, Inventory inv, IItemHandler slots, BlockPos pos) {
      super((MenuType)ContainerInit.EXAMPLE_CHEST.get(), ints);
      this.containerAccess = ContainerLevelAccess.create(inv.player.level(), pos);
      int startX = 8;
      int startY = 18;
      int slotSizePlus2 = 18;

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inv, row * 9 + column, startX + column * slotSizePlus2, startY + row * slotSizePlus2));
         }
      }

      int startPlayerInvY = 84;

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inv, 9 + row * 9 + column, startX + column * slotSizePlus2, startPlayerInvY + row * slotSizePlus2));
         }
      }

      int hotbarY = 142;

      for (int column = 0; column < 9; column++) {
         this.addSlot(new Slot(inv, column, startX + column * slotSizePlus2, hotbarY));
      }
   }

   public ItemStack quickMoveStack(Player player, int ints) {
      ItemStack retStack = ItemStack.EMPTY;
      Slot slot = this.getSlot(ints);
      if (slot.hasItem()) {
         ItemStack item = slot.getItem();
         retStack = item.copy();
         if (ints < 27) {
            if (!this.moveItemStackTo(item, 27, this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(item, 0, 27, false)) {
            return ItemStack.EMPTY;
         }

         if (item.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }
      }

      return retStack;
   }

   public boolean stillValid(Player player) {
      return stillValid(this.containerAccess, player, (Block)BlockInit.OAK_DRAWER.get());
   }
}
