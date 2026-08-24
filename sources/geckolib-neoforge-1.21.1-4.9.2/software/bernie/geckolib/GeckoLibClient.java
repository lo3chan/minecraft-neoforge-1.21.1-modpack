package software.bernie.geckolib;

import software.bernie.geckolib.cache.GeckoLibCache;

public final class GeckoLibClient {
   public static void init() {
      GeckoLibCache.registerReloadListener();
   }
}
