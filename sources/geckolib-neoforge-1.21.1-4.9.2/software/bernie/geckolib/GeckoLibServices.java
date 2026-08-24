package software.bernie.geckolib;

import java.util.ServiceLoader;
import software.bernie.geckolib.service.GeckoLibEvents;
import software.bernie.geckolib.service.GeckoLibNetworking;
import software.bernie.geckolib.service.GeckoLibPlatform;

public final class GeckoLibServices {
   public static final GeckoLibPlatform PLATFORM = load(GeckoLibPlatform.class);
   public static final GeckoLibNetworking NETWORK = load(GeckoLibNetworking.class);

   private static <T> T load(Class<T> clazz) {
      T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
      GeckoLibConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }

   public static class Client {
      public static final GeckoLibEvents EVENTS = GeckoLibServices.load(GeckoLibEvents.class);
      public static final software.bernie.geckolib.service.GeckoLibClient ITEM_RENDERING = GeckoLibServices.load(
         software.bernie.geckolib.service.GeckoLibClient.class
      );
   }
}
