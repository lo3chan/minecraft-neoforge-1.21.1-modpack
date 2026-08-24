package jeresources.platform;

import java.util.ServiceLoader;
import jeresources.util.LogHelper;

public class Services {
   public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

   public static <T> T load(Class<T> serviceClass) {
      T loadedService = ServiceLoader.load(serviceClass)
         .findFirst()
         .orElseThrow(() -> new NullPointerException("Failed to load service for " + serviceClass.getName()));
      LogHelper.debug("Loaded {} for service {}", loadedService, serviceClass);
      return loadedService;
   }
}
