package com.mrcrayfish.configured.impl.framework;

import com.mrcrayfish.framework.api.config.ConfigType;
import com.mrcrayfish.framework.config.FrameworkConfigManager.FrameworkConfigImpl;

public class FrameworkConfigHelper {
   public static boolean isWorldType(FrameworkConfigImpl config) {
      return config.getType() == ConfigType.WORLD || config.getType() == ConfigType.WORLD_SYNC;
   }
}
