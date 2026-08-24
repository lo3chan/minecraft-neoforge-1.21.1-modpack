package net.joefoxe.hexerei.container;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.tileentity.PestleAndMortarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class PestleAndMortarContainer extends AbstractContainerMenu {
   private final BlockEntity tileEntity;
   private final Player playerEntity;
   private final IItemHandler playerInventory;
   private static final int HOTBAR_SLOT_COUNT = 9;
   private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
   private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
   private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
   private static final int VANILLA_SLOT_COUNT = 36;
   private static final int VANILLA_FIRST_SLOT_INDEX = 0;
   private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 36;
   private static final int TE_INVENTORY_SLOT_COUNT = 4;

   public PestleAndMortarContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
      super((MenuType)ModContainers.PESTLE_AND_MORTAR_CONTAINER.get(), windowId);
      this.tileEntity = world.getBlockEntity(pos);
      this.playerEntity = playerInventory.player;
      this.playerInventory = new InvWrapper(playerInventory);
      this.layoutPlayerInventorySlots(8, 86);
      if (this.tileEntity instanceof PestleAndMortarTile tile) {
         IItemHandler handler = new InvWrapper(tile);
         this.addSlot(new SlotItemHandler(handler, 0, 58, 14));
         this.addSlot(new SlotItemHandler(handler, 1, 58, 14));
         this.addSlot(new SlotItemHandler(handler, 2, 58, 14));
         this.addSlot(new SlotItemHandler(handler, 3, 58, 14));
         this.addSlot(new SlotItemHandler(handler, 4, 58, 14));
         this.addSlot(new SlotItemHandler(handler, 5, 58, 14));
      }
   }

   public PestleAndMortarContainer(int windowId, Inventory playerInventory, RegistryFriendlyByteBuf byteBuf) {
      this(windowId, playerInventory.player.level(), byteBuf.readBlockPos(), playerInventory, playerInventory.player);
   }

   public boolean stillValid(Player playerIn) {
      return stillValid(
         ContainerLevelAccess.create(this.tileEntity.getLevel(), this.tileEntity.getBlockPos()), playerIn, (Block)ModBlocks.PESTLE_AND_MORTAR.get()
      );
   }

   private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
      for (int i = 0; i < amount; i++) {
         this.addSlot(new SlotItemHandler(handler, index, x, y));
         x += dx;
         index++;
      }

      return index;
   }

   private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
      for (int j = 0; j < verAmount; j++) {
         index = this.addSlotRange(handler, index, x, y, horAmount, dx);
         y += dy;
      }

      return index;
   }

   private void layoutPlayerInventorySlots(int leftCol, int topRow) {
      this.addSlotBox(this.playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
      topRow += 58;
      this.addSlotRange(this.playerInventory, 0, leftCol, topRow, 9, 18);
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      Slot sourceSlot = (Slot)this.slots.get(index);
      if (sourceSlot != null && sourceSlot.hasItem()) {
         ItemStack sourceStack = sourceSlot.getItem();
         ItemStack copyOfSourceStack = sourceStack.copy();
         if (index < 36) {
            if (!this.moveItemStackTo(sourceStack, 36, 40, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (index >= 40) {
               System.out.println("Invalid slotIndex:" + index);
               return ItemStack.EMPTY;
            }

            if (!this.moveItemStackTo(sourceStack, 0, 36, false)) {
               return ItemStack.EMPTY;
            }
         }

         if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
         } else {
            sourceSlot.setChanged();
         }

         sourceSlot.onTake(this.playerEntity, sourceStack);
         return copyOfSourceStack;
      } else {
         return ItemStack.EMPTY;
      }
   }
}
