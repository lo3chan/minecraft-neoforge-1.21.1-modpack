package net.mcreator.undeadrevamp.init;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

@EventBusSubscriber
public class UndeadRevamp2ModFuels {
   @SubscribeEvent
   public static void furnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
      ItemStack itemstack = event.getItemStack();
      if (itemstack.getItem() == UndeadRevamp2ModItems.DEVILURCHIN.get()) {
         event.setBurnTime(2500);
      }
   }
}
