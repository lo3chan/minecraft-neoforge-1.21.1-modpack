package net.joefoxe.hexerei.container;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

public class BroomContainer extends AbstractContainerMenu implements HasCustomInventoryScreen {
   private final Player playerEntity;
   public final BroomEntity broomEntity;
   private final IItemHandler playerInventory;
   private final IItemHandler playerEnderInventory;
   public static final int OFFSET = 34;
   public boolean isEnder;
   public ItemStack satchel;
   private static final int HOTBAR_SLOT_COUNT = 9;
   private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
   private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
   private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
   private static final int VANILLA_SLOT_COUNT = 36;
   private static final int VANILLA_FIRST_SLOT_INDEX = 0;
   private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 36;
   private static final int TE_INVENTORY_SLOT_COUNT = 3;

   public BroomContainer(int windowId, final BroomEntity broomEntity, Inventory playerInventory, Player player, boolean isEnder) {
      super((MenuType)ModContainers.BROOM_CONTAINER.get(), windowId);
      this.broomEntity = broomEntity;
      broomEntity.startOpen(player);
      this.playerEntity = player;
      this.playerInventory = new InvWrapper(playerInventory);
      this.playerEnderInventory = new InvWrapper(player.getEnderChestInventory());
      this.isEnder = isEnder;
      this.satchel = broomEntity.itemHandler.getStackInSlot(1);
      int offset = 0;
      broomEntity.sync();
      if (broomEntity.itemHandler.getStackInSlot(1).is(HexereiTags.Items.SMALL_SATCHELS)) {
         offset = 21;
      }

      if (broomEntity.itemHandler.getStackInSlot(1).is(HexereiTags.Items.MEDIUM_SATCHELS)) {
         offset = 42;
      }

      if (broomEntity.itemHandler.getStackInSlot(1).is(HexereiTags.Items.LARGE_SATCHELS)) {
         offset = 63;
      }

      this.layoutPlayerInventorySlots(11, 106 + offset - 34);
      this.addSlot(new SlotItemHandler(broomEntity.itemHandler, 0, 37, 13) {
         public int getMaxStackSize() {
            return 1;
         }

         public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.is(HexereiTags.Items.BROOM_MISC);
         }
      });
      this.addSlot(new SlotItemHandler(broomEntity.itemHandler, 1, 99, 13) {
         public int getMaxStackSize() {
            return 1;
         }

         public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.is(HexereiTags.Items.SMALL_SATCHELS) || stack.is(HexereiTags.Items.MEDIUM_SATCHELS) || stack.is(HexereiTags.Items.LARGE_SATCHELS);
         }

         public boolean mayPickup(Player playerIn) {
            if (broomEntity.isEnder()) {
               return true;
            } else {
               ItemStack satchel = broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL);
               if (satchel.is(HexereiTags.Items.SMALL_SATCHELS)) {
                  return broomEntity.getSatchelSlots(9).stream().allMatch(ItemStack::isEmpty);
               } else if (satchel.is(HexereiTags.Items.MEDIUM_SATCHELS)) {
                  return broomEntity.getSatchelSlots(18).stream().allMatch(ItemStack::isEmpty);
               } else {
                  return satchel.is(HexereiTags.Items.LARGE_SATCHELS) ? broomEntity.getSatchelSlots(27).stream().allMatch(ItemStack::isEmpty) : true;
               }
            }
         }
      });
      this.addSlot(new SlotItemHandler(broomEntity.itemHandler, 2, 160, 13) {
         public int getMaxStackSize() {
            return 1;
         }

         public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.is(HexereiTags.Items.BROOM_BRUSH);
         }
      });

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlot(new SlotItemHandler(broomEntity.itemHandler, 3 + i * 9 + j, 15 + 21 * j, 21 * i + 82 - 34) {
               public boolean mayPlace(@NotNull ItemStack stack) {
                  return !stack.is((Item)ModItems.WILLOW_BROOM.get()) && !stack.is((Item)ModItems.MAHOGANY_BROOM.get());
               }
            });
         }
      }

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlot(new SlotItemHandler(this.playerEnderInventory, i * 9 + j, 15 + 21 * j, 21 * i + 82 - 34) {
               public boolean mayPlace(@NotNull ItemStack stack) {
                  return true;
               }
            });
         }
      }

      int offset2 = 0;
      if (!broomEntity.isEnder()) {
         if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.SMALL_SATCHELS)) {
            offset2 = 21;

            for (int i = 0; i < 1; i++) {
               for (int j = 0; j < 9; j++) {
                  ((Slot)this.slots.get(39 + i * 9 + j)).y = 21 * i + 82 - 34;
               }
            }

            for (int i = 1; i < 3; i++) {
               for (int j = 0; j < 9; j++) {
                  ((Slot)this.slots.get(39 + i * 9 + j)).y = -999;
               }
            }
         }

         if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.MEDIUM_SATCHELS)) {
            offset2 = 42;

            for (int i = 0; i < 2; i++) {
               for (int j = 0; j < 9; j++) {
                  ((Slot)this.slots.get(39 + i * 9 + j)).y = 21 * i + 82 - 34;
               }
            }

            for (int i = 2; i < 3; i++) {
               for (int j = 0; j < 9; j++) {
                  ((Slot)this.slots.get(39 + i * 9 + j)).y = -999;
               }
            }
         }

         if (broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.LARGE_SATCHELS)) {
            offset2 = 63;

            for (int i = 0; i < 3; i++) {
               for (int j = 0; j < 9; j++) {
                  ((Slot)this.slots.get(39 + i * 9 + j)).y = 21 * i + 82 - 34;
               }
            }
         }

         for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
               ((Slot)this.slots.get(39 + i * 9 + j + 27)).y = -999;
            }
         }
      } else {
         offset2 = 63;

         for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
               ((Slot)this.slots.get(39 + i * 9 + j + 27)).y = 21 * i + 82 - 34;
            }
         }

         for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
               ((Slot)this.slots.get(39 + i * 9 + j)).y = -999;
            }
         }
      }

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            ((Slot)this.slots.get(i * 9 + j)).y = 106 + i * 18 + offset2 - 34;
         }
      }

      for (int k = 0; k < 9; k++) {
         ((Slot)this.slots.get(27 + k)).y = 164 + offset2 - 34;
      }

      if (offset2 == 0) {
         for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
               ((Slot)this.slots.get(39 + i * 9 + j)).y = -999;
            }
         }
      }

      this.addDataSlot(new DataSlot() {
         public void set(int value) {
            broomEntity.setFloatMode(value != 0);
         }

         public int get() {
            return broomEntity.getFloatMode() ? 1 : 0;
         }
      });
   }

   public void removed(Player pPlayer) {
      if (pPlayer instanceof ServerPlayer) {
         ItemStack itemstack = this.getCarried();
         if (!itemstack.isEmpty()) {
            if (pPlayer.isAlive() && !((ServerPlayer)pPlayer).hasDisconnected()) {
               pPlayer.getInventory().placeItemBackInInventory(itemstack);
            } else {
               pPlayer.drop(itemstack, false);
            }

            this.setCarried(ItemStack.EMPTY);
         }
      }

      this.broomEntity.stopOpen(pPlayer);
   }

   public void clicked(int p_150400_, int p_150401_, ClickType p_150402_, Player p_150403_) {
      super.clicked(p_150400_, p_150401_, p_150402_, p_150403_);
      int offset = 0;
      if (this.satchel != this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL)) {
         if (!this.satchel.isEmpty()) {
            this.playSoundClose(this.isEnder);
         } else {
            this.playSoundOpen(this.broomEntity.isEnder());
         }

         this.satchel = this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL);
         this.isEnder = this.broomEntity.isEnder();
      }

      if (!this.broomEntity.isEnder()) {
         if (this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.SMALL_SATCHELS)) {
            offset = 21;

            for (int i = 0; i < 1; i++) {
               for (int j = 0; j < 9; j++) {
                  ((Slot)this.slots.get(39 + i * 9 + j)).y = 21 * i + 82 - 34;
               }
            }

            for (int i = 1; i < 3; i++) {
               for (int j = 0; j < 9; j++) {
                  ((Slot)this.slots.get(39 + i * 9 + j)).y = -999;
               }
            }
         }

         if (this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.MEDIUM_SATCHELS)) {
            offset = 42;

            for (int i = 0; i < 2; i++) {
               for (int j = 0; j < 9; j++) {
                  ((Slot)this.slots.get(39 + i * 9 + j)).y = 21 * i + 82 - 34;
               }
            }

            for (int i = 2; i < 3; i++) {
               for (int j = 0; j < 9; j++) {
                  ((Slot)this.slots.get(39 + i * 9 + j)).y = -999;
               }
            }
         }

         if (this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.LARGE_SATCHELS)) {
            offset = 63;

            for (int i = 0; i < 3; i++) {
               for (int j = 0; j < 9; j++) {
                  ((Slot)this.slots.get(39 + i * 9 + j)).y = 21 * i + 82 - 34;
               }
            }
         }

         for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
               ((Slot)this.slots.get(39 + i * 9 + j + 27)).y = -999;
            }
         }
      } else {
         offset = 63;

         for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
               ((Slot)this.slots.get(39 + i * 9 + j + 27)).y = 21 * i + 82 - 34;
            }
         }

         for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
               ((Slot)this.slots.get(39 + i * 9 + j)).y = -999;
            }
         }
      }

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            ((Slot)this.slots.get(i * 9 + j)).y = 106 + i * 18 + offset - 34;
         }
      }

      for (int k = 0; k < 9; k++) {
         ((Slot)this.slots.get(27 + k)).y = 164 + offset - 34;
      }

      if (offset == 0) {
         for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
               ((Slot)this.slots.get(39 + i * 9 + j)).y = -999;
            }
         }
      }
   }

   public void playSound() {
      this.broomEntity
         .level()
         .playSound(null, this.broomEntity.blockPosition(), (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public void playSoundOpen(boolean isEnder) {
      SoundEvent sound = (SoundEvent)SoundEvents.ARMOR_EQUIP_LEATHER.value();
      float volume = 0.75F;
      if (isEnder) {
         sound = SoundEvents.ENDER_CHEST_OPEN;
         volume = 0.5F;
      }

      this.broomEntity
         .level()
         .playSound(
            null,
            this.broomEntity.getX(),
            this.broomEntity.getY() + 0.5,
            this.broomEntity.getZ(),
            sound,
            SoundSource.BLOCKS,
            volume,
            this.broomEntity.level().random.nextFloat() * 0.1F + 0.9F
         );
   }

   public void playSoundClose(boolean isEnder) {
      SoundEvent sound = (SoundEvent)SoundEvents.ARMOR_EQUIP_LEATHER.value();
      float pitch = 0.4F;
      float volume = 0.75F;
      if (isEnder) {
         sound = SoundEvents.ENDER_CHEST_CLOSE;
         pitch = 0.9F;
         volume = 0.5F;
      }

      this.broomEntity
         .level()
         .playSound(
            null,
            this.broomEntity.getX(),
            this.broomEntity.getY() + 0.5,
            this.broomEntity.getZ(),
            sound,
            SoundSource.BLOCKS,
            volume,
            this.broomEntity.level().random.nextFloat() * 0.1F + pitch
         );
   }

   public void playSound(SoundEvent event) {
      this.broomEntity.level().playSound(null, this.broomEntity.blockPosition(), event, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public boolean getFloatMode() {
      return this.broomEntity.getFloatMode();
   }

   public void setFloatMode(boolean value) {
      this.broomEntity.setFloatMode(value);
   }

   public boolean stillValid(Player playerIn) {
      return this.broomEntity.isRemoved() ? false : !(playerIn.distanceToSqr(this.broomEntity) > 64.0);
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
      if (!sourceSlot.hasItem()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack sourceStack = sourceSlot.getItem();
         ItemStack copyOfSourceStack = sourceStack.copy();
         int count = 0;
         if (this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.SMALL_SATCHELS)) {
            count = 9;
         }

         if (this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.MEDIUM_SATCHELS)) {
            count = 18;
         }

         if (this.broomEntity.getModule(BroomEntity.BroomSlot.SATCHEL).is(HexereiTags.Items.LARGE_SATCHELS)) {
            count = 27;
         }

         if (this.broomEntity.isEnder()) {
            count += 27;
         }

         if (index < 36) {
            if (!this.moveItemStackTo(sourceStack, 36 + (this.broomEntity.isEnder() ? 30 : 0), 39 + count, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (index >= 39 + count) {
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
      }
   }

   public void openCustomInventoryScreen(Player pPlayer) {
   }
}
