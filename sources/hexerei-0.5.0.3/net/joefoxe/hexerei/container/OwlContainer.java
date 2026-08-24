package net.joefoxe.hexerei.container;

import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

public class OwlContainer extends AbstractContainerMenu {
   private final Player playerEntity;
   public final OwlEntity owlEntity;
   private final IItemHandler playerInventory;
   private static final int HOTBAR_SLOT_COUNT = 9;
   private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
   private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
   private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
   private static final int VANILLA_SLOT_COUNT = 36;
   private static final int VANILLA_FIRST_SLOT_INDEX = 0;
   private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 36;
   private static final int TE_INVENTORY_SLOT_COUNT = 2;

   public OwlContainer(int windowId, final OwlEntity owlEntity, Inventory playerInventory, Player player) {
      super((MenuType)ModContainers.OWL_CONTAINER.get(), windowId);
      this.owlEntity = owlEntity;
      this.playerEntity = player;
      this.playerInventory = new InvWrapper(playerInventory);
      this.layoutPlayerInventorySlots(14, 108);
      IItemHandler handler = (IItemHandler)owlEntity.getCapability(ItemHandler.ENTITY);
      this.addSlot(new SlotItemHandler(handler, 0, 110, 50) {
         public int getMaxStackSize() {
            return 1;
         }

         public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.canEquip(EquipmentSlot.HEAD, owlEntity);
         }
      });
      this.addSlot(new SlotItemHandler(handler, 1, 61, 50) {
         public int getMaxStackSize() {
            return 1;
         }

         public boolean mayPlace(@NotNull ItemStack stack) {
            return true;
         }
      });
   }

   public void clicked(int p_150400_, int p_150401_, ClickType p_150402_, Player p_150403_) {
      super.clicked(p_150400_, p_150401_, p_150402_, p_150403_);
   }

   public void playSound() {
      this.owlEntity.level().playSound(null, this.owlEntity.blockPosition(), (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public boolean stillValid(Player playerIn) {
      return this.owlEntity != null && !this.owlEntity.isRemoved() ? !(playerIn.distanceToSqr(this.owlEntity) > 64.0) : false;
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
            if (!this.moveItemStackTo(sourceStack, 36, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (index >= 38) {
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
