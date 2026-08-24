package net.blay09.mods.inventoryessentials.client.sorting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.blay09.mods.inventoryessentials.InventoryEssentialsExtensions;
import net.blay09.mods.inventoryessentials.InventorySorting;
import net.blay09.mods.inventoryessentials.InventoryUtils;
import net.blay09.mods.inventoryessentials.PlatformBindings;
import net.blay09.mods.inventoryessentials.tags.ModItemTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientInventorySorting {
   private static final Logger logger = LoggerFactory.getLogger(ClientInventorySorting.class);
   private static final Comparator<ItemStack> defaultComparator = Comparator.<ItemStack, String>comparing(
         itemStack -> itemStack.getHoverName().getString(), String.CASE_INSENSITIVE_ORDER
      )
      .thenComparing(Comparator.comparingInt(ItemStack::getCount).reversed())
      .thenComparing(itemStack -> itemStack.isEnchanted() ? 0 : 1)
      .thenComparingInt(ItemStack::getDamageValue)
      .thenComparing(itemStack -> Objects.toString(itemStack.getComponents(), ""));

   public static boolean sort(AbstractContainerScreen<?> screen, Slot baseSlot, InventorySorting sortingMode, ClientInventorySorting.SlotClicker clicker) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null) {
         return false;
      } else {
         AbstractContainerMenu menu = screen.getMenu();
         ArrayList<Slot> slotsToSort = new ArrayList<>();

         for (Slot slot : menu.slots) {
            if (isSortableSlot(screen, slot) && InventoryUtils.isSameInventory(baseSlot, slot, true)) {
               slotsToSort.add(slot);
            }
         }

         if (slotsToSort.isEmpty()) {
            try {
               logger.debug("No slots to sort found; clicked slot was {} in {}", baseSlot.getClass().getName(), BuiltInRegistries.MENU.getKey(menu.getType()));
            } catch (UnsupportedOperationException var14) {
               logger.debug("No slots to sort found; clicked slot was {} in {}", baseSlot.getClass().getName(), menu.getClass().getName());
            }

            return false;
         } else {
            consolidateStacks(menu, slotsToSort, clicker);
            if (sortingMode == InventorySorting.CONSOLIDATE_ONLY) {
               return true;
            } else {
               List<ItemStack> goalSorting = computeSortedList(slotsToSort, sortingMode);

               for (int i = 0; i < goalSorting.size(); i++) {
                  ItemStack goalStack = goalSorting.get(i);
                  ItemStack currentStack = slotsToSort.get(i).getItem();
                  if (!ItemStack.isSameItemSameComponents(goalStack, currentStack) || goalStack.getCount() != currentStack.getCount()) {
                     int foundSwapIndex = -1;

                     for (int j = i + 1; j < slotsToSort.size(); j++) {
                        ItemStack candidateStack = slotsToSort.get(j).getItem();
                        if (ItemStack.isSameItemSameComponents(goalStack, candidateStack) && goalStack.getCount() == candidateStack.getCount()) {
                           foundSwapIndex = j;
                           break;
                        }
                     }

                     if (foundSwapIndex != -1) {
                        swapSlots(menu, slotsToSort, i, foundSwapIndex, clicker);
                     }
                  }
               }

               return true;
            }
         }
      }
   }

   private static Comparator<ItemStack> getComparator(InventorySorting sortingMode) {
      return switch (sortingMode) {
         case CONSOLIDATE_ONLY -> throw new IllegalStateException("No comparator available for CONSOLIDATE_ONLY");
         case RETAIN_ORDER -> throw new IllegalStateException("No comparator available for RETAIN_ORDER");
         case ALPHABETICAL -> defaultComparator;
         case CREATIVE -> CreativeSorting.getCreativeComparator().thenComparing(defaultComparator);
      };
   }

   private static List<ItemStack> computeSortedList(List<Slot> slotsToSort, InventorySorting sortingMode) {
      List<ItemStack> stacks = slotsToSort.stream().map(Slot::getItem).<ItemStack>map(ItemStack::copy).filter(stack -> !stack.isEmpty()).toList();
      return sortingMode == InventorySorting.RETAIN_ORDER
         ? RetainOrderSorting.computeSortedList(stacks)
         : stacks.stream().sorted(getComparator(sortingMode)).toList();
   }

   private static void swapSlots(AbstractContainerMenu menu, List<Slot> slots, int firstIndex, int secondIndex, ClientInventorySorting.SlotClicker clicker) {
      if (firstIndex != secondIndex) {
         Slot firstSlot = slots.get(firstIndex);
         Slot secondSlot = slots.get(secondIndex);
         ItemStack firstStack = firstSlot.getItem();
         ItemStack secondStack = secondSlot.getItem();
         if (!firstSlot.hasItem() || !secondSlot.hasItem()) {
            Slot fromSlot = firstSlot.hasItem() ? firstSlot : secondSlot;
            Slot toSlot = firstSlot.hasItem() ? secondSlot : firstSlot;
            clicker.click(menu, fromSlot, 0, ClickType.PICKUP);
            clicker.click(menu, toSlot, 0, ClickType.PICKUP);
            return;
         }

         if (!firstStack.is(ModItemTags.BUNDLES) && !secondStack.is(ModItemTags.BUNDLES)) {
            Slot firstSlotToClick = ItemStack.isSameItemSameComponents(firstStack, secondStack) && secondStack.getCount() > firstStack.getCount()
               ? secondSlot
               : firstSlot;
            Slot secondSlotToClick = firstSlotToClick == firstSlot ? secondSlot : firstSlot;
            clicker.click(menu, firstSlotToClick, 0, ClickType.PICKUP);
            clicker.click(menu, secondSlotToClick, 0, ClickType.PICKUP);
            clicker.click(menu, firstSlotToClick, 0, ClickType.PICKUP);
         } else {
            Slot emptyBufferSlot = null;

            for (Slot candidate : slots) {
               if (!candidate.hasItem()) {
                  emptyBufferSlot = candidate;
                  break;
               }
            }

            if (emptyBufferSlot != null) {
               clicker.click(menu, firstSlot, 0, ClickType.PICKUP);
               clicker.click(menu, emptyBufferSlot, 0, ClickType.PICKUP);
               clicker.click(menu, secondSlot, 0, ClickType.PICKUP);
               clicker.click(menu, firstSlot, 0, ClickType.PICKUP);
               clicker.click(menu, emptyBufferSlot, 0, ClickType.PICKUP);
               clicker.click(menu, secondSlot, 0, ClickType.PICKUP);
            }
         }
      }
   }

   private static void consolidateStacks(AbstractContainerMenu menu, List<Slot> slots, ClientInventorySorting.SlotClicker clicker) {
      for (int i = 0; i < slots.size(); i++) {
         Slot thisSlot = slots.get(i);
         if (thisSlot.hasItem()) {
            ItemStack thisStack = thisSlot.getItem();

            for (int j = i + 1; j < slots.size(); j++) {
               int thisStackLimit = Math.min(thisSlot.getMaxStackSize(), thisSlot.getMaxStackSize(thisStack));
               if (thisStack.getCount() >= thisStackLimit) {
                  break;
               }

               Slot otherSlot = slots.get(j);
               ItemStack otherStack = otherSlot.getItem();
               if (!thisStack.is(ModItemTags.BUNDLES)
                  && !otherStack.is(ModItemTags.BUNDLES)
                  && !otherStack.isEmpty()
                  && ItemStack.isSameItemSameComponents(thisStack, otherStack)) {
                  clicker.click(menu, otherSlot, 0, ClickType.PICKUP);
                  clicker.click(menu, thisSlot, 0, ClickType.PICKUP);
                  if (!menu.getCarried().isEmpty()) {
                     clicker.click(menu, otherSlot, 0, ClickType.PICKUP);
                  }
               }
            }
         }
      }
   }

   private static boolean isSortableSlot(AbstractContainerScreen<?> screen, Slot slot) {
      if (slot.container instanceof Inventory) {
         int containerSlot = slot.getContainerSlot();
         if (containerSlot < 9 || containerSlot >= 36) {
            return false;
         }
      }

      return PlatformBindings.INSTANCE.isSortableSlot(slot) || InventoryEssentialsExtensions.isSortableSlot(screen, slot);
   }

   @FunctionalInterface
   public interface SlotClicker {
      void click(AbstractContainerMenu var1, Slot var2, int var3, ClickType var4);
   }
}
