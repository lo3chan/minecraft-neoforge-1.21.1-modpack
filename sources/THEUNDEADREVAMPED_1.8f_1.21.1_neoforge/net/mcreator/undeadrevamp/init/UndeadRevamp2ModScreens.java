package net.mcreator.undeadrevamp.init;

import net.mcreator.undeadrevamp.client.gui.BlackpetalblockScreen;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class UndeadRevamp2ModScreens {
   @SubscribeEvent
   public static void clientLoad(RegisterMenuScreensEvent event) {
      event.register((MenuType)UndeadRevamp2ModMenus.BLACKPETALBLOCK.get(), BlackpetalblockScreen::new);
   }
}
