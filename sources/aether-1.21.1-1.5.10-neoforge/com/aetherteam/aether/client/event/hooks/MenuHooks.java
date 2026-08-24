package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.cumulus.client.CumulusClient;
import java.util.Calendar;
import java.util.function.Predicate;
import net.minecraft.client.gui.screens.TitleScreen;

public class MenuHooks {
   public static void setCustomSplashText(TitleScreen screen) {
      Predicate<Calendar> condition = calendar -> calendar.get(2) + 1 == 7 && calendar.get(5) == 22;
      CumulusClient.MENU_HELPER.setCustomSplash(screen, condition, "Happy anniversary to the Aether!");
   }
}
