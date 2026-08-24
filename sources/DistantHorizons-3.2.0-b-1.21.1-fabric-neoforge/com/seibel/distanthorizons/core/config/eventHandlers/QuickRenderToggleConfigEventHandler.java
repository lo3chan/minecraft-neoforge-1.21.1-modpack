package com.seibel.distanthorizons.core.config.eventHandlers;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiRendererMode;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.listeners.ConfigChangeListener;

public class QuickRenderToggleConfigEventHandler {
   public static QuickRenderToggleConfigEventHandler INSTANCE = new QuickRenderToggleConfigEventHandler();
   private final ConfigChangeListener<Boolean> quickRenderChangeListener = new ConfigChangeListener<>(
      Config.Client.quickEnableRendering,
      val -> Config.Client.Advanced.Debugging.rendererMode
         .set(Config.Client.quickEnableRendering.get() ? EDhApiRendererMode.DEFAULT : EDhApiRendererMode.DISABLED)
   );
   private final ConfigChangeListener<EDhApiRendererMode> rendererModeChangeListener = new ConfigChangeListener<>(
      Config.Client.Advanced.Debugging.rendererMode,
      val -> Config.Client.quickEnableRendering.set(Config.Client.Advanced.Debugging.rendererMode.get() != EDhApiRendererMode.DISABLED)
   );

   private QuickRenderToggleConfigEventHandler() {
   }

   public void setUiOnlyConfigValues() {
      boolean enableRendering = Config.Client.Advanced.Debugging.rendererMode.get() != EDhApiRendererMode.DISABLED;
      Config.Client.quickEnableRendering.set(enableRendering);
   }
}
