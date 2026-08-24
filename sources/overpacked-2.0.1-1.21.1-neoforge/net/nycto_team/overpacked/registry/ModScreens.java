package net.nycto_team.overpacked.registry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.nycto_team.overpacked.screen.GiantBackpackScreen;

@EventBusSubscriber(
   modid = "overpacked",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ModScreens {
   @SubscribeEvent
   public static void registerScreens(RegisterMenuScreensEvent event) {
      event.register(ModMenus.giant_backpack.get(), GiantBackpackScreen::new);
   }
}
