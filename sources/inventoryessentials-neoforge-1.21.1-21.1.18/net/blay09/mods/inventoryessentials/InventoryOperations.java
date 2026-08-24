package net.blay09.mods.inventoryessentials;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class InventoryOperations {
   public static final int SELECTION_SIZE = 9;
   private final InventoryOperations.SlotClickHandler slotClickHandler;
   private final InventoryOperations.SlotPolicy slotPolicy;

   public InventoryOperations(InventoryOperations.SlotClickHandler slotClickHandler, InventoryOperations.SlotPolicy slotPolicy) {
      this.slotClickHandler = slotClickHandler;
      this.slotPolicy = slotPolicy;
   }

   public boolean transferToContainer(AbstractContainerMenu menu, Player player, boolean fillEmptySlots) {
      if (menu.getCarried().isEmpty() && !(menu instanceof InventoryMenu)) {
         ArrayList<Slot> sourceSlots = new ArrayList<>();
         ArrayList<Slot> nonEmptyTargetSlots = new ArrayList<>();
         ArrayList<Slot> emptyTargetSlots = new ArrayList<>();

         for (Slot slot : menu.slots) {
            if (this.slotPolicy.isValidSlot(slot)) {
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
                  this.slotClickHandler.click(menu, sourceSlot, 0, ClickType.PICKUP);
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
                              this.slotClickHandler.click(menu, targetSlot, 0, ClickType.PICKUP);
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
                              this.slotClickHandler.click(menu, emptyTargetSlot, 0, ClickType.PICKUP);
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
                        this.slotClickHandler.click(menu, sourceSlot, 0, ClickType.PICKUP);
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

   public boolean transferToInventory(AbstractContainerMenu menu, Player player, boolean includeHotbar, boolean fillEmptySlots) {
      if (menu.getCarried().isEmpty() && !(menu instanceof InventoryMenu)) {
         ArrayList<Slot> sourceSlots = new ArrayList<>();
         ArrayList<Slot> nonEmptyTargetSlots = new ArrayList<>();
         ArrayList<Slot> emptyTargetSlots = new ArrayList<>();

         for (Slot slot : menu.slots) {
            if (this.slotPolicy.isValidSlot(slot)) {
               if (slot.container instanceof Inventory) {
                  int containerSlot = slot.getContainerSlot();
                  if (containerSlot >= 0 && containerSlot < 36 && (includeHotbar || containerSlot >= 9)) {
                     if (slot.hasItem()) {
                        nonEmptyTargetSlots.add(slot);
                     } else {
                        emptyTargetSlots.add(slot);
                     }
                  }
               } else if (slot.hasItem() && slot.mayPickup(player)) {
                  sourceSlots.add(slot);
               }
            }
         }

         if (!sourceSlots.isEmpty() && !nonEmptyTargetSlots.isEmpty()) {
            boolean movedAny = false;

            for (Slot sourceSlot : sourceSlots) {
               if (sourceSlot.hasItem()) {
                  this.slotClickHandler.click(menu, sourceSlot, 0, ClickType.PICKUP);
                  ItemStack carried = menu.getCarried();
                  if (!carried.isEmpty()) {
                     ItemStack sourceStack = carried.copy();
                     boolean hasMatchingItemInInventory = false;

                     for (Slot targetSlot : nonEmptyTargetSlots) {
                        ItemStack targetStack = targetSlot.getItem();
                        if (!targetStack.isEmpty() && ItemStack.isSameItemSameComponents(sourceStack, targetStack)) {
                           hasMatchingItemInInventory = true;
                           int targetLimit = Math.min(targetSlot.getMaxStackSize(), targetSlot.getMaxStackSize(targetStack));
                           if (targetStack.getCount() < targetLimit) {
                              int oldCarriedCount = menu.getCarried().getCount();
                              this.slotClickHandler.click(menu, targetSlot, 0, ClickType.PICKUP);
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

                     if (fillEmptySlots && !carried.isEmpty() && hasMatchingItemInInventory) {
                        Iterator<Slot> iterator = emptyTargetSlots.iterator();

                        while (iterator.hasNext()) {
                           Slot emptyTargetSlot = iterator.next();
                           if (emptyTargetSlot.hasItem()) {
                              nonEmptyTargetSlots.add(emptyTargetSlot);
                              iterator.remove();
                           } else if (emptyTargetSlot.mayPlace(sourceStack)) {
                              int oldCarriedCountx = menu.getCarried().getCount();
                              this.slotClickHandler.click(menu, emptyTargetSlot, 0, ClickType.PICKUP);
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
                        this.slotClickHandler.click(menu, sourceSlot, 0, ClickType.PICKUP);
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

   public boolean isValidSlot(Slot slot) {
      return this.slotPolicy.isValidSlot(slot);
   }

   public static InventoryOperations forServerPlayer(ServerPlayer player) {
      return new InventoryOperations(
         (containerMenu, slot, mouseButton, clickType) -> containerMenu.clicked(slot.index, mouseButton, clickType, player),
         InventoryOperations.SlotPolicy.always()
      );
   }

   @FunctionalInterface
   public interface SlotClickHandler {
      void click(AbstractContainerMenu var1, Slot var2, int var3, ClickType var4);
   }

   @FunctionalInterface
   public interface SlotPolicy {
      boolean isValidSlot(Slot var1);

      static InventoryOperations.SlotPolicy always() {
         return slot -> true;
      }
   }
}
