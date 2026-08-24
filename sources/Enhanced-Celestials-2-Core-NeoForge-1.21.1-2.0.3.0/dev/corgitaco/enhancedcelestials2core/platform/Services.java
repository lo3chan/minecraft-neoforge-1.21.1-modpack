package dev.corgitaco.enhancedcelestials2core.platform;

import dev.corgitaco.enhancedcelestials2core.core.EC2Constants;
import java.util.ServiceLoader;

public class Services {
   public static <T> T load(Class<T> clazz) {
      T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
      EC2Constants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }
}
