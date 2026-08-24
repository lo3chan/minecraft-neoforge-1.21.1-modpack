package com.aetherteam.cumulus.api;

import com.aetherteam.cumulus.platform.Services;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.slf4j.Logger;

public class Menus {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ResourceLocation MINECRAFT_ICON = ResourceLocation.withDefaultNamespace("textures/block/grass_block_side.png");
   public static final Component MINECRAFT_NAME = Component.translatable("cumulus_menus.menu_title.minecraft");
   private static Map<ResourceLocation, Menu> MENUS;
   public static Menu MINECRAFT;

   @Internal
   public static void init() {
      HashMap<ResourceLocation, Menu> menus = new HashMap<>(MENUS);

      for (MenuInitializer menuInitializer : Services.PLATFORM.getMenuInitializers()) {
         menuInitializer.registerMenus((location, menu) -> {
            if (menus.containsKey(location)) {
               LOGGER.error("A Cumulus Menu [{}] was already registered meaning such will be not be registered!", location);
            } else {
               menus.put(location, menu);
            }
         });
      }

      MENUS = menus;
   }

   private static Menu registerVanillaScreen(Map<ResourceLocation, Menu> menus) {
      Menu vanilla = new Menu(MINECRAFT_ICON, MINECRAFT_NAME, new TitleScreen(true));
      menus.put(ResourceLocation.withDefaultNamespace("minecraft"), vanilla);
      return vanilla;
   }

   @Nullable
   public static ResourceLocation getKey(Menu menu) {
      for (Entry<ResourceLocation, Menu> entry : MENUS.entrySet()) {
         if (entry.getValue().equals(menu)) {
            return entry.getKey();
         }
      }

      return null;
   }

   public static Menu get(ResourceLocation type) {
      return MENUS.getOrDefault(type, MINECRAFT);
   }

   public static List<Menu> getMenus() {
      return List.copyOf(MENUS.values());
   }

   public static List<Screen> getMenuScreens() {
      return getMenus().stream().map(Menu::screen).collect(Collectors.toList());
   }

   static {
      HashMap<ResourceLocation, Menu> menus = new HashMap<>();
      MINECRAFT = registerVanillaScreen(menus);
      MENUS = menus;
   }
}
