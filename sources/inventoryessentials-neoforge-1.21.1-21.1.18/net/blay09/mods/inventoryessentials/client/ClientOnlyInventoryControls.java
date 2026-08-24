package net.blay09.mods.inventoryessentials.client;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.blay09.mods.inventoryessentials.InventoryEssentialsConfig;
import net.blay09.mods.inventoryessentials.InventoryOperations;
import net.blay09.mods.inventoryessentials.InventoryUtils;
import net.blay09.mods.inventoryessentials.client.sorting.ClientInventorySorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;

public class ClientOnlyInventoryControls implements InventoryControls {
   private final InventoryOperations operations = this.createOperations();

   protected InventoryOperations createOperations() {
      return new InventoryOperations(this::slotClick, InventoryOperations.SlotPolicy.always());
   }

   @Override
   public boolean singleTransfer(AbstractContainerScreen<?> screen, Slot clickedSlot) {
      AbstractContainerMenu menu = screen.getMenu();
      Player player = Minecraft.getInstance().player;
      if (player == null) {
         return false;
      } else if (!clickedSlot.mayPickup(player)) {
         return false;
      } else {
         ItemStack targetStack = clickedSlot.getItem().copy();
         if (targetStack.getCount() == 1) {
            this.slotClick(menu, clickedSlot, 0, ClickType.QUICK_MOVE);
            return true;
         } else {
            Slot fallbackSlot = null;

            for (Slot slot : menu.slots) {
               ItemStack stack = slot.getItem();
               if (this.isValidTargetSlot(slot)
                  && slot != clickedSlot
                  && slot.mayPlace(targetStack)
                  && !InventoryUtils.isSameInventory(clickedSlot, slot)
                  && stack.getCount() < Math.min(slot.getMaxStackSize(), slot.getMaxStackSize(stack))) {
                  if (ItemStack.isSameItemSameComponents(targetStack, stack)) {
                     this.slotClick(menu, clickedSlot, 1, ClickType.PICKUP);
                     this.slotClick(menu, slot, 1, ClickType.PICKUP);
                     this.slotClick(menu, clickedSlot, 0, ClickType.PICKUP);
                     return true;
                  }

                  if (!slot.hasItem() && fallbackSlot == null) {
                     fallbackSlot = slot;
                  }
               }
            }

            if (fallbackSlot != null) {
               this.slotClick(menu, clickedSlot, 1, ClickType.PICKUP);
               this.slotClick(menu, fallbackSlot, 1, ClickType.PICKUP);
               this.slotClick(menu, clickedSlot, 0, ClickType.PICKUP);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   @Override
   public boolean bulkTransferByType(AbstractContainerScreen<?> screen, Slot clickedSlot) {
      ItemStack clickedStackCopy = clickedSlot.getItem().copy();
      clickedStackCopy.setDamageValue(0);
      AbstractContainerMenu menu = screen.getMenu();
      List<Slot> transferSlots = new ArrayList<>();
      transferSlots.add(clickedSlot);

      for (Slot slot : menu.slots) {
         if (slot != clickedSlot && this.isValidTargetSlot(slot) && InventoryUtils.isSameInventory(slot, clickedSlot)) {
            ItemStack slotStackCopy = slot.getItem().copy();
            slotStackCopy.setDamageValue(0);
            if (ItemStack.isSameItemSameComponents(clickedStackCopy, slotStackCopy)) {
               transferSlots.add(slot);
            }
         }
      }

      for (Slot transferSlot : transferSlots) {
         this.slotClick(menu, transferSlot, 0, ClickType.QUICK_MOVE);
      }

      return true;
   }

   @Override
   public boolean bulkTransferSingle(AbstractContainerScreen<?> screen, Slot clickedSlot) {
      if (!clickedSlot.hasItem() && !InventoryEssentialsConfig.getActive().allowBulkTransferAllOnEmptySlot) {
         return false;
      } else {
         Player player = Minecraft.getInstance().player;
         if (player == null) {
            return false;
         } else {
            AbstractContainerMenu menu = screen.getMenu();
            boolean isProbablyMovingToPlayerInventory = false;
            if (!(clickedSlot.container instanceof Inventory)) {
               isProbablyMovingToPlayerInventory = InventoryUtils.containerContainsPlayerInventory(menu);
            }

            boolean clickedAnArmorItem = clickedSlot.getItem().getItem() instanceof Equipable equipable && equipable.getEquipmentSlot().isArmor();
            boolean isInsideInventory = menu instanceof InventoryMenu;
            boolean movedAny = false;
            if (isProbablyMovingToPlayerInventory) {
               Deque<Slot> emptySlots = new ArrayDeque<>();
               List<Slot> nonEmptySlots = new ArrayList<>();

               for (Slot slot : menu.slots) {
                  if (!InventoryUtils.isSameInventory(slot, clickedSlot) && slot.container instanceof Inventory && this.isValidTargetSlot(slot)) {
                     if (slot.hasItem()) {
                        nonEmptySlots.add(slot);
                     } else if (!Inventory.isHotbarSlot(slot.getContainerSlot())) {
                        emptySlots.add(slot);
                     }
                  }
               }

               for (Slot slotx : menu.slots) {
                  if (slotx.mayPickup(player)
                     && InventoryUtils.isSameInventory(slotx, clickedSlot, true)
                     && this.quickTransferSingle(menu, emptySlots, nonEmptySlots, slotx)) {
                     movedAny = true;
                  }
               }
            } else if (clickedAnArmorItem && isInsideInventory) {
               if (!InventoryEssentialsConfig.getActive().bulkTransferArmorSets) {
                  return false;
               }

               if (!menu.getCarried().isEmpty()) {
                  return false;
               }

               if (clickedSlot.index >= 5 && clickedSlot.index < 9) {
                  for (int i = 5; i < 9; i++) {
                     this.slotClick(menu, i, 0, ClickType.QUICK_MOVE);
                  }

                  return true;
               }

               Map<EquipmentSlot, Slot> armorSlots = InventoryUtils.findMatchingArmorSetSlots(menu, clickedSlot);
               List<EquipmentSlot> equipmentSlots = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

               for (int i = 5; i < 9; i++) {
                  EquipmentSlot equipmentSlot = equipmentSlots.get(i - 5);
                  Slot swapSlot = armorSlots.get(equipmentSlot);
                  if (swapSlot != null) {
                     this.slotClick(menu, i, 0, ClickType.PICKUP);
                     this.slotClick(menu, swapSlot, 0, ClickType.PICKUP);
                     this.slotClick(menu, i, 0, ClickType.PICKUP);
                  }
               }

               movedAny = true;
            } else {
               for (Slot slotxx : menu.slots) {
                  if (slotxx.mayPickup(player) && this.isValidTargetSlot(slotxx) && InventoryUtils.isSameInventory(slotxx, clickedSlot, true)) {
                     this.singleTransfer(screen, slotxx);
                     movedAny = true;
                  }
               }
            }

            return movedAny;
         }
      }
   }

   @Override
   public boolean bulkTransferAll(AbstractContainerScreen<?> screen, Slot clickedSlot) {
      if (!clickedSlot.hasItem() && !InventoryEssentialsConfig.getActive().allowBulkTransferAllOnEmptySlot) {
         return false;
      } else {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player == null) {
            return false;
         } else {
            AbstractContainerMenu menu = screen.getMenu();
            boolean isProbablyMovingToPlayerInventory = false;
            if (!(clickedSlot.container instanceof Inventory)) {
               isProbablyMovingToPlayerInventory = InventoryUtils.containerContainsPlayerInventory(menu);
            }

            boolean clickedAnArmorItem = clickedSlot.getItem().getItem() instanceof Equipable equipable && equipable.getEquipmentSlot().isArmor();
            boolean isInsideInventory = menu instanceof InventoryMenu;
            boolean movedAny = false;
            if (isProbablyMovingToPlayerInventory) {
               Deque<Slot> emptySlots = new ArrayDeque<>();
               List<Slot> nonEmptySlots = new ArrayList<>();

               for (Slot slot : menu.slots) {
                  if (!InventoryUtils.isSameInventory(slot, clickedSlot) && slot.container instanceof Inventory && this.isValidTargetSlot(slot)) {
                     if (slot.hasItem()) {
                        nonEmptySlots.add(slot);
                     } else if (!Inventory.isHotbarSlot(slot.getContainerSlot())) {
                        emptySlots.add(slot);
                     }
                  }
               }

               for (Slot slotx : menu.slots) {
                  if (slotx.mayPickup(player)
                     && InventoryUtils.isSameInventory(slotx, clickedSlot, true)
                     && this.quickTransferStack(menu, emptySlots, nonEmptySlots, slotx)) {
                     movedAny = true;
                  }
               }
            } else if (clickedAnArmorItem && isInsideInventory) {
               if (!InventoryEssentialsConfig.getActive().bulkTransferArmorSets) {
                  return false;
               }

               if (!menu.getCarried().isEmpty()) {
                  return false;
               }

               if (clickedSlot.index >= 5 && clickedSlot.index < 9) {
                  for (int i = 5; i < 9; i++) {
                     this.slotClick(menu, i, 0, ClickType.QUICK_MOVE);
                  }

                  return true;
               }

               Map<EquipmentSlot, Slot> armorSlots = InventoryUtils.findMatchingArmorSetSlots(menu, clickedSlot);
               List<EquipmentSlot> equipmentSlots = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

               for (int i = 5; i < 9; i++) {
                  EquipmentSlot equipmentSlot = equipmentSlots.get(i - 5);
                  Slot swapSlot = armorSlots.get(equipmentSlot);
                  if (swapSlot != null) {
                     this.slotClick(menu, i, 0, ClickType.PICKUP);
                     this.slotClick(menu, swapSlot, 0, ClickType.PICKUP);
                     this.slotClick(menu, i, 0, ClickType.PICKUP);
                  }
               }

               movedAny = true;
            } else {
               for (Slot slotxx : menu.slots) {
                  if (slotxx.mayPickup(player) && this.isValidTargetSlot(slotxx) && InventoryUtils.isSameInventory(slotxx, clickedSlot, true)) {
                     this.slotClick(menu, slotxx, 0, ClickType.QUICK_MOVE);
                     movedAny = true;
                  }
               }
            }

            return movedAny;
         }
      }
   }

   private boolean quickTransferStack(AbstractContainerMenu menu, Deque<Slot> emptySlots, List<Slot> nonEmptySlots, Slot slot) {
      ItemStack targetStack = slot.getItem().copy();
      if (targetStack.isEmpty()) {
         return false;
      } else {
         this.slotClick(menu, slot, 0, ClickType.PICKUP);

         for (Slot nonEmptySlot : nonEmptySlots) {
            ItemStack stack = nonEmptySlot.getItem();
            if (ItemStack.isSameItemSameComponents(targetStack, stack)) {
               boolean hasSpaceLeft = stack.getCount() < Math.min(nonEmptySlot.getMaxStackSize(), nonEmptySlot.getMaxStackSize(stack));
               if (hasSpaceLeft) {
                  this.slotClick(menu, nonEmptySlot, 0, ClickType.PICKUP);
                  ItemStack mouseItem = menu.getCarried();
                  if (mouseItem.isEmpty()) {
                     return true;
                  }
               }
            }
         }

         Iterator<Slot> iterator = emptySlots.iterator();

         while (iterator.hasNext()) {
            Slot emptySlot = iterator.next();
            this.slotClick(menu, emptySlot, 0, ClickType.PICKUP);
            if (emptySlot.hasItem()) {
               nonEmptySlots.add(emptySlot);
               iterator.remove();
            }

            ItemStack mouseItem = menu.getCarried();
            if (mouseItem.isEmpty()) {
               return true;
            }
         }

         ItemStack mouseItem = menu.getCarried();
         if (!mouseItem.isEmpty()) {
            this.slotClick(menu, slot, 0, ClickType.PICKUP);
         }

         return false;
      }
   }

   private boolean quickTransferSingle(AbstractContainerMenu menu, Deque<Slot> emptySlots, List<Slot> nonEmptySlots, Slot slot) {
      ItemStack targetStack = slot.getItem().copy();
      if (targetStack.isEmpty()) {
         return false;
      } else {
         this.slotClick(menu, slot, 0, ClickType.PICKUP);

         for (Slot nonEmptySlot : nonEmptySlots) {
            ItemStack stack = nonEmptySlot.getItem();
            if (ItemStack.isSameItemSameComponents(targetStack, stack)) {
               boolean hasSpaceLeft = stack.getCount() < Math.min(nonEmptySlot.getMaxStackSize(), nonEmptySlot.getMaxStackSize(stack));
               if (hasSpaceLeft) {
                  this.slotClick(menu, nonEmptySlot, 1, ClickType.PICKUP);
                  ItemStack mouseItem = menu.getCarried();
                  if (mouseItem.getCount() < targetStack.getCount()) {
                     this.slotClick(menu, slot, 0, ClickType.PICKUP);
                     return true;
                  }
               }
            }
         }

         Iterator<Slot> iterator = emptySlots.iterator();

         while (iterator.hasNext()) {
            Slot emptySlot = iterator.next();
            this.slotClick(menu, emptySlot, 1, ClickType.PICKUP);
            if (emptySlot.hasItem()) {
               nonEmptySlots.add(emptySlot);
               iterator.remove();
            }

            ItemStack mouseItem = menu.getCarried();
            if (mouseItem.getCount() < targetStack.getCount()) {
               this.slotClick(menu, slot, 0, ClickType.PICKUP);
               return true;
            }
         }

         ItemStack mouseItem = menu.getCarried();
         if (!mouseItem.isEmpty()) {
            this.slotClick(menu, slot, 0, ClickType.PICKUP);
         }

         return false;
      }
   }

   @Override
   public boolean restockContainer(AbstractContainerScreen<?> screen) {
      return this.transferToContainer(screen, false);
   }

   @Override
   public boolean restockInventory(AbstractContainerScreen<?> screen) {
      LocalPlayer player = Minecraft.getInstance().player;
      return player == null ? false : this.operations.transferToInventory(screen.getMenu(), player, true, false);
   }

   @Override
   public boolean dumpToContainer(AbstractContainerScreen<?> screen) {
      return this.transferToContainer(screen, true);
   }

   private boolean transferToContainer(AbstractContainerScreen<?> screen, boolean fillEmptySlots) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null) {
         return false;
      } else {
         AbstractContainerMenu menu = screen.getMenu();
         if (menu.getCarried().isEmpty() && !(menu instanceof InventoryMenu)) {
            ArrayList<Slot> sourceSlots = new ArrayList<>();
            ArrayList<Slot> nonEmptyTargetSlots = new ArrayList<>();
            ArrayList<Slot> emptyTargetSlots = new ArrayList<>();

            for (Slot slot : menu.slots) {
               if (this.isValidTargetSlot(slot)) {
                  if (slot.container instanceof Inventory) {
                     int containerSlot = slot.getContainerSlot();
                     if (containerSlot >= 9 && containerSlot < 36 && slot.mayPickup(player) && slot.hasItem()) {
                        sourceSlots.add(slot);
                     }
                  } else if (slot.hasItem()) {
                     nonEmptyTargetSlots.add(slot);
                  } else {
                     emptyTargetSlots.add(slot);
                  }
               }
            }

            if (!sourceSlots.isEmpty() && !nonEmptyTargetSlots.isEmpty()) {
               boolean movedAny = false;

               for (Slot sourceSlot : sourceSlots) {
                  if (sourceSlot.hasItem()) {
                     this.slotClick(menu, sourceSlot, 0, ClickType.PICKUP);
                     ItemStack carried = menu.getCarried();
                     if (!carried.isEmpty()) {
                        ItemStack sourceStack = carried.copy();
                        boolean hasMatchingItemInContainer = false;

                        for (Slot targetSlot : nonEmptyTargetSlots) {
                           ItemStack targetStack = targetSlot.getItem();
                           if (!targetStack.isEmpty() && ItemStack.isSameItemSameComponents(sourceStack, targetStack)) {
                              hasMatchingItemInContainer = true;
                              int targetLimit = Math.min(targetSlot.getMaxStackSize(), targetSlot.getMaxStackSize(targetStack));
                              if (targetStack.getCount() < targetLimit) {
                                 int oldCarriedCount = menu.getCarried().getCount();
                                 this.slotClick(menu, targetSlot, 0, ClickType.PICKUP);
                                 carried = menu.getCarried();
                                 if (carried.getCount() < oldCarriedCount) {
                                    movedAny = true;
                                 }

                                 if (carried.isEmpty()) {
                                    break;
                                 }
                              }
                           }
                        }

                        if (fillEmptySlots && !carried.isEmpty() && hasMatchingItemInContainer) {
                           Iterator<Slot> iterator = emptyTargetSlots.iterator();

                           while (iterator.hasNext()) {
                              Slot emptyTargetSlot = iterator.next();
                              if (emptyTargetSlot.hasItem()) {
                                 nonEmptyTargetSlots.add(emptyTargetSlot);
                                 iterator.remove();
                              } else if (emptyTargetSlot.mayPlace(sourceStack)) {
                                 int oldCarriedCountx = menu.getCarried().getCount();
                                 this.slotClick(menu, emptyTargetSlot, 0, ClickType.PICKUP);
                                 carried = menu.getCarried();
                                 if (carried.getCount() < oldCarriedCountx) {
                                    movedAny = true;
                                    if (emptyTargetSlot.hasItem()) {
                                       nonEmptyTargetSlots.add(emptyTargetSlot);
                                       iterator.remove();
                                    }
                                 }

                                 if (carried.isEmpty()) {
                                    break;
                                 }
                              }
                           }
                        }

                        if (!menu.getCarried().isEmpty()) {
                           this.slotClick(menu, sourceSlot, 0, ClickType.PICKUP);
                        }
                     }
                  }
               }

               return movedAny;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public void dragTransfer(AbstractContainerScreen<?> screen, Slot clickedSlot) {
      this.slotClick(screen.getMenu(), clickedSlot, 0, ClickType.QUICK_MOVE);
   }

   @Override
   public boolean sort(AbstractContainerScreen<?> screen, Slot baseSlot) {
      return ClientInventorySorting.sort(screen, baseSlot, InventoryEssentialsConfig.getActive().inventorySorting, this::slotClick);
   }

   protected void slotClick(AbstractContainerMenu menu, Slot slot, int mouseButton, ClickType clickType) {
      this.slotClick(menu, slot.index, mouseButton, clickType);
   }

   protected void slotClick(AbstractContainerMenu menu, int slotIndex, int mouseButton, ClickType clickType) {
      Player player = Minecraft.getInstance().player;
      MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
      if (player != null && gameMode != null && (menu.isValidSlotIndex(slotIndex) || slotIndex == -999)) {
         gameMode.handleInventoryMouseClick(menu.containerId, slotIndex, mouseButton, clickType, player);
      }
   }

   @Override
   public boolean dropByType(AbstractContainerScreen<?> screen, Slot hoverSlot) {
      ItemStack targetStack = hoverSlot.getItem().copy();
      AbstractContainerMenu menu = screen.getMenu();
      List<Slot> transferSlots = new ArrayList<>();
      transferSlots.add(hoverSlot);

      for (Slot slot : menu.slots) {
         if (slot != hoverSlot && this.isValidTargetSlot(slot) && InventoryUtils.isSameInventory(slot, hoverSlot)) {
            ItemStack stack = slot.getItem();
            if (ItemStack.isSameItemSameComponents(targetStack, stack)) {
               transferSlots.add(slot);
            }
         }
      }

      for (Slot transferSlot : transferSlots) {
         this.slotClick(menu, transferSlot, 1, ClickType.THROW);
      }

      return true;
   }

   @Override
   public boolean dropByType(AbstractContainerScreen<?> screen, ItemStack targetStack) {
      if (targetStack.isEmpty()) {
         return false;
      } else {
         AbstractContainerMenu menu = screen.getMenu();
         List<Slot> transferSlots = new ArrayList<>();

         for (Slot slot : menu.slots) {
            ItemStack stack = slot.getItem();
            if (ItemStack.isSameItemSameComponents(targetStack, stack) && this.isValidTargetSlot(slot)) {
               transferSlots.add(slot);
            }
         }

         this.slotClick(menu, -999, 0, ClickType.PICKUP);

         for (Slot transferSlot : transferSlots) {
            this.slotClick(menu, transferSlot, 1, ClickType.THROW);
         }

         return true;
      }
   }

   protected boolean isValidTargetSlot(Slot slot) {
      return true;
   }
}
