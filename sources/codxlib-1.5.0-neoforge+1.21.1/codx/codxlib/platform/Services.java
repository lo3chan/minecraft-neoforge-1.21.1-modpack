package codx.codxlib.platform;

import codx.codxlib.neoforge.platform.NeoForgePlatformHelper;

public final class Services {
   public static final IPlatformHelper PLATFORM = load();

   private Services() {
   }

   private static IPlatformHelper load() {
      return new NeoForgePlatformHelper();
   }
}
