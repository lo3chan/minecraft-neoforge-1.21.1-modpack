package net.joefoxe.hexerei.container;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Coffer;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.CofferInvButtonPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class CofferContainer extends AbstractContainerMenu {
   public final BlockEntity tileEntity;
   private final Player playerEntity;
   private final IItemHandler playerInventory;
   public static final int OFFSET = 28;
   public boolean inWorld;
   public InteractionHand hand;
   public ItemStack stack;
   public int slotIndex;
   public DataSlot dataSlot;
   public CofferTile.CofferInvWrapper wrapper;
   private static final int HOTBAR_SLOT_COUNT = 9;
   private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
   private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
   private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
   private static final int VANILLA_SLOT_COUNT = 36;
   private static final int VANILLA_FIRST_SLOT_INDEX = 0;
   private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 36;
   private static final int TE_INVENTORY_SLOT_COUNT = 36;

   public CofferContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
      super((MenuType)ModContainers.COFFER_CONTAINER.get(), windowId);
      this.tileEntity = world.getBlockEntity(pos);
      this.playerEntity = player;
      this.playerInventory = new InvWrapper(playerInventory);
      if (this.tileEntity instanceof Container container) {
         container.startOpen(player);
      }

      this.inWorld = true;
      if (this.tileEntity instanceof CofferTile cofferTile) {
         this.wrapper = new CofferTile.CofferInvWrapper(cofferTile.cofferId, world);
      }

      this.layoutPlayerInventorySlots(11, 119);
      if (this.wrapper != null) {
         this.addSlot(new SlotItemHandler(this.wrapper, 0, 15, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 1, 36, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 2, 57, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 3, 78, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 4, 99, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 5, 120, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 6, 141, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 7, 162, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 8, 183, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 9, 15, 11));
         this.addSlot(new SlotItemHandler(this.wrapper, 10, 36, 11));
         this.addSlot(new SlotItemHandler(this.wrapper, 11, 57, 11));
         this.addSlot(new SlotItemHandler(this.wrapper, 12, 141, 11));
         this.addSlot(new SlotItemHandler(this.wrapper, 13, 162, 11));
         this.addSlot(new SlotItemHandler(this.wrapper, 14, 183, 11));
         this.addSlot(new SlotItemHandler(this.wrapper, 15, 15, 32));
         this.addSlot(new SlotItemHandler(this.wrapper, 16, 36, 32));
         this.addSlot(new SlotItemHandler(this.wrapper, 17, 57, 32));
         this.addSlot(new SlotItemHandler(this.wrapper, 18, 141, 32));
         this.addSlot(new SlotItemHandler(this.wrapper, 19, 162, 32));
         this.addSlot(new SlotItemHandler(this.wrapper, 20, 183, 32));
         this.addSlot(new SlotItemHandler(this.wrapper, 21, 15, 53));
         this.addSlot(new SlotItemHandler(this.wrapper, 22, 36, 53));
         this.addSlot(new SlotItemHandler(this.wrapper, 23, 57, 53));
         this.addSlot(new SlotItemHandler(this.wrapper, 24, 141, 53));
         this.addSlot(new SlotItemHandler(this.wrapper, 25, 162, 53));
         this.addSlot(new SlotItemHandler(this.wrapper, 26, 183, 53));
         this.addSlot(new SlotItemHandler(this.wrapper, 27, 15, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 28, 36, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 29, 57, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 30, 78, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 31, 99, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 32, 120, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 33, 141, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 34, 162, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 35, 183, 74));
      }

      this.dataSlot = this.addDataSlot(new DataSlot() {
         public void set(int value) {
            ((CofferTile)CofferContainer.this.tileEntity).setButtonToggled(value);
         }

         public int get() {
            return ((CofferTile)CofferContainer.this.tileEntity).getButtonToggled();
         }
      });
      this.addDataSlot(this.dataSlot);
   }

   public CofferContainer(int windowId, ItemStack itemStack, Inventory playerInventory, Player player, InteractionHand hand) {
      super((MenuType)ModContainers.COFFER_CONTAINER.get(), windowId);
      this.playerEntity = player;
      this.playerInventory = new InvWrapper(playerInventory);
      this.inWorld = false;
      this.tileEntity = ((Coffer)ModBlocks.COFFER.get())
         .newBlockEntity(
            player.blockPosition(),
            (BlockState)((Coffer)ModBlocks.COFFER.get()).defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH)
         );
      this.slotIndex = hand == InteractionHand.OFF_HAND ? -1 : player.getInventory().selected;
      this.stack = itemStack;
      this.hand = hand;
      this.layoutPlayerInventorySlots(11, 119);
      CompoundTag tag = ((CustomData)this.stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (tag.contains("CofferId")) {
         this.wrapper = new CofferTile.CofferInvWrapper(tag.getUUID("CofferId"), player.level());
      }

      if (this.wrapper != null) {
         this.addSlot(new SlotItemHandler(this.wrapper, 0, 15, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 1, 36, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 2, 57, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 3, 78, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 4, 99, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 5, 120, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 6, 141, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 7, 162, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 8, 183, -10));
         this.addSlot(new SlotItemHandler(this.wrapper, 9, 15, 11));
         this.addSlot(new SlotItemHandler(this.wrapper, 10, 36, 11));
         this.addSlot(new SlotItemHandler(this.wrapper, 11, 57, 11));
         this.addSlot(new SlotItemHandler(this.wrapper, 12, 141, 11));
         this.addSlot(new SlotItemHandler(this.wrapper, 13, 162, 11));
         this.addSlot(new SlotItemHandler(this.wrapper, 14, 183, 11));
         this.addSlot(new SlotItemHandler(this.wrapper, 15, 15, 32));
         this.addSlot(new SlotItemHandler(this.wrapper, 16, 36, 32));
         this.addSlot(new SlotItemHandler(this.wrapper, 17, 57, 32));
         this.addSlot(new SlotItemHandler(this.wrapper, 18, 141, 32));
         this.addSlot(new SlotItemHandler(this.wrapper, 19, 162, 32));
         this.addSlot(new SlotItemHandler(this.wrapper, 20, 183, 32));
         this.addSlot(new SlotItemHandler(this.wrapper, 21, 15, 53));
         this.addSlot(new SlotItemHandler(this.wrapper, 22, 36, 53));
         this.addSlot(new SlotItemHandler(this.wrapper, 23, 57, 53));
         this.addSlot(new SlotItemHandler(this.wrapper, 24, 141, 53));
         this.addSlot(new SlotItemHandler(this.wrapper, 25, 162, 53));
         this.addSlot(new SlotItemHandler(this.wrapper, 26, 183, 53));
         this.addSlot(new SlotItemHandler(this.wrapper, 27, 15, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 28, 36, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 29, 57, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 30, 78, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 31, 99, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 32, 120, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 33, 141, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 34, 162, 74));
         this.addSlot(new SlotItemHandler(this.wrapper, 35, 183, 74));
      }

      this.dataSlot = this.addDataSlot(new DataSlot() {
         public void set(int value) {
            HexereiPacketHandler.sendToServer(new CofferInvButtonPacket(CofferContainer.this.slotIndex, value));
            CompoundTag tagx = ((CustomData)CofferContainer.this.stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
            tagx.putInt("ButtonToggled", value);
            CofferContainer.this.stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tagx));
         }

         public int get() {
            CompoundTag tagx = ((CustomData)CofferContainer.this.stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
            return tagx.contains("ButtonToggled") ? tagx.getInt("ButtonToggled") : 0;
         }
      });
      this.addDataSlot(this.dataSlot);
   }

   public void broadcastChanges() {
      super.broadcastChanges();
   }

   public void slotsChanged(Container pContainer) {
      super.slotsChanged(pContainer);
   }

   public void playSound() {
      this.tileEntity
         .getLevel()
         .playSound(null, this.tileEntity.getBlockPos(), (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public int getToggled() {
      return this.dataSlot.get();
   }

   public void setToggled(int value) {
      this.dataSlot.set(value);
   }

   public boolean stillValid(Player playerIn) {
      return this.inWorld
         ? stillValid(
            ContainerLevelAccess.create(this.tileEntity.getLevel(), this.tileEntity.getBlockPos()), playerIn, this.tileEntity.getBlockState().getBlock()
         )
         : playerIn.getItemInHand(this.hand).equals(this.stack);
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
      if (sourceSlot != null && sourceSlot.hasItem() && this.wrapper != null) {
         ItemStack sourceStack = sourceSlot.getItem();
         ItemStack copyOfSourceStack = sourceStack.copy();
         if (index < 36) {
            if (!this.moveItemStackTo(sourceStack, 36, 36 + this.wrapper.getSlots(), false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (index >= 36 + this.wrapper.getSlots()) {
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
