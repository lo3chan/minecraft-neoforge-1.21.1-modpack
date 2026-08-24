package net.mehvahdjukaar.moonlight.core.client.config;

import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigReloadType;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

final class ConfigScreenLayout {
   static final int HEADER = 44;
   static final int FOOTER = 36;
   static final int ITEM_HEIGHT = 24;
   static final int SELECT_ITEM_HEIGHT = 30;
   static final int ROW_WIDTH = 280;
   static final int ROW_ICON = 16;
   static final int CONTROL_WIDTH = 96;
   static final int CONTROL_HEIGHT = 20;
   static final int ARROW_WIDTH = 12;
   static final int RESET_WIDTH = 20;
   static final int GAP = 4;
   static final int DESC_LINES_PER_ROW = 2;

   @Nullable
   static ResourceLocation reloadIcon(ConfigReloadType type) {
      return switch (type) {
         case WORLD_RELOAD -> MoonlightIcons.WORLD_RELOAD;
         case GAME_RESTART -> MoonlightIcons.GAME_RESTART;
         case NONE -> null;
      };
   }

   static ResourceLocation configFileIcon(ConfigType type) {
      return switch (type) {
         case CLIENT -> MoonlightIcons.CONFIG_CLIENT;
         case COMMON_SYNCED -> MoonlightIcons.CONFIG_SERVER;
         case COMMON -> MoonlightIcons.CONFIG_COMMON;
      };
   }

   static void drawClipped(GuiGraphics graphics, Font font, Component text, int minX, int y, int maxX, int color) {
      graphics.enableScissor(minX, y - 1, maxX, y + 9 + 1);
      graphics.drawString(font, text, minX, y, color);
      graphics.disableScissor();
   }
}
