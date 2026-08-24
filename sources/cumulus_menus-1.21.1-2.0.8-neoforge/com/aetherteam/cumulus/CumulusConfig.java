package com.aetherteam.cumulus;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class CumulusConfig {
   public static final ModConfigSpec CLIENT_SPEC;
   public static final CumulusConfig.Client CLIENT;

   static {
      Pair<CumulusConfig.Client, ModConfigSpec> clientSpecPair = new Builder().configure(CumulusConfig.Client::new);
      CLIENT_SPEC = (ModConfigSpec)clientSpecPair.getRight();
      CLIENT = (CumulusConfig.Client)clientSpecPair.getLeft();
   }

   public static class Client {
      public final ConfigValue<Boolean> enable_menu_api;
      public final ConfigValue<String> active_menu;
      public final ConfigValue<Boolean> enable_menu_list_button;
      public final ConfigValue<Boolean> enable_world_preview;
      public final ConfigValue<Boolean> enable_world_preview_button;
      public final ConfigValue<Boolean> enable_quick_load_button;

      public Client(Builder builder) {
         builder.push("Menu");
         this.enable_menu_api = builder.comment("Determines whether the Menu API is enabled or not")
            .translation("config.cumulus_menus.client.menu.enable_menu_api")
            .define("Enable Menu API", true);
         this.active_menu = builder.comment("Sets the current active menu title screen")
            .translation("config.cumulus_menus.client.menu.active_menu")
            .define("Active Menu", "cumulus_menus:minecraft");
         this.enable_menu_list_button = builder.comment("Adds a button to the top right of the main menu screen to open a menu selection screen")
            .translation("config.cumulus_menus.client.menu.enable_menu_list_button")
            .define("Enables menu selection button", true);
         builder.pop();
         builder.push("World Preview");
         this.enable_world_preview = builder.comment("Changes the background panorama into a preview of the latest played world")
            .translation("config.cumulus_menus.client.world_preview.enable_world_preview")
            .define("Enables world preview", false);
         this.enable_world_preview_button = builder.comment(
               "Adds a button to the top right of the main menu screen to toggle between the panorama and world preview"
            )
            .translation("config.cumulus_menus.client.world_preview.enable_world_preview_button")
            .define("Enables toggle world button", true);
         this.enable_quick_load_button = builder.comment(
               "Adds a button to the top right of the main menu screen to allow quick loading into a world if the world preview is enabled"
            )
            .translation("config.cumulus_menus.client.world_preview.enable_quick_load_button")
            .define("Enables quick load button", true);
         builder.pop();
      }
   }
}
