package net.blay09.mods.balm.world.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class QuickMove {
   public static final String CONTAINER = "container";
   public static final String PLAYER = "player";
   private static final String PLAYER_INVENTORY = "inventory";
   private static final String PLAYER_HOTBAR = "hotbar";

   private QuickMove() {
   }

   public static QuickMove.Builder create(QuickMove.MoveItemStackTo moveItemStackTo) {
      return new QuickMove.Builder(moveItemStackTo);
   }

   public static QuickMove.Builder create(AbstractContainerMenu menu, QuickMove.MoveItemStackTo moveItemStackTo) {
      int containerSlotCount = menu.slots.size() - 36;
      return new QuickMove.Builder(moveItemStackTo)
         .slotRange("container", 0, containerSlotCount)
         .slotRange("player", containerSlotCount, containerSlotCount + 36)
         .slotRange("inventory", containerSlotCount, containerSlotCount + 27)
         .slotRange("hotbar", containerSlotCount + 27, containerSlotCount + 36);
   }

   public static final class Builder {
      private final QuickMove.MoveItemStackTo moveItemStackTo;
      private final List<QuickMove.NamedRange> ranges = new ArrayList<>();
      private final List<QuickMove.Route> routes = new ArrayList<>();
      private boolean includeDefaultRoutes = true;

      private Builder(QuickMove.MoveItemStackTo moveItemStackTo) {
         this.moveItemStackTo = moveItemStackTo;
      }

      public QuickMove.Builder slot(String name, int slot) {
         this.ranges.add(new QuickMove.NamedRange(name, slot, slot + 1));
         return this;
      }

      public QuickMove.Builder slotRange(String name, int startInclusive, int endExclusive) {
         this.ranges.add(new QuickMove.NamedRange(name, startInclusive, endExclusive));
         return this;
      }

      public QuickMove.Builder route(String sourceRangeName, String targetRangeName) {
         return this.route(sourceRangeName, targetRangeName, false);
      }

      public QuickMove.Builder route(String sourceRangeName, String targetRangeName, boolean reverse) {
         return this.route(it -> true, sourceRangeName, targetRangeName, reverse);
      }

      public QuickMove.Builder route(Predicate<ItemStack> predicate, String sourceRangeName, String targetRangeName) {
         return this.route(predicate, sourceRangeName, targetRangeName, false);
      }

      public QuickMove.Builder route(Predicate<ItemStack> predicate, String sourceRangeName, String targetRangeName, boolean reverse) {
         this.routes.add(new QuickMove.Route(predicate, sourceRangeName, targetRangeName, reverse));
         return this;
      }

      public QuickMove.Builder disableDefaultRoutes() {
         this.includeDefaultRoutes = false;
         return this;
      }

      public QuickMove.Routing build() {
         if (this.includeDefaultRoutes) {
            this.route("container", "player");
            this.route("player", "player");
         }

         return new QuickMove.Routing(List.copyOf(this.ranges), List.copyOf(this.routes), this.moveItemStackTo);
      }
   }

   @FunctionalInterface
   public interface MoveItemStackTo {
      boolean moveItemStackTo(ItemStack var1, int var2, int var3, boolean var4);
   }

   protected record NamedRange(String name, int start, int end) {
      boolean contains(int index) {
         return index >= this.start && index < this.end;
      }
   }

   protected record Route(Predicate<ItemStack> predicate, String sourceName, String targetName, boolean reverse) {
   }

   public record Routing(List<QuickMove.NamedRange> ranges, List<QuickMove.Route> routes, QuickMove.MoveItemStackTo moveItemStackTo) {
      public ItemStack transfer(AbstractContainerMenu menu, Player player, int index) {
         ItemStack itemStack = ItemStack.EMPTY;
         Slot slot = (Slot)menu.slots.get(index);
         if (!slot.hasItem()) {
            return ItemStack.EMPTY;
         } else {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();
            QuickMove.NamedRange sourceRange = this.findRangeByIndex(index);
            boolean moved = false;
            if (sourceRange != null) {
               for (QuickMove.Route route : this.routes) {
                  if (route.sourceName.equals(sourceRange.name) && route.predicate.test(slotStack)) {
                     QuickMove.NamedRange targetRange = this.findRangeByName(route.targetName);
                     if (targetRange != null) {
                        if (targetRange.name.equals("player")) {
                           QuickMove.NamedRange hotbarRange = this.findRangeByName("hotbar");
                           QuickMove.NamedRange inventoryRange = this.findRangeByName("inventory");
                           if (hotbarRange != null
                              && !hotbarRange.contains(index)
                              && this.moveItemStackTo.moveItemStackTo(slotStack, hotbarRange.start, hotbarRange.end, !route.reverse)) {
                              moved = true;
                              break;
                           }

                           if (inventoryRange != null
                              && !inventoryRange.contains(index)
                              && this.moveItemStackTo.moveItemStackTo(slotStack, inventoryRange.start, inventoryRange.end, route.reverse)) {
                              moved = true;
                              break;
                           }
                        } else if (this.moveItemStackTo.moveItemStackTo(slotStack, targetRange.start, targetRange.end, route.reverse)) {
                           moved = true;
                           break;
                        }
                     }
                  }
               }
            }

            if (!moved) {
               return ItemStack.EMPTY;
            } else {
               if (slotStack.isEmpty()) {
                  slot.setByPlayer(ItemStack.EMPTY);
               } else {
                  slot.setChanged();
               }

               if (slotStack.getCount() == itemStack.getCount()) {
                  return ItemStack.EMPTY;
               } else {
                  slot.onTake(player, slotStack);
                  return itemStack;
               }
            }
         }
      }

      private QuickMove.NamedRange findRangeByIndex(int index) {
         for (QuickMove.NamedRange range : this.ranges) {
            if (range.contains(index)) {
               return range;
            }
         }

         return null;
      }

      private QuickMove.NamedRange findRangeByName(String name) {
         for (QuickMove.NamedRange range : this.ranges) {
            if (range.name.equals(name)) {
               return range;
            }
         }

         return null;
      }
   }
}
