package com.anthonyhilyard.iceberg.events.client;

import com.anthonyhilyard.iceberg.events.Event;
import com.anthonyhilyard.iceberg.events.EventFactory;
import java.util.UUID;
import net.minecraft.world.item.ItemStack;

public interface NewItemPickupEvent {
   Event<NewItemPickupEvent> EVENT = EventFactory.create(NewItemPickupEvent.class, listeners -> (playerUUID, itemStack) -> {
      for (NewItemPickupEvent listener : listeners) {
         listener.onItemPickup(playerUUID, itemStack);
      }
   });

   void onItemPickup(UUID var1, ItemStack var2);
}
