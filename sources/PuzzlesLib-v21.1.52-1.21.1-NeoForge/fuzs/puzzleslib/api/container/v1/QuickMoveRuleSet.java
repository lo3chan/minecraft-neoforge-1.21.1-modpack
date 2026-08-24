package fuzs.puzzleslib.api.container.v1;

import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class QuickMoveRuleSet {
   private static final Predicate<Slot> IS_INVENTORY = slot -> slot.container instanceof Inventory;
   private static final Predicate<Slot> IS_INVENTORY_ITEMS = slot -> IS_INVENTORY.test(slot) && slot.getContainerSlot() < 36;
   private static final Predicate<Slot> IS_INVENTORY_ARMOR = slot -> IS_INVENTORY.test(slot) && slot.getContainerSlot() >= 36 && slot.getContainerSlot() < 40;
   private static final Predicate<Slot> IS_HOTBAR = slot -> IS_INVENTORY_ITEMS.test(slot) && Inventory.isHotbarSlot(slot.getContainerSlot());
   private static final Predicate<Slot> IS_NOT_HOTBAR = slot -> IS_INVENTORY_ITEMS.test(slot) && !IS_HOTBAR.test(slot);
   private final List<QuickMoveRuleSet.Rule> rules = new ArrayList<>();
   private final List<Slot> slots;
   private final QuickMoveRuleSet.Action action;
   private final QuickMoveRuleSet.Type type;

   private QuickMoveRuleSet(AbstractContainerMenu menu, QuickMoveRuleSet.Action action, QuickMoveRuleSet.Type type) {
      this.slots = menu.slots;
      this.action = action;
      this.type = type;
   }

   public static QuickMoveRuleSet of(AbstractContainerMenu menu, QuickMoveRuleSet.Action action) {
      return of(menu, action, QuickMoveRuleSet.Type.RELAXED);
   }

   public static QuickMoveRuleSet of(AbstractContainerMenu menu, QuickMoveRuleSet.Action action, QuickMoveRuleSet.Type type) {
      return new QuickMoveRuleSet(menu, action, type);
   }

   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack itemInSlot = slot.getItem();
         itemStack = itemInSlot.copy();

         for (QuickMoveRuleSet.Rule rule : this.rules) {
            if (rule.isValid() && rule.filter().test(slot)) {
               if (this.action.moveItemStackTo(itemInSlot, rule.startIndex(), rule.endIndex(), rule.reverseDirection())) {
                  break;
               }

               if (this.type == QuickMoveRuleSet.Type.STRICT) {
                  return ItemStack.EMPTY;
               }
            }
         }

         if (itemInSlot.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemInSlot.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, itemInSlot);
      }

      return this.type == QuickMoveRuleSet.Type.RELAXED ? ItemStack.EMPTY : itemStack;
   }

   public QuickMoveRuleSet addContainerSlotRule(int... indices) {
      for (int index : indices) {
         this.addContainerSlotRule(index);
      }

      return this;
   }

   public QuickMoveRuleSet addContainerSlotRule(int index) {
      return this.addContainerSlotRule(index, Predicates.alwaysTrue());
   }

   public QuickMoveRuleSet addContainerSlotRule(int index, Predicate<Slot> filter) {
      return this.addContainerSlotRule(index, false, filter);
   }

   public QuickMoveRuleSet addContainerSlotRule(int index, boolean reverseDirection, Predicate<Slot> filter) {
      return this.addRule(
         new QuickMoveRuleSet.Rule(
            QuickMoveRuleSet.Rule.Type.CONTAINER_SLOT,
            index,
            index + 1,
            reverseDirection,
            slot -> IS_INVENTORY.test(slot) && this.slots.get(index).mayPlace(slot.getItem()) && filter.test(slot)
         )
      );
   }

   public QuickMoveRuleSet addContainerRule(Container container) {
      return this.addContainerRule(
         this.getInclusiveStartIndex(slot -> slot.container == container), this.getExclusiveEndIndex(slot -> slot.container == container)
      );
   }

   public QuickMoveRuleSet addContainerRule(int startIndex, int endIndex) {
      return this.addRule(
         new QuickMoveRuleSet.Rule(
            QuickMoveRuleSet.Rule.Type.CONTAINER,
            startIndex,
            endIndex,
            false,
            slot -> slot.index >= this.getInclusiveStartIndex(IS_INVENTORY_ITEMS) && slot.index < this.getExclusiveEndIndex(IS_INVENTORY_ITEMS)
         )
      );
   }

   public QuickMoveRuleSet addInventoryRules() {
      return this.addInventoryRules(true);
   }

   public QuickMoveRuleSet addInventoryRules(boolean reverseDirection) {
      this.addInventoryRule(QuickMoveRuleSet.Rule.Type.INVENTORY_ARMOR, reverseDirection, IS_INVENTORY_ARMOR);
      return this.addInventoryRule(QuickMoveRuleSet.Rule.Type.INVENTORY_ITEMS, reverseDirection, IS_INVENTORY_ITEMS);
   }

   private QuickMoveRuleSet addInventoryRule(QuickMoveRuleSet.Rule.Type type, boolean reverseDirection, Predicate<Slot> filter) {
      return this.addRule(
         new QuickMoveRuleSet.Rule(type, this.getInclusiveStartIndex(filter), this.getExclusiveEndIndex(filter), reverseDirection, IS_INVENTORY.negate())
      );
   }

   public QuickMoveRuleSet addInventoryCompartmentRules() {
      this.addInventoryCompartmentRule(QuickMoveRuleSet.Rule.Type.TO_ARMOR, QuickMoveRuleSet.Rule.Type.FROM_ARMOR, IS_INVENTORY_ARMOR, IS_INVENTORY_ITEMS);
      return this.addInventoryCompartmentRule(QuickMoveRuleSet.Rule.Type.TO_HOTBAR, QuickMoveRuleSet.Rule.Type.FROM_HOTBAR, IS_HOTBAR, IS_NOT_HOTBAR);
   }

   private QuickMoveRuleSet addInventoryCompartmentRule(
      QuickMoveRuleSet.Rule.Type to, QuickMoveRuleSet.Rule.Type from, Predicate<Slot> toFilter, Predicate<Slot> fromFilter
   ) {
      this.addRule(
         new QuickMoveRuleSet.Rule(
            to,
            this.getInclusiveStartIndex(toFilter),
            this.getExclusiveEndIndex(toFilter),
            false,
            slot -> slot.index >= this.getInclusiveStartIndex(fromFilter) && slot.index < this.getExclusiveEndIndex(fromFilter)
         )
      );
      return this.addRule(
         new QuickMoveRuleSet.Rule(
            from,
            this.getInclusiveStartIndex(fromFilter),
            this.getExclusiveEndIndex(fromFilter),
            false,
            slot -> slot.index >= this.getInclusiveStartIndex(toFilter) && slot.index < this.getExclusiveEndIndex(toFilter)
         )
      );
   }

   private QuickMoveRuleSet addRule(QuickMoveRuleSet.Rule rule) {
      this.rules.add(rule);
      return this;
   }

   private int getInclusiveStartIndex(Predicate<Slot> predicate) {
      for (int i = 0; i < this.slots.size(); i++) {
         if (predicate.test(this.slots.get(i))) {
            return i;
         }
      }

      return -1;
   }

   private int getExclusiveEndIndex(Predicate<Slot> predicate) {
      for (int i = this.slots.size() - 1; i >= 0; i--) {
         if (predicate.test(this.slots.get(i))) {
            return i + 1;
         }
      }

      return -1;
   }

   @FunctionalInterface
   public interface Action {
      boolean moveItemStackTo(ItemStack var1, int var2, int var3, boolean var4);
   }

   private record Rule(QuickMoveRuleSet.Rule.Type type, int startIndex, int endIndex, boolean reverseDirection, Predicate<Slot> filter) {
      public boolean isValid() {
         return this.startIndex != -1 && this.endIndex != -1;
      }

      static enum Type {
         CONTAINER_SLOT,
         CONTAINER,
         INVENTORY_ITEMS,
         INVENTORY_ARMOR,
         TO_HOTBAR,
         FROM_HOTBAR,
         TO_ARMOR,
         FROM_ARMOR;
      }
   }

   public static enum Type {
      STRICT,
      RELAXED,
      LENIENT;
   }
}
