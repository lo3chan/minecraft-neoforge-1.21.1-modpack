package com.seibel.distanthorizons.core.api.external.methods.config.client;

import com.seibel.distanthorizons.api.enums.config.EDhApiServerFolderNameMode;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;
import com.seibel.distanthorizons.api.interfaces.config.client.IDhApiMultiplayerConfig;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.api.DhApiConfigValue;

public class DhApiMultiplayerConfig implements IDhApiMultiplayerConfig {
   public static DhApiMultiplayerConfig INSTANCE = new DhApiMultiplayerConfig();

   private DhApiMultiplayerConfig() {
   }

   @Override
   public IDhApiConfigValue<EDhApiServerFolderNameMode> folderSavingMode() {
      return new DhApiConfigValue<>(Config.Client.Advanced.Multiplayer.serverFolderNameMode);
   }
}
