package net.mehvahdjukaar.moonlight.api.misc;

import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface InvPlacer {
   InvPlacer.SimplePlacer EXISTING = of(SlotProvider.ALL, stack -> !stack.isEmpty());
   InvPlacer.SimplePlacer EMPTY = of(SlotProvider.ALL, ItemStack::isEmpty);
   InvPlacer.SimplePlacer ANY = of(SlotProvider.ALL);
   InvPlacer DEFAULT = EXISTING.or(ANY);
   InvPlacer DROP = (stack, inventory, player) -> {
      player.drop(stack, false);
      return true;
   };

   boolean place(ItemStack var1, Inventory var2, Player var3);

   default InvPlacer or(InvPlacer other) {
      return (stack, inventory, player) -> this.place(stack, inventory, player) || other.place(stack, inventory, player);
   }

   static InvPlacer.ExclusivePlacer exclusiveSequence() {
      return new InvPlacer.ExclusivePlacer();
   }

   static InvPlacer.SimplePlacer of(SlotProvider slots) {
      return of(slots, Predicates.alwaysTrue());
   }

   static InvPlacer.SimplePlacer of(SlotProvider slots, Predicate<ItemStack> predicate) {
      return new InvPlacer.SimplePlacer(slots, predicate);
   }

   static InvPlacer handOrExistingOrAnyAvoidEmptyHand(InteractionHand hand) {
      return exclusiveSequence().stage(handNotEmpty(hand)).stage(EXISTING).stage(ANY).or(ANY);
   }

   static InvPlacer handOrExistingOrAny(InteractionHand hand) {
      return hand(hand).or(EXISTING).or(ANY);
   }

   static InvPlacer existingOrAny() {
      return DEFAULT;
   }

   static InvPlacer.SimplePlacer handNotEmpty(InteractionHand hand) {
      return hand(hand, stack -> !stack.isEmpty());
   }

   static InvPlacer.SimplePlacer hand(InteractionHand hand) {
      return hand(hand, Predicates.alwaysTrue());
   }

   static InvPlacer.SimplePlacer hand(InteractionHand hand, Predicate<ItemStack> predicate) {
      return of(SlotProvider.hand(hand), predicate);
   }

   static InvPlacer.SimplePlacer slot(int slot) {
      return of(SlotProvider.single(slot));
   }

   public static class ExclusivePlacer implements InvPlacer {
      private final List<InvPlacer.ExclusivePlacer.Stage> stages = new ArrayList<>();
      private final Set<SlotProvider.Slot> visitedSlots = new HashSet<>();

      public InvPlacer.ExclusivePlacer stage(SlotProvider provider, Predicate<ItemStack> predicate) {
         this.stage(InvPlacer.of(provider, predicate));
         return this;
      }

      public InvPlacer.ExclusivePlacer stage(SlotProvider provider) {
         return this.stage(provider, Predicates.alwaysTrue());
      }

      public InvPlacer.ExclusivePlacer stage(InvPlacer.ExclusivePlacer.Stage stage) {
         this.stages.add(stage);
         return this;
      }

      @Override
      public boolean place(ItemStack stack, Inventory inventory, Player player) {
         for (InvPlacer.ExclusivePlacer.Stage stage : this.stages) {
            Iterator<SlotProvider.Slot> slots = stage.slotProvider().getSlots(inventory);

            while (slots.hasNext()) {
               SlotProvider.Slot slot = slots.next();
               if (!this.visitedSlots.contains(slot)) {
                  Predicate<ItemStack> predicate = stage.predicate();
                  if (predicate.test(slot.getStack())) {
                     this.visitedSlots.add(slot);
                     if (slot.add(stack, inventory, player)) {
                        return true;
                     }

                     return true;
                  }
               }
            }
         }

         return false;
      }

      public interface Stage {
         SlotProvider slotProvider();

         Predicate<ItemStack> predicate();
      }
   }

   public record SimplePlacer(SlotProvider slotProvider, Predicate<ItemStack> predicate) implements InvPlacer, InvPlacer.ExclusivePlacer.Stage {
      @Override
      public boolean place(ItemStack stack, Inventory inventory, Player player) {
         Iterator<SlotProvider.Slot> iterator = this.slotProvider.getSlots(inventory);

         while (iterator.hasNext()) {
            SlotProvider.Slot slot = iterator.next();
            if (this.predicate.test(slot.getStack()) && slot.add(stack, inventory, player)) {
               return true;
            }
         }

         return false;
      }
   }
}
