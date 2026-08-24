package com.aetherteam.cumulus.client.event.listeners;

import com.aetherteam.cumulus.client.CumulusClient;
import com.aetherteam.cumulus.client.event.hooks.MenuHooks;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent.Opening;
import net.neoforged.neoforge.client.event.ScreenEvent.Render.Post;

@EventBusSubscriber(
   modid = "cumulus_menus",
   value = {Dist.CLIENT}
)
public class MenuListener {
   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onGuiOpenLow(Opening event) {
      Screen screen = event.getScreen();
      Screen newScreen = event.getNewScreen();
      MenuHooks.setLastSplash(screen, CumulusClient.MENU_HELPER);
      MenuHooks.trackFallbacks(newScreen);
      Screen titleScreen = MenuHooks.setupCustomMenu(screen, CumulusClient.MENU_HELPER);
      if (titleScreen != null) {
         event.setNewScreen(titleScreen);
      }
   }

   @SubscribeEvent
   public static void onGuiDraw(Post event) {
      MenuHooks.resetFade(CumulusClient.MENU_HELPER);
   }

   @SubscribeEvent
   public static void onGuiInitialize(net.neoforged.neoforge.client.event.ScreenEvent.Init.Post event) {
      Screen screen = event.getScreen();
      if (screen instanceof TitleScreen) {
         Button menuSwitchButton = MenuHooks.setupMenuScreenButton(screen);
         if (menuSwitchButton != null) {
            event.addListener(menuSwitchButton);
         }

         Button toggleWorldButton = MenuHooks.setupToggleWorldButton(screen);
         if (toggleWorldButton != null) {
            event.addListener(toggleWorldButton);
         }

         Button quickLoadButton = MenuHooks.setupQuickLoadButton(screen);
         if (quickLoadButton != null) {
            event.addListener(quickLoadButton);
         }
      }
   }
}
