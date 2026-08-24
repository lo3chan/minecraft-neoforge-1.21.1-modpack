package net.joefoxe.hexerei.container;

import net.joefoxe.hexerei.item.custom.CourierPackageItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class PackageContainer extends ItemStackContainer {
   public static final int OFFSET = 28;
   CourierPackageItem.PackageInvWrapper wrapper;
   private final DataSlot sealed = new DataSlot() {
      private int value;

      public int get() {
         return this.value;
      }

      public void set(int value) {
         this.value = value;
         if (PackageContainer.this.wrapper != null) {
            PackageContainer.this.wrapper.setSealed(value);
         }
      }
   };

   public PackageContainer(int windowId, ItemStack itemStack, Inventory playerInventory, InteractionHand hand, int slotIndex) {
      super((MenuType<?>)ModContainers.PACKAGE_CONTAINER.get(), windowId, playerInventory, itemStack, hand, slotIndex);
      this.addDataSlot(this.sealed);
      if (this.wrapper != null) {
         this.setSealed(this.wrapper.getSealed() ? 1 : 0);
      }
   }

   public PackageContainer(int windowId, Inventory playerInventory, InteractionHand hand, int slotIndex) {
      this(windowId, playerInventory.player.getItemInHand(hand), playerInventory, hand, slotIndex);
   }

   public PackageContainer(int windowId, Inventory playerInventory, RegistryFriendlyByteBuf byteBuf) {
      this(windowId, playerInventory, byteBuf.readByte() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, byteBuf.readByte());
   }

   @Override
   protected void addContainerSlots() {
      if (this.stack.getItem() instanceof CourierPackageItem) {
         this.wrapper = new CourierPackageItem.PackageInvWrapper(this.stack);

         for (int i = 0; i < this.wrapper.getSlots(); i++) {
            this.addSlot(new SlotItemHandler(this.wrapper, i, 26 + 21 * i, -12) {
               public void setChanged() {
                  super.setChanged();
                  PackageContainer.this.wrapper.setStackInSlot(this.getSlotIndex(), this.getItemHandler().getStackInSlot(this.getSlotIndex()));
               }
            });
         }
      }
   }

   public boolean isEmpty() {
      return this.wrapper != null ? this.wrapper.isEmpty() : true;
   }

   public void removed(Player pPlayer) {
      if (!pPlayer.level().isClientSide) {
         this.wrapper.setSealed(this.sealed.get());
      }

      super.removed(pPlayer);
   }

   public int getSealed() {
      return this.sealed.get();
   }

   public void setSealed(int value) {
      this.sealed.set(value);
   }

   public boolean clickMenuButton(Player pPlayer, int pId) {
      this.sealed.set(pId);
      return true;
   }

   @Override
   protected void addPlayerSlots() {
      this.layoutPlayerInventorySlots(11, 31);
   }

   @Override
   public boolean stillValid(Player playerIn) {
      return playerIn.getItemInHand(this.hand).equals(this.stack);
   }
}
