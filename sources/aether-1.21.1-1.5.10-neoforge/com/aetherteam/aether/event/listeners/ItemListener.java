package com.aetherteam.aether.event.listeners;

import com.aetherteam.aether.event.hooks.ItemHooks;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class ItemListener {
   public static void listen(IEventBus bus) {
      bus.addListener(EventPriority.LOWEST, ItemListener::onTooltipAdd);
   }

   public static void onTooltipAdd(ItemTooltipEvent event) {
      ItemStack itemStack = event.getItemStack();
      TooltipFlag tooltipFlag = event.getFlags();
      List<Component> itemTooltips = event.getToolTip();
      ItemHooks.addDungeonTooltips(itemTooltips, itemStack, tooltipFlag);
   }
}
