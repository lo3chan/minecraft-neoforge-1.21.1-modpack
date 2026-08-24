package dev.latvian.mods.kubejs.client;

import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class MenuScreenRegistryKubeEvent implements ClientKubeEvent {
   private final RegisterMenuScreensEvent event;

   public MenuScreenRegistryKubeEvent(RegisterMenuScreensEvent event) {
      this.event = event;
   }

   public void register(MenuType<?> type, ScreenConstructor constructor) {
      this.event.register(type, constructor);
   }
}
