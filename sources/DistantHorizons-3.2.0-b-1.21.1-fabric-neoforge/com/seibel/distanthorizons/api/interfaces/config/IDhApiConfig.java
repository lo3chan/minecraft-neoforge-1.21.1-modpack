package com.seibel.distanthorizons.api.interfaces.config;

import com.seibel.distanthorizons.api.interfaces.config.both.IDhApiWorldGenerationConfig;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiDebuggingConfig;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiGraphicsConfig;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiMultiThreadingConfig;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiMultiplayerConfig;

public interface IDhApiConfig {
   IDhApiGraphicsConfig graphics();

   IDhApiWorldGenerationConfig worldGenerator();

   IDhApiMultiplayerConfig multiplayer();

   IDhApiMultiThreadingConfig multiThreading();

   IDhApiDebuggingConfig debugging();
}
