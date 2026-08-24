package com.seibel.distanthorizons.core.api.external.methods.config.client;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiDebugRendering;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiDebuggingConfig;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.api.DhApiConfigValue;

public class DhApiDebuggingConfig implements IDhApiDebuggingConfig {
   public static DhApiDebuggingConfig INSTANCE = new DhApiDebuggingConfig();

   private DhApiDebuggingConfig() {
   }

   @Override
   public IDhApiConfigValue<EDhApiDebugRendering> debugRendering() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Debugging.debugRenderingColors);
   }

   @Override
   public IDhApiConfigValue<Boolean> debugKeybindings() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Debugging.enableDebugKeybindings);
   }

   @Override
   public IDhApiConfigValue<Boolean> renderWireframe() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Debugging.renderWireframe);
   }

   @Override
   public IDhApiConfigValue<Boolean> lodOnlyMode() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Debugging.lodOnlyMode);
   }

   @Override
   public IDhApiConfigValue<Boolean> debugWireframeRendering() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Debugging.DebugWireframe.enableRendering);
   }
}
