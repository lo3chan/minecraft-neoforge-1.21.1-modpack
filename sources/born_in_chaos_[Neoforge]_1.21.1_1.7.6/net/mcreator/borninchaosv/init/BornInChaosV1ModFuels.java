package net.mcreator.borninchaosv.init;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

@EventBusSubscriber
public class BornInChaosV1ModFuels {
   @SubscribeEvent
   public static void furnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
      ItemStack itemstack = event.getItemStack();
      if (itemstack.getItem() == BornInChaosV1ModItems.FIRE_DUST.get()) {
         event.setBurnTime(3500);
      } else if (itemstack.getItem() == BornInChaosV1ModItems.SMOLDERING_INFERNAL_EMBER.get()) {
         event.setBurnTime(3000);
      }
   }
}
