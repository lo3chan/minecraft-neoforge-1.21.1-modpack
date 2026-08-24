package com.seibel.distanthorizons.core.api.external.methods.config.client;

import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiAmbientOcclusionConfig;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.api.DhApiConfigValue;

public class DhApiAmbientOcclusionConfig implements IDhApiAmbientOcclusionConfig {
   public static DhApiAmbientOcclusionConfig INSTANCE = new DhApiAmbientOcclusionConfig();

   private DhApiAmbientOcclusionConfig() {
   }

   @Override
   public IDhApiConfigValue<Boolean> enabled() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Graphics.enableSsao);
   }
}
