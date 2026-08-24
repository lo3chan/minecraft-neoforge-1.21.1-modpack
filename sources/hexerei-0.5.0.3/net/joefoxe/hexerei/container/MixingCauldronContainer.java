package net.joefoxe.hexerei.container;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class MixingCauldronContainer extends AbstractContainerMenu {
   public final BlockEntity tileEntity;
   private final Player playerEntity;
   private final IItemHandler playerInventory;
   private FluidStack fluid;
   private static final int HOTBAR_SLOT_COUNT = 9;
   private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
   private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
   private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
   private static final int VANILLA_SLOT_COUNT = 36;
   private static final int VANILLA_FIRST_SLOT_INDEX = 0;
   private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 36;
   private static final int TE_INVENTORY_SLOT_COUNT = 10;

   public MixingCauldronContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
      super((MenuType)ModContainers.MIXING_CAULDRON_CONTAINER.get(), windowId);
      this.tileEntity = world.getBlockEntity(pos);
      this.playerEntity = player;
      this.playerInventory = new InvWrapper(playerInventory);
      this.layoutPlayerInventorySlots(14, 120);
      if (this.tileEntity instanceof MixingCauldronTile cauldron) {
         IItemHandler handler = new InvWrapper(cauldron);
         this.addSlot(new SlotItemHandler(handler, 9, 42, 24));
         this.addSlot(new SlotItemHandler(handler, 0, 105, 17));
         this.addSlot(new SlotItemHandler(handler, 1, 127, 26));
         this.addSlot(new SlotItemHandler(handler, 2, 136, 48));
         this.addSlot(new SlotItemHandler(handler, 3, 127, 70));
         this.addSlot(new SlotItemHandler(handler, 4, 105, 79));
         this.addSlot(new SlotItemHandler(handler, 5, 83, 70));
         this.addSlot(new SlotItemHandler(handler, 6, 74, 48));
         this.addSlot(new SlotItemHandler(handler, 7, 83, 26));
         this.addSlot(new SlotItemHandler(handler, 8, 178, 48));
      }

      if (this.tileEntity instanceof MixingCauldronTile mixingTile) {
         this.fluid = mixingTile.getFluidStack();
      }

      this.addDataSlot(new DataSlot() {
         public void set(int value) {
            ((MixingCauldronTile)MixingCauldronContainer.this.tileEntity).setCraftDelay(value);
         }

         public int get() {
            return ((MixingCauldronTile)MixingCauldronContainer.this.tileEntity).getCraftDelay();
         }
      });
   }

   public void setFluid(FluidStack fluidStack) {
      this.fluid = fluidStack;
   }

   public FluidStack getFluid() {
      return this.tileEntity instanceof MixingCauldronTile mixingTile ? mixingTile.getFluidStack() : this.fluid;
   }

   public FluidStack getRenderedFluid() {
      return this.tileEntity instanceof MixingCauldronTile mixingTile ? mixingTile.renderedFluid : this.fluid;
   }

   public float getCraftPercent() {
      return this.tileEntity instanceof MixingCauldronTile cauldronTile && !cauldronTile.getCrafted() ? cauldronTile.craftDelay / 100.0F : 0.0F;
   }

   public float getCraftPercentHalf() {
      if (this.tileEntity instanceof MixingCauldronTile cauldronTile) {
         float delayHalf = cauldronTile.craftDelay - 50.0F;
         if (!cauldronTile.getCrafted()) {
            return delayHalf / 100.0F;
         }
      }

      return 0.0F;
   }

   public boolean stillValid(Player playerIn) {
      return stillValid(
         ContainerLevelAccess.create(this.tileEntity.getLevel(), this.tileEntity.getBlockPos()), playerIn, (Block)ModBlocks.MIXING_CAULDRON.get()
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
            if (!this.moveItemStackTo(sourceStack, 36, 46, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (index >= 46) {
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
