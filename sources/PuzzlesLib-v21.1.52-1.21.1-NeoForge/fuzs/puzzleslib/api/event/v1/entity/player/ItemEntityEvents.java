package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ItemEntityEvents {
   public static final EventInvoker<ItemEntityEvents.Toss> TOSS = EventInvoker.lookup(ItemEntityEvents.Toss.class);
   public static final EventInvoker<ItemEntityEvents.Pickup> PICKUP = EventInvoker.lookup(ItemEntityEvents.Pickup.class);
   public static final EventInvoker<ItemEntityEvents.Touch> TOUCH = EventInvoker.lookup(ItemEntityEvents.Touch.class);

   private ItemEntityEvents() {
   }

   @FunctionalInterface
   public interface Pickup {
      void onItemPickup(Player var1, ItemEntity var2, ItemStack var3);
   }

   @FunctionalInterface
   public interface Toss {
      EventResult onItemToss(Player var1, ItemEntity var2);
   }

   @FunctionalInterface
   public interface Touch {
      EventResult onItemTouch(Player var1, ItemEntity var2);
   }
}
