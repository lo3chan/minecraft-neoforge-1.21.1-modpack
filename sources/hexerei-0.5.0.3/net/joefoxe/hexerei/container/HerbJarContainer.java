package net.joefoxe.hexerei.container;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.items.JarSlot;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class HerbJarContainer extends AbstractContainerMenu {
   private final BlockEntity tileEntity;
   private final Player playerEntity;
   private final IItemHandler playerInventory;
   public final ItemStack stack;
   public static final int OFFSET = 28;
   private static final int HOTBAR_SLOT_COUNT = 9;
   private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
   private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
   private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
   private static final int VANILLA_SLOT_COUNT = 36;
   private static final int VANILLA_FIRST_SLOT_INDEX = 0;
   private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 36;
   private static final int TE_INVENTORY_SLOT_COUNT = 1;

   public HerbJarContainer(int windowId, Inventory inventory, RegistryFriendlyByteBuf byteBuf) {
      this(
         windowId,
         ItemStack.parseOptional(inventory.player.level().registryAccess(), byteBuf.readNbt()),
         inventory.player.level(),
         byteBuf.readBlockPos(),
         inventory,
         inventory.player
      );
   }

   public HerbJarContainer(int windowId, ItemStack itemStack, Level world, BlockPos pos, Inventory playerInventory, Player player) {
      super((MenuType)ModContainers.HERB_JAR_CONTAINER.get(), windowId);
      this.stack = itemStack;
      this.tileEntity = world.getBlockEntity(pos);
      this.playerEntity = player;
      this.playerInventory = new InvWrapper(playerInventory);
      this.layoutPlayerInventorySlots(11, 119);
      if (this.tileEntity instanceof HerbJarTile herbJarTile) {
         this.addSlot(new JarSlot(herbJarTile.itemHandler, 0, 83, 46));
         this.addDataSlot(new DataSlot() {
            public void set(int value) {
               ((HerbJarTile)HerbJarContainer.this.tileEntity).setButtonToggled(value);
            }

            public int get() {
               return ((HerbJarTile)HerbJarContainer.this.tileEntity).getButtonToggled();
            }
         });
      }
   }

   public int getToggled() {
      return ((HerbJarTile)this.tileEntity).getButtonToggled();
   }

   public void setToggled(int value) {
      ((HerbJarTile)this.tileEntity).setButtonToggled(value);
   }

   public static boolean canAddItemToSlot(@Nullable Slot slot, @Nonnull ItemStack stack, boolean stackSizeMatters) {
      boolean flag = slot == null || !slot.hasItem();
      if (slot != null) {
         ItemStack slotStack = slot.getItem();
         if (!flag && ItemStack.isSameItemSameComponents(slotStack, stack)) {
            return slotStack.getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= slot.getMaxStackSize(slotStack);
         }
      }

      return flag;
   }

   public void clicked(int p_150400_, int p_150401_, ClickType p_150402_, Player p_150403_) {
      this.doClick(p_150400_, p_150401_, p_150402_, p_150403_);
      this.tileEntity.setChanged();
   }

   private void doClick(int p_150431_, int p_150432_, ClickType p_150433_, Player p_150434_) {
      if (p_150433_ == ClickType.QUICK_CRAFT) {
         int i = this.quickcraftStatus;
         this.quickcraftStatus = getQuickcraftHeader(p_150432_);
         if ((i != 1 || this.quickcraftStatus != 2) && i != this.quickcraftStatus) {
            this.resetQuickCraft();
         } else if (this.getCarried().isEmpty()) {
            this.resetQuickCraft();
         } else if (this.quickcraftStatus == 0) {
            this.quickcraftType = getQuickcraftType(p_150432_);
            if (isValidQuickcraftType(this.quickcraftType, p_150434_)) {
               this.quickcraftStatus = 1;
               this.quickcraftSlots.clear();
            } else {
               this.resetQuickCraft();
            }
         } else if (this.quickcraftStatus == 1) {
            Slot slot = (Slot)this.slots.get(p_150431_);
            ItemStack itemstack = this.getCarried();
            if (canItemQuickReplace(slot, itemstack, true)
               && slot.mayPlace(itemstack)
               && (this.quickcraftType == 2 || itemstack.getCount() > this.quickcraftSlots.size())
               && this.canDragTo(slot)) {
               this.quickcraftSlots.add(slot);
            }
         } else if (this.quickcraftStatus == 2) {
            if (!this.quickcraftSlots.isEmpty()) {
               if (this.quickcraftSlots.size() == 1) {
                  int l = ((Slot)this.quickcraftSlots.iterator().next()).index;
                  this.resetQuickCraft();
                  this.doClick(l, this.quickcraftType, ClickType.PICKUP, p_150434_);
                  return;
               }

               ItemStack itemstack3 = this.getCarried().copy();
               int j1 = this.getCarried().getCount();

               for (Slot slot1 : this.quickcraftSlots) {
                  ItemStack itemstack1 = this.getCarried();
                  if (slot1 != null
                     && canItemQuickReplace(slot1, itemstack1, true)
                     && slot1.mayPlace(itemstack1)
                     && (this.quickcraftType == 2 || itemstack1.getCount() >= this.quickcraftSlots.size())
                     && this.canDragTo(slot1)) {
                     int j = slot1.hasItem() ? slot1.getItem().getCount() : 0;
                     int k = Math.min(itemstack3.getMaxStackSize(), slot1.getMaxStackSize(itemstack3));
                     int l = Math.min(getQuickCraftPlaceCount(this.quickcraftSlots, this.quickcraftType, itemstack3) + j, k);
                     j1 -= l - j;
                     slot1.setByPlayer(itemstack3.copyWithCount(l));
                  }
               }

               itemstack3.setCount(j1);
               this.setCarried(itemstack3);
            }

            this.resetQuickCraft();
         } else {
            this.resetQuickCraft();
         }
      } else if (this.quickcraftStatus != 0) {
         this.resetQuickCraft();
      } else if ((p_150433_ == ClickType.PICKUP || p_150433_ == ClickType.QUICK_MOVE) && (p_150432_ == 0 || p_150432_ == 1)) {
         ClickAction clickaction = p_150432_ == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
         if (p_150431_ == -999) {
            if (!this.getCarried().isEmpty()) {
               if (clickaction == ClickAction.PRIMARY) {
                  p_150434_.drop(this.getCarried(), true);
                  this.setCarried(ItemStack.EMPTY);
               } else {
                  p_150434_.drop(this.getCarried().split(1), true);
               }
            }
         } else if (p_150433_ == ClickType.QUICK_MOVE) {
            if (p_150431_ < 0) {
               return;
            }

            Slot slot6 = (Slot)this.slots.get(p_150431_);
            if (!slot6.mayPickup(p_150434_)) {
               return;
            }

            ItemStack itemstack9 = this.quickMoveStack(p_150434_, p_150431_);

            while (!itemstack9.isEmpty() && ItemStack.isSameItem(slot6.getItem(), itemstack9)) {
               itemstack9 = this.quickMoveStack(p_150434_, p_150431_);
            }
         } else {
            if (p_150431_ < 0) {
               return;
            }

            Slot slot7 = (Slot)this.slots.get(p_150431_);
            ItemStack itemstack10 = slot7.getItem();
            ItemStack itemstack11 = this.getCarried();
            p_150434_.updateTutorialInventoryAction(itemstack11, slot7.getItem(), clickaction);
            if (!itemstack11.overrideStackedOnOther(slot7, clickaction, p_150434_)
               && !itemstack10.overrideOtherStackedOnMe(itemstack11, slot7, clickaction, p_150434_, this.createCarriedSlotAccess())) {
               if (itemstack10.isEmpty()) {
                  if (!itemstack11.isEmpty()) {
                     int l2 = clickaction == ClickAction.PRIMARY ? itemstack11.getCount() : 1;
                     this.setCarried(slot7.safeInsert(itemstack11, l2));
                  }
               } else if (slot7.mayPickup(p_150434_)) {
                  if (itemstack11.isEmpty()) {
                     int toMove;
                     if (slot7 instanceof JarSlot) {
                        if (itemstack10.getMaxStackSize() < itemstack10.getCount()) {
                           toMove = p_150432_ == 0 ? itemstack10.getMaxStackSize() : (itemstack10.getMaxStackSize() + 1) / 2;
                        } else {
                           toMove = p_150432_ == 0 ? itemstack10.getCount() : (itemstack10.getCount() + 1) / 2;
                        }
                     } else {
                        toMove = p_150432_ == 0 ? itemstack10.getCount() : (itemstack10.getCount() + 1) / 2;
                     }

                     this.setCarried(slot7.remove(toMove));
                     if (itemstack10.isEmpty()) {
                        slot7.set(ItemStack.EMPTY);
                     }

                     slot7.onTake(p_150434_, this.getCarried());
                  } else if (slot7.mayPlace(itemstack11)) {
                     if (ItemStack.isSameItemSameComponents(itemstack10, itemstack11)) {
                        int j3 = clickaction == ClickAction.PRIMARY ? itemstack11.getCount() : 1;
                        this.setCarried(slot7.safeInsert(itemstack11, j3));
                     } else if (itemstack11.getCount() <= slot7.getMaxStackSize(itemstack11)) {
                        slot7.set(itemstack11);
                        this.setCarried(itemstack10);
                     }
                  } else if (ItemStack.isSameItemSameComponents(itemstack10, itemstack11)) {
                     Optional<ItemStack> optional = slot7.tryRemove(itemstack10.getCount(), itemstack11.getMaxStackSize() - itemstack11.getCount(), p_150434_);
                     optional.ifPresent(p_150428_ -> {
                        itemstack11.grow(p_150428_.getCount());
                        slot7.onTake(p_150434_, p_150428_);
                     });
                  }
               }
            }

            slot7.setChanged();
         }
      } else if (p_150433_ == ClickType.CLONE && p_150434_.getAbilities().instabuild && this.getCarried().isEmpty() && p_150431_ >= 0) {
         Slot slot5 = (Slot)this.slots.get(p_150431_);
         if (slot5.hasItem()) {
            ItemStack itemstack6 = slot5.getItem().copy();
            itemstack6.setCount(itemstack6.getMaxStackSize());
            this.setCarried(itemstack6);
         }
      } else if (p_150433_ == ClickType.THROW && this.getCarried().isEmpty() && p_150431_ >= 0) {
         Slot slot4 = (Slot)this.slots.get(p_150431_);
         int i1 = p_150432_ == 0 ? 1 : slot4.getItem().getCount();
         ItemStack itemstack8 = slot4.safeTake(i1, 2147483647, p_150434_);
         p_150434_.drop(itemstack8, true);
      } else if (p_150433_ == ClickType.PICKUP_ALL && p_150431_ >= 0) {
         Slot slot3 = (Slot)this.slots.get(p_150431_);
         ItemStack itemstack5 = this.getCarried();
         if (!itemstack5.isEmpty() && (!slot3.hasItem() || !slot3.mayPickup(p_150434_))) {
            int k1 = p_150432_ == 0 ? 0 : this.slots.size() - 1;
            int j2 = p_150432_ == 0 ? 1 : -1;

            for (int k2 = 0; k2 < 2; k2++) {
               for (int k3 = k1; k3 >= 0 && k3 < this.slots.size() && itemstack5.getCount() < itemstack5.getMaxStackSize(); k3 += j2) {
                  Slot slot8 = (Slot)this.slots.get(k3);
                  if (slot8.hasItem()
                     && canItemQuickReplace(slot8, itemstack5, true)
                     && slot8.mayPickup(p_150434_)
                     && this.canTakeItemForPickAll(itemstack5, slot8)) {
                     ItemStack itemstack12 = slot8.getItem();
                     if (k2 != 0 || itemstack12.getCount() != itemstack12.getMaxStackSize()) {
                        ItemStack itemstack13 = slot8.safeTake(itemstack12.getCount(), itemstack5.getMaxStackSize() - itemstack5.getCount(), p_150434_);
                        itemstack5.grow(itemstack13.getCount());
                     }
                  }
               }
            }
         }
      }
   }

   private SlotAccess createCarriedSlotAccess() {
      return new SlotAccess() {
         public ItemStack get() {
            return HerbJarContainer.this.getCarried();
         }

         public boolean set(ItemStack p_150452_) {
            HerbJarContainer.this.setCarried(p_150452_);
            return true;
         }
      };
   }

   protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
      boolean flag = false;
      int i = startIndex;
      if (reverseDirection) {
         i = endIndex - 1;
      }

      for (; !stack.isEmpty() && (reverseDirection ? i >= startIndex : i < endIndex); i += reverseDirection ? -1 : 1) {
         Slot slot = (Slot)this.slots.get(i);
         ItemStack itemstack = slot.getItem();
         if (!itemstack.isEmpty() && itemstack.getItem() == stack.getItem() && ItemStack.isSameItemSameComponents(stack, itemstack)) {
            int j = itemstack.getCount() + stack.getCount();
            int maxSize = slot.getMaxStackSize(itemstack);
            if (j <= maxSize) {
               stack.setCount(0);
               itemstack.setCount(j);
               slot.setChanged();
               flag = true;
            } else if (itemstack.getCount() < maxSize) {
               stack.shrink(maxSize - itemstack.getCount());
               itemstack.setCount(maxSize);
               slot.setChanged();
               flag = true;
            }
         }
      }

      if (!stack.isEmpty()) {
         if (reverseDirection) {
            i = endIndex - 1;
         } else {
            i = startIndex;
         }

         while (reverseDirection ? i >= startIndex : i < endIndex) {
            Slot slot1 = (Slot)this.slots.get(i);
            ItemStack itemstack1 = slot1.getItem();
            if (itemstack1.isEmpty() && slot1.mayPlace(stack)) {
               if (stack.getCount() > slot1.getMaxStackSize(stack)) {
                  slot1.set(stack.split(slot1.getMaxStackSize(stack)));
               } else {
                  slot1.set(stack.split(stack.getCount()));
               }

               slot1.setChanged();
               flag = true;
               break;
            }

            i += reverseDirection ? -1 : 1;
         }
      }

      return flag;
   }

   public boolean stillValid(Player playerIn) {
      return stillValid(ContainerLevelAccess.create(this.tileEntity.getLevel(), this.tileEntity.getBlockPos()), playerIn, (Block)ModBlocks.HERB_JAR.get());
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
            if (!this.moveItemStackTo(sourceStack, 36, 37, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (index >= 37) {
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
