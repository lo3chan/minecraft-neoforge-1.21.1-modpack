package codx.codxlib.api;

import codx.codxlib.platform.Services;
import java.nio.file.Path;
import java.util.List;

public final class CodxLib {
   private CodxLib() {
   }

   public static String version(String modId) {
      return Services.PLATFORM.getModVersion(modId);
   }

   public static String minecraftVersion() {
      return Services.PLATFORM.getMinecraftVersion();
   }

   public static String loaderName() {
      return Services.PLATFORM.getLoaderName();
   }

   public static Path configDir() {
      return Services.PLATFORM.getConfigDir();
   }

   public static Path gameDir() {
      return Services.PLATFORM.getGameDir();
   }

   public static boolean isModLoaded(String modId) {
      return Services.PLATFORM.isModLoaded(modId);
   }

   public static List<LoadedMod> loadedMods() {
      return Services.PLATFORM.getLoadedMods();
   }

   public static Environment environment() {
      return Services.PLATFORM.getEnvironment();
   }

   public static boolean isClient() {
      return environment() == Environment.CLIENT;
   }
}
