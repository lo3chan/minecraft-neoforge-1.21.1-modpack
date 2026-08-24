package com.aetherteam.aether.client;

import com.aetherteam.cumulus.CumulusConfig;

public class AetherMenuUtil {
   public static boolean isAetherMenu() {
      return ((String)CumulusConfig.CLIENT.active_menu.get()).equals("aether:the_aether")
         || ((String)CumulusConfig.CLIENT.active_menu.get()).equals("aether:the_aether_left");
   }

   public static boolean isMinecraftMenu() {
      return ((String)CumulusConfig.CLIENT.active_menu.get()).equals("minecraft:minecraft")
         || ((String)CumulusConfig.CLIENT.active_menu.get()).equals("aether:minecraft_left");
   }
}
