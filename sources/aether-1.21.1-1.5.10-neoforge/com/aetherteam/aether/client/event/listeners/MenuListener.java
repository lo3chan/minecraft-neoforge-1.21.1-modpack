package com.aetherteam.aether.client.event.listeners;

import com.aetherteam.aether.client.event.hooks.MenuHooks;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ScreenEvent.Init.Post;

public class MenuListener {
   public static void listen(IEventBus bus) {
      bus.addListener(MenuListener::onGuiInitialize);
   }

   public static void onGuiInitialize(Post event) {
      if (event.getScreen() instanceof TitleScreen titleScreen) {
         MenuHooks.setCustomSplashText(titleScreen);
      }
   }
}
