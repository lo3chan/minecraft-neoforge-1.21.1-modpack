package com.seibel.distanthorizons.core.api.external.methods.config;

import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfig;
import com.seibel.distanthorizons.api.interfaces.config.both.IDhApiWorldGenerationConfig;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiDebuggingConfig;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiGraphicsConfig;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiMultiThreadingConfig;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiMultiplayerConfig;
import com.seibel.distanthorizons.core.api.external.methods.config.client.DhApiDebuggingConfig;
import com.seibel.distanthorizons.core.api.external.methods.config.client.DhApiGraphicsConfig;
import com.seibel.distanthorizons.core.api.external.methods.config.client.DhApiMultiThreadingConfig;
import com.seibel.distanthorizons.core.api.external.methods.config.client.DhApiMultiplayerConfig;
import com.seibel.distanthorizons.core.api.external.methods.config.common.DhApiWorldGenerationConfig;

public class DhApiConfig implements IDhApiConfig {
   public static final DhApiConfig INSTANCE = new DhApiConfig();

   private DhApiConfig() {
   }

   @Override
   public IDhApiGraphicsConfig graphics() {
      return DhApiGraphicsConfig.INSTANCE;
   }

   @Override
   public IDhApiWorldGenerationConfig worldGenerator() {
      return DhApiWorldGenerationConfig.INSTANCE;
   }

   @Override
   public IDhApiMultiplayerConfig multiplayer() {
      return DhApiMultiplayerConfig.INSTANCE;
   }

   @Override
   public IDhApiMultiThreadingConfig multiThreading() {
      return DhApiMultiThreadingConfig.INSTANCE;
   }

   @Override
   public IDhApiDebuggingConfig debugging() {
      return DhApiDebuggingConfig.INSTANCE;
   }
}
