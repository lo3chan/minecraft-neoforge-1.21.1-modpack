package com.aetherteam.cumulus.platform;

import com.aetherteam.cumulus.platform.services.IPlatformHelper;
import com.mojang.logging.LogUtils;
import java.util.ServiceLoader;
import org.slf4j.Logger;

public class Services {
   public static final Logger LOGGER = LogUtils.getLogger();
   public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

   public static <T> T load(Class<T> clazz) {
      T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
      LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }
}
