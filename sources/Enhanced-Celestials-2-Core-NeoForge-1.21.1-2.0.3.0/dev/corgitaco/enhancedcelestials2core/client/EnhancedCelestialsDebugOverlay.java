package dev.corgitaco.enhancedcelestials2core.client;

import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public final class EnhancedCelestialsDebugOverlay {
   private static final int PADDING = 4;
   private static final int LINE_HEIGHT = 10;
   private static final int LINE_COLOR = 14737632;
   private static boolean enabled = false;

   private EnhancedCelestialsDebugOverlay() {
   }

   public static void toggle() {
      enabled = !enabled;
   }

   public static boolean isEnabled() {
      return enabled;
   }

   public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
      if (enabled) {
         Minecraft minecraft = Minecraft.getInstance();
         ClientLevel level = minecraft.level;
         if (level != null) {
            List<Component> clientLines = EnhancedCelestials.lunarForecastWorldData(level)
               .map(LunarForecast::debugInfo)
               .orElse(List.of(Component.literal("Lunar events disabled in this dimension")));
            List<Component> serverLines = buildServerLines(minecraft, level);
            Font font = minecraft.font;
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int y = 4;

            for (Component line : clientLines) {
               guiGraphics.drawString(font, line, 4, y, 14737632);
               y += 10;
            }

            int var12 = 4;

            for (Component line : serverLines) {
               int width = font.width(line);
               guiGraphics.drawString(font, line, screenWidth - 4 - width, var12, 14737632);
               var12 += 10;
            }
         }
      }
   }

   private static List<Component> buildServerLines(Minecraft minecraft, ClientLevel clientLevel) {
      if (!minecraft.hasSingleplayerServer()) {
         return List.of(Component.literal("Not available (only when hosting singleplayer)"));
      } else {
         try {
            ServerLevel serverLevel = minecraft.getSingleplayerServer().getLevel(clientLevel.dimension());
            return serverLevel == null
               ? List.of(Component.literal("Server level not found"))
               : EnhancedCelestials.lunarForecastWorldData(serverLevel)
                  .map(LunarForecast::debugInfo)
                  .orElse(List.of(Component.literal("Lunar events disabled in this dimension")));
         } catch (Exception var3) {
            return List.of(Component.literal("Error reading server state: " + var3.getMessage()));
         }
      }
   }
}
