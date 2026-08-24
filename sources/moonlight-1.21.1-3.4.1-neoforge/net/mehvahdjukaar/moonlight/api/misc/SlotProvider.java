package net.mehvahdjukaar.moonlight.api.misc;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface SlotProvider {
   SlotProvider ALL = inv -> IntStream.range(0, inv.items.size()).mapToObj(i -> SlotProvider.Slot.invSlot(inv, i)).iterator();
   SlotProvider OFF_HAND = inv -> IntStream.range(0, inv.offhand.size()).mapToObj(i -> SlotProvider.Slot.offHandSlot(inv, i)).iterator();
   SlotProvider MAIN_HAND = inv -> List.of(SlotProvider.Slot.invSlot(inv, inv.selected)).iterator();

   Iterator<SlotProvider.Slot> getSlots(Inventory var1);

   static SlotProvider hand(InteractionHand hand) {
      return hand == InteractionHand.MAIN_HAND ? MAIN_HAND : OFF_HAND;
   }

   static SlotProvider single(int slot) {
      return inv -> List.of(SlotProvider.Slot.invSlot(inv, slot)).iterator();
   }

   public interface Slot {
      ItemStack getStack();

      boolean add(ItemStack var1, Inventory var2, Player var3);

      static SlotProvider.Slot invSlot(final Inventory inv, final int slot) {
         return new SlotProvider.Slot() {
            @Override
            public ItemStack getStack() {
               return inv.getItem(slot);
            }

            @Override
            public boolean add(ItemStack toAdd, Inventory invx, Player player) {
               ItemStack current = this.getStack();
               if (current.isEmpty()) {
                  inv.setItem(slot, toAdd);
                  return true;
               } else if (!inv.hasRemainingSpaceForItem(current, toAdd)) {
                  return false;
               } else if (toAdd.isEmpty()) {
                  return false;
               } else {
                  try {
                     int originalCount;
                     do {
                        originalCount = toAdd.getCount();
                        toAdd.setCount(inv.addResource(slot, toAdd));
                     } while (!toAdd.isEmpty() && toAdd.getCount() < originalCount);

                     return toAdd.getCount() < originalCount;
                  } catch (Throwable var8) {
                     CrashReport crashReport = CrashReport.forThrowable(var8, "Adding item to inventory");
                     CrashReportCategory crashReportCategory = crashReport.addCategory("Item being added");
                     crashReportCategory.setDetail("Item ID", Item.getId(toAdd.getItem()));
                     crashReportCategory.setDetail("Item data", toAdd.getDamageValue());
                     crashReportCategory.setDetail("Item name", () -> toAdd.getHoverName().getString());
                     throw new ReportedException(crashReport);
                  }
               }
            }
         };
      }

      static SlotProvider.Slot offHandSlot(final Inventory inv, final int offHandSlot) {
         return new SlotProvider.Slot() {
            @Override
            public ItemStack getStack() {
               return (ItemStack)inv.offhand.get(offHandSlot);
            }

            @Override
            public boolean add(ItemStack toAdd, Inventory invx, Player player) {
               ItemStack stackInSlot = this.getStack();
               if (stackInSlot.isEmpty()) {
                  inv.offhand.set(offHandSlot, toAdd);
                  return true;
               } else if (!stackInSlot.isEmpty() && !inv.hasRemainingSpaceForItem(stackInSlot, toAdd)) {
                  return false;
               } else if (toAdd.isEmpty()) {
                  return false;
               } else {
                  try {
                     int originalCount;
                     do {
                        originalCount = toAdd.getCount();
                        this.addResourceOffHand(toAdd, inv);
                     } while (!toAdd.isEmpty() && toAdd.getCount() < originalCount);

                     return toAdd.getCount() < originalCount;
                  } catch (Throwable var8) {
                     CrashReport crashReport = CrashReport.forThrowable(var8, "Adding item to inventory");
                     CrashReportCategory crashReportCategory = crashReport.addCategory("Item being added");
                     crashReportCategory.setDetail("Item ID", Item.getId(toAdd.getItem()));
                     crashReportCategory.setDetail("Item data", toAdd.getDamageValue());
                     crashReportCategory.setDetail("Item name", () -> toAdd.getHoverName().getString());
                     throw new ReportedException(crashReport);
                  }
               }
            }

            private void addResourceOffHand(ItemStack toAdd, Inventory invx) {
               NonNullList<ItemStack> offHand = inv.offhand;

               for (int offSlot = 0; offSlot < offHand.size(); offSlot++) {
                  int stackCount = toAdd.getCount();
                  ItemStack itemStack = (ItemStack)offHand.get(offSlot);
                  if (itemStack.isEmpty()) {
                     itemStack = toAdd.copyWithCount(0);
                     offHand.set(offSlot, itemStack);
                     return;
                  }

                  int possibleSpace = inv.getMaxStackSize(itemStack) - itemStack.getCount();
                  int addedCount = Math.min(stackCount, possibleSpace);
                  if (addedCount != 0) {
                     stackCount -= addedCount;
                     itemStack.grow(addedCount);
                     itemStack.setPopTime(5);
                     toAdd.setCount(stackCount);
                  }
               }
            }
         };
      }
   }
}
