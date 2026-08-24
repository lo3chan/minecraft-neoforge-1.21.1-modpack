package net.nycto_team.overpacked.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.nycto_team.overpacked.entity.GiantBackpack;
import net.nycto_team.overpacked.registry.ModMenus;

public class GiantBackpackMenu extends AbstractContainerMenu {
   private final Container container;
   public final GiantBackpack backpack;
   public final int inv_id;
   public final int rows;

   public GiantBackpackMenu(int id, Inventory inv, FriendlyByteBuf data) {
      this(id, inv, (GiantBackpack)inv.player.level().getEntity(data.readInt()), data.readByte());
   }

   public GiantBackpackMenu(int id, Inventory inv, GiantBackpack backpack, int inv_id) {
      super(ModMenus.giant_backpack.get(), id);
      this.backpack = backpack;
      this.inv_id = inv_id;
      this.container = backpack.inv[inv_id];
      this.rows = inv_id == 0 ? 6 : 3;
      checkContainerSize(this.container, this.rows * 9);
      this.container.startOpen(inv.player);

      for (int i = 0; i < this.rows; i++) {
         for (int ii = 0; ii < 9; ii++) {
            this.addSlot(new ShulkerBoxSlot(this.container, ii + i * 9, 8 + ii * 18, 18 + i * 18));
         }
      }

      int player_inv_offset = inv_id == 0 ? 54 : 0;

      for (int i = 0; i < 3; i++) {
         for (int ii = 0; ii < 9; ii++) {
            this.addSlot(new Slot(inv, ii + i * 9 + 9, 8 + ii * 18, 103 + i * 18 - 18 + player_inv_offset));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(inv, i, 8 + i * 18, 143 + player_inv_offset));
      }

      if (!inv.player.level().isClientSide()) {
         backpack.UpdateAnim(inv_id, 1);
      }
   }

   public boolean stillValid(Player player) {
      return !this.backpack.has_inv_changed(this.inv_id, this.container)
         && this.container.stillValid(player)
         && this.backpack.isAlive()
         && player.canInteractWithEntity(this.backpack, 4.0);
   }

   public ItemStack quickMoveStack(Player player, int id) {
      ItemStack stack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(id);
      if (slot.hasItem()) {
         ItemStack slot_stack = slot.getItem();
         stack = slot_stack.copy();
         if (id < this.rows * 9) {
            if (!this.moveItemStackTo(slot_stack, this.rows * 9, this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(slot_stack, 0, this.rows * 9, false)) {
            return ItemStack.EMPTY;
         }

         if (slot_stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }
      }

      return stack;
   }

   public void removed(Player player) {
      super.removed(player);
      this.container.stopOpen(player);
      if (!player.level().isClientSide()) {
         this.backpack.UpdateAnim(this.inv_id, -1);
      }
   }
}
