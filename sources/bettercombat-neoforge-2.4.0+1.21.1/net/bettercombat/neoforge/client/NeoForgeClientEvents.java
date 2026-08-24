package net.bettercombat.neoforge.client;

import net.bettercombat.client.WeaponAttributeTooltip;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(
   modid = "bettercombat",
   bus = Bus.GAME,
   value = {Dist.CLIENT}
)
public class NeoForgeClientEvents {
   @SubscribeEvent
   public static void onTooltip(ItemTooltipEvent event) {
      WeaponAttributeTooltip.modifyTooltip(event.getItemStack(), event.getToolTip());
   }
}
