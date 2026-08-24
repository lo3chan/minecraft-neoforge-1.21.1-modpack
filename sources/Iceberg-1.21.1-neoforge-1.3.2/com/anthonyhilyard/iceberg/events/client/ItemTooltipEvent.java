package com.anthonyhilyard.iceberg.events.client;

import com.anthonyhilyard.iceberg.events.Event;
import com.anthonyhilyard.iceberg.events.EventFactory;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;

public interface ItemTooltipEvent {
   Event<ItemTooltipEvent> EVENT = EventFactory.create(ItemTooltipEvent.class, listeners -> (itemStack, context, flag, lines) -> {
      for (ItemTooltipEvent listener : listeners) {
         listener.onItemTooltip(itemStack, context, flag, lines);
      }
   });

   void onItemTooltip(ItemStack var1, TooltipContext var2, TooltipFlag var3, List<Component> var4);
}
