package dev.corgitaco.enhancedcelestials2shaders.client;

import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import dev.corgitaco.enhancedcelestials2shaders.LunarShadersMod;
import dev.corgitaco.enhancedcelestials2shaders.api.LunarEventUtils;
import dev.corgitaco.enhancedcelestials2shaders.render.LunarColorRenderer;
import net.minecraft.world.level.Level;

public final class ClientLunarHandler {
   private static final ClientLunarHandler INSTANCE = new ClientLunarHandler();

   public static ClientLunarHandler getInstance() {
      return INSTANCE;
   }

   private ClientLunarHandler() {
   }

   public void onClientTick() {
      if (LunarShadersMod.isInitialized()) {
         Level clientLevel = LunarShadersMod.getPlatform().getClientLevel();
         if (clientLevel != null) {
            EnhancedCelestials.lunarForecastWorldData(clientLevel).ifPresent(this::logStateTransition);
         }
      }
   }

   private void logStateTransition(LunarForecast forecast) {
      if (forecast.didEventChangeThisTick()) {
         if (forecast.isEventActive()) {
            String eventName = forecast.currentLunarEventOrDefault()
               .unwrapKey()
               .map(key -> LunarEventUtils.formatEventName(key.location().toString()))
               .orElse("Unknown");
            LunarEventUtils.logInfo("[CLIENT] Lunar event detected: {} (blend: {}%)", eventName, (int)(forecast.getBlend() * 100.0F));
         } else {
            LunarEventUtils.logInfo("[CLIENT] Lunar event ended");
         }
      }
   }

   public void reset() {
      LunarColorRenderer.getInstance().reset();
      LunarEventUtils.logDebug("Client handler reset");
   }

   public String getDebugInfo() {
      Level clientLevel = LunarShadersMod.getPlatform().getClientLevel();
      LunarForecast forecast = clientLevel == null ? null : (LunarForecast)EnhancedCelestials.lunarForecastWorldData(clientLevel).orElse(null);
      StringBuilder sb = new StringBuilder();
      sb.append("=== Lunar Shaders Client Debug ===\n");
      sb.append("Initialized: ").append(LunarShadersMod.isInitialized()).append("\n");
      sb.append("Active: ").append(forecast != null && forecast.isEventActive()).append("\n");
      sb.append("Renderer: ").append(LunarColorRenderer.getInstance().getDebugInfo()).append("\n");
      return sb.toString();
   }
}
