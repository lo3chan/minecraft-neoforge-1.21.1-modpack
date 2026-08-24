package io.github.maxencedc.sparsestructures.platform;

import io.github.maxencedc.sparsestructures.Constants;
import io.github.maxencedc.sparsestructures.platform.services.IPlatformHelper;
import java.util.ServiceLoader;

public class Services {
   public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

   public static <T> T load(Class<T> clazz) {
      T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
      Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }
}
